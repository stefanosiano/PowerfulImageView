package com.stefanosiano.powerfulimageviewsample;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {
    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);/*
        final PowerfulImageView piw = (PowerfulImageView) findViewById(R.id.piv);
        piw.changeProgressMode(PivProgressMode.HORIZONTAL);


        final Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                piw.changeProgressMode(PivProgressMode.NONE);
                piw.setImageResource(R.drawable.wallpaper1);
            }
        };

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progress += 15;
                Log.e("ASD", progress+"");
                piw.getProgressOptions().setValuePercent(progress);

                if(progress < 100)
                    piw.postDelayed(this, 400);
                else {
                    piw.postDelayed(runnable2, 1000);
                    piw.changeProgressMode(PivProgressMode.HORIZONTAL_INDETERMINATE);
                    //piw.setImageResource(R.drawable.wallpaper1);
                }
            }
        };

        piw.postDelayed(runnable, 2500);*/
    }
}
