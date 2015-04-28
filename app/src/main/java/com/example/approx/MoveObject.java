package com.example.approx;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MoveObject extends Activity {
    private SimulationView mView;
    private SensorManager mSensorManager;
    private WindowManager mWindowManager;
    private Display mDisplay;
    public static Bitmap b;
    public static Canvas c;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        mView = new SimulationView(this);
        setContentView(mView);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] srcPixels = new int[width* height];
        c = new Canvas(b);
        b.getPixels(srcPixels, 0, width, 0, 0, width, height);
        b.setPixel(100, 101, Color.BLACK);
        b.setPixel(101,101,Color.BLACK);
        b.setPixel(101,102,Color.BLACK);
        b.setPixel(103,102,Color.BLACK);
        b.setPixel(104,102,Color.BLACK);
        b.setPixel(100,100,Color.BLACK);
       mView.draw(c);
        Log.d("Coord", " fdf"+ srcPixels );

    }
    /*public static Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            v.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            int[] srcPixels = new int[b.getWidth()* b.getHeight()];
            b.getPixels(srcPixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
            Log.d("Coord", " fdf"+ srcPixels );
            return b;
        } else{
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        int[] srcPixels = new int[b.getWidth()* b.getHeight()];
        b.getPixels(srcPixels, 0, b.getWidth(), 0, 0, b.getWidth(), b.getHeight());
            Log.d("Coord", " fdf"+ srcPixels );
        return b;}
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        mView.startSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.stopSimulation();
    }


    class SimulationView extends View implements SensorEventListener {

        private static final float sBallDiameter = 0.004f;
        private static final float sBallDiameter2 = sBallDiameter * sBallDiameter;
        private Sensor mAccelerometer;
        private long mLastT;
        private float mLastDeltaT;
        private float mXDpi;
        private float mYDpi;
        private float mMetersToPixelsX;
        private float mMetersToPixelsY;
        private Bitmap mBitmap;
        private float mXOrigin;
        private float mYOrigin;
        private float mSensorX;
        private float mSensorY;
        private long mSensorTimeStamp;
        private long mCpuTimeStamp;
        private float mHorizontalBound;
        private float mVerticalBound;
        private final ParticleSystem mParticleSystem = new ParticleSystem();


        class Particle {
            private float mPosX;
            private float mPosY;
            private float mAccelX;
            private float mAccelY;
            private float mLastPosX;
            private float mLastPosY;
            private float mOneMinusFriction;


            public void computePhysics(float sx, float sy, float dT, float dTC) { //вычисление коррекции

                final float m = 4000.0f; // маса виртуального объекта
                final float gx = -sx * m;
                final float gy = -sy * m;

                final float invm = 1.0f / m; // второй закон Ньютона
                final float ax = gx * invm;
                final float ay = gy * invm;
                final float dTdT = dT * dT;
                final float x = mPosX + mOneMinusFriction * dTC * (mPosX - mLastPosX) + mAccelX  //временная коррекция по массе объекта
                        * dTdT;
                final float y = mPosY + mOneMinusFriction * dTC * (mPosY - mLastPosY) + mAccelY
                        * dTdT;
                mLastPosX = mPosX;
                mLastPosY = mPosY;
                mPosX = x;
                mPosY = y;
                mAccelX = ax;
                mAccelY = ay;
            }


            public void resolveCollisionWithBounds() { //коллизия шарика, дабы не убегал куда зря
                final float xmax = mHorizontalBound;
                final float ymax = mVerticalBound;
                final float x = mPosX;
                final float y = mPosY;
                if (x > xmax) {
                    mPosX = xmax;
                } else if (x < -xmax) {
                    mPosX = -xmax;
                }
                if (y > ymax) {
                    mPosY = ymax;
                } else if (y < -ymax) {
                    mPosY = -ymax;
                }
            }
        }


        class ParticleSystem {  // система для обновления координат самого шарика и ввыод его на хкран
            static final int NUM_PARTICLES = 1;
            private Particle mBalls[] = new Particle[NUM_PARTICLES];
            ParticleSystem() {
                mBalls[0] = new Particle();

            }


            private void updatePositions(float sx, float sy, long timestamp) { // обновление координат по времени, включая временную коррекцию
                final long t = timestamp;
                if (mLastT != 0) {
                    final float dT = (float) (t - mLastT) * (1.0f / 1000000000.0f);
                    if (mLastDeltaT != 0) {
                        final float dTC = dT / mLastDeltaT;
                        Particle ball = mBalls[0];
                        ball.computePhysics(sx, sy, dT, dTC);

                    }
                    mLastDeltaT = dT;
                }
                mLastT = t;
            }


            public void update(float sx, float sy, long now) {  //обновление координат системы (шара), дабы знать,где его рисовать

                updatePositions(sx, sy, now);



                Particle curr = mBalls[0];

                Particle ball = mBalls[0];
                float dx = ball.mPosX - curr.mPosX;
                float dy = ball.mPosY - curr.mPosY;
                float dd = dx * dx + dy * dy;
                // Check for collisions
                if (dd <= sBallDiameter2) {
                                  /*
                                   * add a little bit of entropy, after
                                   * nothing is perfect in the universe.
                                   */
                    dx += ((float) Math.random() - 0.5f) * 0.0001f;
                    dy += ((float) Math.random() - 0.5f) * 0.0001f;
                    dd = dx * dx + dy * dy;
                    // simulate the spring
                    final float d = (float) Math.sqrt(dd);
                    final float c = (0.5f * (sBallDiameter - d)) / d;
                    curr.mPosX -= dx * c;
                    curr.mPosY -= dy * c;
                    ball.mPosX += dx * c;
                    ball.mPosY += dy * c;

                    curr.resolveCollisionWithBounds(); // чтобы не было коллизий

                }
            }






        }

        public void startSimulation() {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        }

        public void stopSimulation() {
            mSensorManager.unregisterListener(this);
        }

        public SimulationView(Context context) {
            super(context);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mXDpi = metrics.xdpi;
            mYDpi = metrics.ydpi;
            mMetersToPixelsX = mXDpi / 0.0254f;
            mMetersToPixelsY = mYDpi / 0.0254f;

            // делаем шарик размером в 0.5 см на экране
            Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            final int dstWidth = (int) (sBallDiameter * mMetersToPixelsX + 0.5f);
            final int dstHeight = (int) (sBallDiameter * mMetersToPixelsY + 0.5f);
            mBitmap = Bitmap.createScaledBitmap(ball, dstWidth, dstHeight, true);

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            // оригиналшьные размеры экрана и шара
            mXOrigin = (w - mBitmap.getWidth()) * 0.5f;
            mYOrigin = (h - mBitmap.getHeight()) * 0.5f;
            mHorizontalBound = ((w / mMetersToPixelsX - sBallDiameter) * 0.5f);
            mVerticalBound = ((h / mMetersToPixelsY - sBallDiameter) * 0.5f);
        }



        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;


            switch (mDisplay.getOrientation()) {
                case Surface.ROTATION_0:
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    mSensorX = -event.values[1];
                    mSensorY = event.values[0];
                    break;
                case Surface.ROTATION_180:
                    mSensorX = -event.values[0];
                    mSensorY = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    mSensorX = event.values[1];
                    mSensorY = -event.values[0];
                    break;
            }

            mSensorTimeStamp = event.timestamp;
            mCpuTimeStamp = System.nanoTime();
        }

        @Override
        protected void onDraw(Canvas canvas) { //конкретно уже рисуем шар на экране в зависимости от позиции




            final ParticleSystem particleSystem = mParticleSystem;
            final long now = mSensorTimeStamp + (System.nanoTime() - mCpuTimeStamp);
            final float sx = mSensorX;
            final float sy = mSensorY;

            particleSystem.update(sx, sy, now);

            final float xc = mXOrigin;
            final float yc = mYOrigin;
            final float xs = mMetersToPixelsX;
            final float ys = mMetersToPixelsY;
            final Bitmap bitmap = mBitmap;
            final int count = 1;
            for (int i = 0; i < count; i++) {
                  /*
                   * We transform the canvas so that the coordinate system matches
                   * the sensors coordinate system with the origin in the center
                   * of the screen and the unit is the meter.
                   */

                final float x = xc + particleSystem.mBalls[0].mPosX * xs;
                final float y = yc - particleSystem.mBalls[0].mPosY * ys;
                canvas.drawBitmap(bitmap, x, y, null);

            }

            // and make sure to redraw asap
            invalidate();
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}
