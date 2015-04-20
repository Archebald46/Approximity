package com.example.approx;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;


public class MoveSense {
    SensorManager sensorManager;
    Sensor sensorAccel;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast,sum;
    private Context context;
    public static int count = 0;
    int a=0;
    private int strong;
    public float x, y, z;
    private int actionCode;
    CountDownTimer countDownTimer;
    public MoveSense(Context ctx,int str, int code){
        strong=str;
        actionCode=code;//1-тряска 2-бросок
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        context=ctx;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_UI);  // это,короче, слушатели и время опроса датчиков
        countDownTimer = new CountDownTimer(6000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                if (actionCode==1){
                mAccel=0;
                count=0;
                }
                countDownTimer.start();
            }
        };
        countDownTimer.start();
    }
    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch(actionCode){
                case 1://тряска
                    z = event.values[2];
                    mAccelLast = mAccelCurrent;
                    mAccelCurrent=z;
                    float delta = mAccelCurrent - mAccelLast;
                    mAccel = mAccel * 0.9f + delta;
                    if (mAccel >strong) {
                        count++;
                        Log.e("",Float.toString(count));
                    }
                    if (count == 18) {
                        a++;
                        Log.e(Float.toString(a) + " " +Float.toString(mAccel) + " ", "МУТИТ");
                        Toast.makeText(context,"АЖТРЯСЕТ",Toast.LENGTH_SHORT).show();
                        mAccel=0.0f;
                        count = 0;
                    }
                    break;
                case 2://бросок
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];
                    sum=x+y+z;
                    if (sum>40||sum<-40){
                    Log.e(Float.toString(x) + " " +Float.toString(y) + " " +Float.toString(z) + " ", "БРОСОК");
                        Toast.makeText(context,"БРОСОК",Toast.LENGTH_SHORT).show();
                }
                    break;
           }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public boolean isUnregistred(){
        sensorManager.unregisterListener(listener);
        sensorManager=null;
        sensorAccel=null;
        countDownTimer.cancel();
        countDownTimer = null;
        return true;
    }
}
