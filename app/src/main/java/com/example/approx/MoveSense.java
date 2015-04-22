package com.example.approx;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;

import android.widget.Toast;


public class MoveSense {
    SensorManager sensorManager;
    Sensor sensorAccel;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast,sum;
    private Context context;
    public static int count = 0;

    private int strong;
    public float x, y, z;
    private int actionCode,dex;
    public static int act = 0;
    int time;
    CountDownTimer countDownTimer;
    public MoveSense(Context ctx,int str, int code, int dx){
        strong=str;
        dex = dx;
        time = 2000 + 61*(dex - 1);
        actionCode=code;//1-тряска 2-бросок
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        context=ctx;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_UI);  // это,короче, слушатели и время опроса датчиков
        countDownTimer = new CountDownTimer(time,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                     getAct();
                if (act ==1){
                    main.action = act;
                    mAccel=0;
                    count=0;
                    act = 0;
                    isUnregistred();
                    Toast.makeText(context,"Shake OK",Toast.LENGTH_SHORT).show();

                }
                if (act ==2){
                    main.action = act;
                    mAccel=0;
                    count=0;
                    act = 0;
                    isUnregistred();
                    Toast.makeText(context,"Drop OK",Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onFinish() {
                if (act == 0) {
                    main.action = act;
                    mAccel=0;
                    count=0;
                    act = 0;
                    isUnregistred();
                    Toast.makeText(context,"WASTED",Toast.LENGTH_SHORT).show();
                }

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

                    }
                    if (count == 18) {

                        act = 1;

                    }
                    break;
                case 2://бросок
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];
                    sum=x+y+z;
                    if (sum>strong*3||sum<-strong*3){
                        act = 2;
                }
                    break;
           }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };



    public boolean isUnregistred(){
        if (sensorManager != null){
        sensorManager.unregisterListener(listener);}
        if (sensorAccel !=null){
        sensorAccel=null;}
        if (countDownTimer !=null){
        countDownTimer.cancel();
        countDownTimer = null;}
        count = 0;
        return true;
    }

    public static int getAct() {

        return act;
    }

    public static int getCount() {

        return count;
    }

}
