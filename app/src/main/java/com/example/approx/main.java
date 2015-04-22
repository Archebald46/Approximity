package com.example.approx;


import android.os.Bundle;




import android.app.Activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import static android.widget.RadioGroup.OnCheckedChangeListener;


public class main extends Activity implements View.OnClickListener, OnCheckedChangeListener {
    Button str;
    Button gt;
    Button cln;
    Toast toast;
    RadioButton rShake;
    RadioButton rDrop;
    MoveSense moveSense;
    RadioGroup rg;
    EditText dexter;
    int dexSTR;
    int radiobtn;
    int i;
    public static int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        str = (Button) findViewById(R.id.button_start);
        gt = (Button) findViewById(R.id.button_get);
        cln = (Button) findViewById(R.id.button_clean);
        rShake = (RadioButton) findViewById(R.id.radioShake);
        rDrop = (RadioButton) findViewById(R.id.radioDrop);
        rg = (RadioGroup) findViewById(R.id.RG);
        dexter = (EditText) findViewById(R.id.dexterText);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
        str.setOnClickListener(this);
        gt.setOnClickListener(this);
        cln.setOnClickListener(this);
        rg.setOnCheckedChangeListener(this);
    }


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
                    if (dexter.getText().toString().equals("")){
                        toast.setText("Please,input dex");
                        toast.show();
                        break;
                    } else{
                    dexSTR = Integer.parseInt(dexter.getText().toString());

                    if (moveSense !=null){
                        moveSense.isUnregistred();}
                    moveSense = new MoveSense(this, 12, radiobtn,dexSTR);


                     break;}
                } else {
                    toast.setText("RB isn't active!");
                    toast.show();
                    break;
                }


            case R.id.button_get:

                i = moveSense.getCount();
                toast.setText("All: " + i);
                toast.show();
                break;

            case R.id.button_clean:
                rg.clearCheck();
                if (moveSense !=null){
                moveSense.isUnregistred();}
                radiobtn = 0;
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