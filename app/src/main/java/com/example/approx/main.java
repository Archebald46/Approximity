package com.example.approx;


import android.content.Intent;
import android.os.Bundle;




import android.app.Activity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import static android.widget.RadioGroup.OnCheckedChangeListener;


public class main extends Activity implements View.OnClickListener, OnCheckedChangeListener {

    Button str;
    Button gt;
    Button cln;
    Button bl;
    public static Toast toast;
    RadioButton rShake;
    RadioButton rDrop;
    MoveSense moveSense;
    RadioGroup rg;
    EditText dexter;
    EditText strength;
    public static ProgressBar progressB;
    int dexSTR;
    int strSTR;
    int radiobtn;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   //установка полноэкранного режима для активити
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        str = (Button) findViewById(R.id.button_start);
        gt = (Button) findViewById(R.id.button_get);
        cln = (Button) findViewById(R.id.button_clean);
        bl = (Button) findViewById(R.id.button_ball);
        rShake = (RadioButton) findViewById(R.id.radioShake);     //регистрация используемых компонентов (кнопок и прочего)
        rDrop = (RadioButton) findViewById(R.id.radioDrop);
        rg = (RadioGroup) findViewById(R.id.RG);
        dexter = (EditText) findViewById(R.id.dexterText);
        strength = (EditText) findViewById(R.id.strText);
        progressB = (ProgressBar) findViewById(R.id.progressBar);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

        str.setOnClickListener(this); // установка слушателя на кнопки для обработки нажатий на них
        gt.setOnClickListener(this);
        cln.setOnClickListener(this);
        bl.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this); // слушатель для радиогруппы, в которой лежат радиобатоны
    }

 public static void setActionInt (int first, int second){   //получает данные из класса moveSense по завершении дейтвия, чтобы вывести определенный результат

     if (first ==1 && second==1){
           toast.setText("Тряска ОК! Действие: "+ first +" Стастус "+ second);
           toast.show();

     } else {
         if (first == 1 && second == 0) {
             toast.setText("Провалено! Действие: "+ first +" Статус: "+ second);
             toast.show();
             progressB.setProgress(0);
         }
     }

     if (first ==2 && second==2){
         toast.setText("Бросок ОК! Действие: "+ first +" Статус: "+ second);
         toast.show();

     } else {
         if (first == 2 && second == 0) {
             toast.setText("Провалено! Действие: "+ first +" Статус: "+ second);
             toast.show();
             progressB.setProgress(0);
         }
     }

 }


    /*public static void setProgressB (int prog) {
        if (progressB.getProgress()<100){
            progressB.incrementProgressBy(prog);}else{   //это еще один вариант юзания прогрессбара, но его не трогаем
            progressB.setProgress(100);
        }
    }*/


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (moveSense != null){
        moveSense.isUnregistred();} //снятие регистрации с акселерометра, дабы он не работал, когда не нужно

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:  // кнопка Старт
                if (rShake.isChecked() || rDrop.isChecked()) {
                    if (dexter.getText().toString().equals("")||strength.getText().toString().equals("")){ //если все заполнено и выбрано, то запускем сенсор
                        toast.setText("Введи силу/ловкость от 1 до 100");
                        toast.show();
                        break;
                    } else{
                    dexSTR = Integer.parseInt(dexter.getText().toString());  // получаем значения из эдитов
                    strSTR = Integer.parseInt(strength.getText().toString());
                    if (moveSense !=null){
                        moveSense.isUnregistred();}
                        progressB.setProgress(0); // обнуляем прогрессбар при запуске
                    moveSense = new MoveSense(this, strSTR, radiobtn, dexSTR, progressB); //передаем входные параметры (сила,действие,ловкость и прогрессбар) в класс moveSense для старта
                     break;}
                } else {
                    toast.setText("Не выбрано действие");
                    toast.show();
                    break;
                }


            case R.id.button_get: //кнопка Кол-во встрясок
                i = moveSense.getCount();
                toast.setText("Кол-во встрясок: " + i); // получаем количество встрясок телефона при действии "Тряска"
                toast.show();
                break;

            case R.id.button_clean: //кнопка Clean
                rg.clearCheck();
                if (moveSense !=null){
                moveSense.isUnregistred();} // сбрасывает выбор радиобатонов, обнуляет прогрессбар
                radiobtn = 0;
                progressB.setProgress(0);
                break;

            case R.id.button_ball: //кнопка Clean
                Intent intent = new Intent(getApplicationContext(), MoveObject.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) { // следит за изменениями в радиогруппе, то есть, за выбранным радиобатоном
        // TODO Auto-generated method stub
        switch (checkedId) {
            case R.id.radioDrop:
                if (moveSense !=null){
                moveSense.isUnregistred();} //передает 2, если выбран Бросок
                radiobtn = 2;
                break;

            case R.id.radioShake:
                if (moveSense !=null){
                moveSense.isUnregistred();} // передает 1, если выбрана Тряска
                radiobtn = 1;
                break;


        }
    }
}