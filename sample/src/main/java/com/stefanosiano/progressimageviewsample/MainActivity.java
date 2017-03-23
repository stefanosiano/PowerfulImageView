package com.stefanosiano.progressimageviewsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.stefanosiano.progressimageview.ProgressImageView;
import com.stefanosiano.progressimageview.progress.PivProgressMode;

public class MainActivity extends AppCompatActivity {
    int progress = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressImageView piw = (ProgressImageView) findViewById(R.id.piv);
        piw.changeProgressMode(PivProgressMode.DETERMINATE);

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progress += 15;
                Log.e("ASD", progress+"");
                piw.getProgressOptions().setValuePercent(progress);

                piw.postDelayed(this, 800);
            }
        };
        runnable.run();
    }
}
