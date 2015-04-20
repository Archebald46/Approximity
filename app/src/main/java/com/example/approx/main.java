package com.example.approx;


import android.os.Bundle;




import android.app.Activity;


public class main extends Activity{

    MoveSense moveSense;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveSense = new MoveSense(this, 12, 2);
    }

    @Override
    protected void onDestroy() {
        moveSense.isUnregistred();
        super.onDestroy();
}
}
