package com.stefanosiano.powerfulimageviewsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.stefanosiano.powerfulimageview.PowerfulImageView;
import com.stefanosiano.powerfulimageview.progress.PivProgressMode;

public class MainActivity extends AppCompatActivity {
    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3);/*
        final PowerfulImageView piw = (PowerfulImageView) findViewById(R.id.piv);
        piw.changeProgressMode(PivProgressMode.HORIZONTAL_DETERMINATE);


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
                progress += 5;
                Log.e("ASD", progress+"");
                piw.getProgressOptions().setValuePercent(progress);

                if(progress < 100)
                    piw.postDelayed(this, 300);
                else {
                    piw.postDelayed(runnable2, 1000);
                    piw.changeProgressMode(PivProgressMode.HORIZONTAL_INDETERMINATE);
                    //piw.setImageResource(R.drawable.wallpaper1);
                }
            }
        };

        piw.postDelayed(runnable, 1500);*/
    }
}
