package com.example.approx;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;

import android.widget.ProgressBar;



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
    int time,strnI, progress;
    double strn;
    ProgressBar PB;

    CountDownTimer countDownTimer;
    public MoveSense(Context ctx,int str, int code, int dx, ProgressBar progressBar){
        PB = progressBar;
        strong=str;
        dex = dx;
        strn = 16 - ((strong -1)* 0.061);
        strnI = (int) Math.round(strn);
        time = 2000 + 61*(dex - 1);
        actionCode=code;//1-тряска 2-бросок
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.00f;
        context=ctx;
        progress = (int) Math.round(100/(strnI+0.3));
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_UI);  // это,короче, слушатели и время опроса датчиков
        countDownTimer = new CountDownTimer(time,500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                if (act == 0) {
                    mAccel=0;
                    count=0;
                    main.setActionInt(actionCode,act);
                    //main.setProgressB(0);
                    PB.setProgress(0);
                    act = 0;
                    isUnregistred();
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
                    if (mAccel >12) { //вместо стронг стоит число 12, то есть сила конкретная
                        count++;
                        //main.setProgressB(progress);
                        if (PB.getProgress()<100){
                            PB.incrementProgressBy(progress);}else{
                            PB.setProgress(100);}
                    }
                    if (count == strnI) {
                        act = 1;
                        mAccel=0;
                        count=0;
                        main.setActionInt(actionCode,act);
                        act = 0;
                        isUnregistred();
                    }
                    break;
                case 2://бросок
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];
                    sum=x+y+z;
                    if (sum>10*3||sum<-12*3){ //вместо стронг стоит число 12, то есть сила конкретная
                        act = 2;
                        mAccel=0;
                        count=0;
                        main.setActionInt(actionCode,act);
                        act = 0;
                        isUnregistred();
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


    public static int getCount() {

        return count;
    }

}
