package com.example.approx;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.widget.ProgressBar;

    public class MoveSense {  // класс, который отвечает за обработку всех движений по акселерометру
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

    public MoveSense(Context ctx,int str, int code, int dx, ProgressBar progressBar){ // получаем входные данные, переданные из main
        PB = progressBar;
        strong=str;
        dex = dx;
        strn = 16 - ((strong -1)* 0.162); // вычисление количества встрясок, зависящего от силы персонажа
        strnI = (int) Math.round(strn); // округление до целого
        time = 2000 + 61*(dex - 1); // вычисление времени,отведенного на выполнение жесты, в зависимости от ловкости персонажа
        actionCode=code;//1-тряска 2-бросок

        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE); // регистрация и подключение к акселерометру
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mAccel = 0.00f;
        context=ctx;
        progress = (int) Math.round(100/(strnI+0.3));
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        sensorManager.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_UI);  // это,короче, слушатели и время опроса акселерометра
        countDownTimer = new CountDownTimer(time,500) {  // запускаем таймер, которые отсчитывает время, данное на выполнение жеста
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                if (act == 0) { // если действие не было выполнено
                    mAccel=0;
                    count=0;
                    main.setActionInt(actionCode,act); // отправляем данные в main о том, что действие провалено
                    //main.setProgressB(0);
                    PB.setProgress(0); // обнуляем прогрессбар
                    act = 0;
                    isUnregistred();  // разрегистрируем акселерометр
                }

            }
        };
        countDownTimer.start();
    }
    SensorEventListener listener = new SensorEventListener() {  //слушатель для самого акселерометра
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch(actionCode){ //выполнение действия в зависимости от выбранного радиобатона
                case 1: //тряска
                    z = event.values[2];
                    mAccelLast = mAccelCurrent;
                    mAccelCurrent=z;
                    float delta = mAccelCurrent - mAccelLast;  // суть всего: расчет разницы между начальным ускороение и конечным по оси Z при тряске
                    mAccel = mAccel * 0.9f + delta;
                    if (mAccel >8) { //вместо стронг стоит число 8, то есть сила конкретная; если разница > 8, то срабатывает счетчик
                        count++; // отсчитывает требуемое количество встрясок
                        //main.setProgressB(progress);

                        if (PB.getProgress()<100){
                            PB.incrementProgressBy(progress);}else{ // отвечает за заполнение прогрессбара в процессе тряски
                            PB.setProgress(100);}
                    }
                    if (count == strnI) { //если треуемое число встрясок достигнуто, то останавливаем сенсор
                        act = 1;
                        mAccel=0;
                        count=0;
                        main.setActionInt(actionCode,act); // передаем данные в main об успешном выполнении
                        act = 0;
                        isUnregistred();
                    }
                    break;
                case 2: //бросок
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];
                    sum=x+y+z; //собираем данные об ускорении со всех осей и вычиссляем их сумму
                    if (sum>8*3||sum<-8*3){ //вместо стронг стоит число 8, то есть сила конкретная; если эта сумма больше заданного чила, то действие выполнено
                        act = 2;
                        mAccel=0;
                        count=0;
                        main.setActionInt(actionCode,act); // передаем данные в main об успешном выполнении дейтвия
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



    public boolean isUnregistred(){ // разрегистрация акселерометра
        if (sensorManager != null){
        sensorManager.unregisterListener(listener);}
        if (sensorAccel !=null){
        sensorAccel=null;}

        if (countDownTimer !=null){ //обнуление таймера
        countDownTimer.cancel();
        countDownTimer = null;}
        count = 0;
        return true;
    }


    public static int getCount() { // метод для получения количества сделанных встрясок в любой момент

        return count;
    }

}
