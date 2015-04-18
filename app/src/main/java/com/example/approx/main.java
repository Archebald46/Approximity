package com.example.approx;


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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        str = (Button) findViewById(R.id.button_start);
        gt = (Button) findViewById(R.id.button_get);
        cln = (Button) findViewById(R.id.button_clean);
        rShake = (RadioButton) findViewById(R.id.radioShake);
        rDrop = (RadioButton) findViewById(R.id.radioDrop);
        rg = (RadioGroup) findViewById(R.id.RG);
        dexter = (EditText) findViewById(R.id.dexterText);
        strength = (EditText) findViewById(R.id.strText);
        progressB = (ProgressBar) findViewById(R.id.progressBar);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        str.setOnClickListener(this);
        gt.setOnClickListener(this);
        cln.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this);
    }

 public static void setActionInt (int first, int second){

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
            progressB.incrementProgressBy(prog);}else{
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
        moveSense.isUnregistred();}

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                if (rShake.isChecked() || rDrop.isChecked()) {
                    if (dexter.getText().toString().equals("")||strength.getText().toString().equals("")){
                        toast.setText("Введи силу/ловкость от 1 до 100");
                        toast.show();
                        break;
                    } else{
                    dexSTR = Integer.parseInt(dexter.getText().toString());
                    strSTR = Integer.parseInt(strength.getText().toString());
                    if (moveSense !=null){
                        moveSense.isUnregistred();}
                        progressB.setProgress(0);
                    moveSense = new MoveSense(this, strSTR, radiobtn, dexSTR, progressB);


                     break;}
                } else {
                    toast.setText("Не выбрано действие");
                    toast.show();
                    break;
                }


            case R.id.button_get:

                i = moveSense.getCount();
                toast.setText("Кол-во встрясок: " + i);
                toast.show();
                break;

            case R.id.button_clean:
                rg.clearCheck();
                if (moveSense !=null){
                moveSense.isUnregistred();}
                radiobtn = 0;
                progressB.setProgress(0);
                break;
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        switch (checkedId) {
            case R.id.radioDrop:
                if (moveSense !=null){
                moveSense.isUnregistred();}
                radiobtn = 2;
                break;

            case R.id.radioShake:
                if (moveSense !=null){
                moveSense.isUnregistred();}
                radiobtn = 1;
                break;


        }
    }
}