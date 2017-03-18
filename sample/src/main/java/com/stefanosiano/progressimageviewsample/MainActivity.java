package com.stefanosiano.progressimageviewsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.stefanosiano.progressimageview.ProgressImageView;

public class MainActivity extends AppCompatActivity {
    int progress = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressImageView piw = (ProgressImageView) findViewById(R.id.piv);

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                progress += 10;
                piw.setProgressPercent(progress);

                piw.postDelayed(this, 1000);
            }
        };
        runnable.run();
    }
}
