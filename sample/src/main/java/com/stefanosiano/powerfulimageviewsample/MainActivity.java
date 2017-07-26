package com.stefanosiano.powerfulimageviewsample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.stefanosiano.powerfulimageview.PowerfulImageView;
import com.stefanosiano.powerfulimageview.progress.PivProgressMode;


public class MainActivity extends Activity {
    int progress = 0;
    int progress2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        final PowerfulImageView piw = (PowerfulImageView) findViewById(R.id.piv);
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
        final PowerfulImageView piw2 = (PowerfulImageView) findViewById(R.id.piv2);
//        piw.changeProgressMode(PivProgressMode.CIRCULAR);
//        piw2.changeProgressMode(PivProgressMode.HORIZONTAL);

        seekBar.setMax(10);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                piw.changeBlurRadius(progress);
                piw2.changeBlurRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                piw.changeProgressMode(PivProgressMode.NONE);
                piw.setImageResource(R.drawable.sf1);
            }
        };

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progress += 18;
                piw.changeProgressValue(progress);

                if(progress < 100)
                    piw.postDelayed(this, 700);
                else {
                    piw.postDelayed(runnable2, 1200);
                    piw.changeProgressIndeterminate(true);
                }
            }
        };

/*

        final Runnable runnable4 = new Runnable() {
            @Override
            public void run() {
                piw2.changeProgressMode(PivProgressMode.NONE);
                piw2.setImageResource(R.drawable.sf1);
            }
        };

        final Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                progress2 += 18;
                piw2.changeProgressValue(progress2);

                if(progress2 < 100)
                    piw2.postDelayed(this, 800);
                else {
                    piw2.postDelayed(runnable4, 1200);
                    piw2.changeProgressIndeterminate(true);
                }
            }
        };
*/
//        piw.postDelayed(runnable, 2500);
        //piw.postDelayed(runnable3, 2500);
    }
}
