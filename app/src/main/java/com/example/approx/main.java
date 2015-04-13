package com.example.approx;


import android.os.Bundle;



import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class main extends Activity {

        TextView tv;
        SensorManager sensorManager;
        Sensor sensorAccel;
        Sensor sensorMagnet;

        private float mAccel;
        private float mAccelCurrent;
        private float mAccelLast;
        public static int count = 0;
        public static Toast toast;


        private long lastUpdate = -1;
        private float x, y, zz;
        private float last_x, last_y, last_z;
        private static final int SHAKE_THRESHOLD = 800;

        StringBuilder sb = new StringBuilder();

        Timer timer;

        int rotation;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            tv = (TextView) findViewById(R.id.tv);
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mAccel = 0.00f;
            mAccelCurrent = SensorManager.GRAVITY_EARTH;
            mAccelLast = SensorManager.GRAVITY_EARTH;
            toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);


        }

        @Override
        protected void onResume () {
            super.onResume();
            sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);  // это,короче, слушатели и время опроса датчиков
            sensorManager.registerListener(listener, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);

            timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getDeviceOrientation();
                            getActualDeviceOrientation();   // текущая позиция тела
                            showInfo();
                        }
                    });
                }
            };
            timer.schedule(task, 0, 50);

            WindowManager windowManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
            Display display = windowManager.getDefaultDisplay();
            rotation = display.getRotation();

        }

        @Override
        protected void onPause () {
            super.onPause();
            sensorManager.unregisterListener(listener);
            timer.cancel();
        }

        String format ( float values[]){
            return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1], values[2]); // вывод на экран значений по осям
        }

    void showInfo() {
        sb.setLength(0);
        sb.append("Orientation : " + format(valuesResult))
                .append("\nOrientation 2: " + format(valuesResult2)) // тоже к выводу
        ;
        tv.setText(sb);
    }

    float[] r = new float[9];

    void getDeviceOrientation() {
        SensorManager.getRotationMatrix(r, null, valuesAccel, valuesMagnet);
        SensorManager.getOrientation(r, valuesResult);

        valuesResult[0] = (float) Math.toDegrees(valuesResult[0]);
        valuesResult[1] = (float) Math.toDegrees(valuesResult[1]);    // это значения,точнее, координаты по осям с датчика 0 - ось X, 1- Y, 2- Z
        valuesResult[2] = (float) Math.toDegrees(valuesResult[2]);
        return;
    }

    float[] inR = new float[9];
    float[] outR = new float[9];

    void getActualDeviceOrientation() {   // получение ориентации экрана
        SensorManager.getRotationMatrix(inR, null, valuesAccel, valuesMagnet);
        int x_axis = SensorManager.AXIS_X;
        int y_axis = SensorManager.AXIS_Y;
        switch (rotation) {
            case (Surface.ROTATION_0):
                break;
            case (Surface.ROTATION_90):
                x_axis = SensorManager.AXIS_Y;
                y_axis = SensorManager.AXIS_MINUS_X;
                break;
            case (Surface.ROTATION_180):
                y_axis = SensorManager.AXIS_MINUS_Y;
                break;
            case (Surface.ROTATION_270):
                x_axis = SensorManager.AXIS_MINUS_Y;
                y_axis = SensorManager.AXIS_X;
                break;
            default:
                break;
        }
        SensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);
        SensorManager.getOrientation(outR, valuesResult2);
        valuesResult2[0] = (float) Math.toDegrees(valuesResult2[0]);
        valuesResult2[1] = (float) Math.toDegrees(valuesResult2[1]);
        valuesResult2[2] = (float) Math.toDegrees(valuesResult2[2]);
        return;
    }

    float[] valuesAccel = new float[3];
    float[] valuesMagnet = new float[3];
    float[] valuesResult = new float[3];
    float[] valuesResult2 = new float[3];


    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                    }

                    float z = event.values[2];
                    mAccelLast = mAccelCurrent;
                    mAccelCurrent = (float) Math.sqrt((double) (z * z));  // получаем координаты и силу тряски, если они 6 раз больше 12, то выводим тост
                    float delta = mAccelCurrent - mAccelLast;
                    mAccel = mAccel * 0.9f + delta;
                    if (mAccel > 12) {
                        count++;
                    }
                    if (count == 6) {

                        toast.setText("Мутит,поц");
                        toast.show();
                        count = 0;
                    }



                    long curTime = System.currentTimeMillis(); // эта неведомая хрень с резким броском влево или право
                    if ((curTime - lastUpdate) > 600) {
                        lastUpdate = curTime;

                        x = event.values[0];

                        if(Round(x,4)>10.0000){
                            Log.d("sensor", "R: " + x);
                            toast.setText("Право");
                            toast.show();
                        }
                        else if(Round(x,4)<-15.0000){
                            Log.d("sensor", "L: " + x);
                            toast.setText("Лево");
                            toast.show();
                        }



                    }




                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:         // магнитный, его я не юзаю
                    for (int i = 0; i < 3; i++) {
                        valuesMagnet[i] = event.values[i];
                    }
                    break;
            }
        }
    };

public static float Round(float Rval, int Rpl) { // неведомая херня
        float p = (float)Math.pow(10,Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return tmp/p;
        }
}
