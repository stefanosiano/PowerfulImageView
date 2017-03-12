package com.stefanosiano.progressimageview;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by stefano on 3/11/17.
 */

public class ProgressCircleAnimation extends Animation {

    private ProgressImageView piv;
    private int angle;

    public ProgressCircleAnimation(ProgressImageView piv) {
        this.piv = piv;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        angle = (int) (360 * interpolatedTime);
        piv.setProgressAngleForAnimation(angle);
    }
}