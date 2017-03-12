package com.stefanosiano.progressimageview.progress;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

import com.stefanosiano.progressimageview.ProgressImageView;

/**
 * Created by stefano on 3/12/17.
 */

public class IndeterminateProgressDrawer implements ProgressDrawer {

    private final ProgressImageView piw;
    private Animation progressAnimation;
    private Paint mProgressPaint, mProgressRemainingPaint;
    private int mRemainingProgressStartAngle, mRemainingProgressSweepAngle, mProgressSweepAngle;
    private int[] mIndeterminateProgressColorArray;

    public IndeterminateProgressDrawer(ProgressImageView piw) {
        this.piw = piw;
    }

    public void setProgressAngle(int progressAngle) {
        int mProgressAngle = progressAngle;
        if(mProgressAngle > 360)
            mProgressAngle = mProgressAngle % 360;

        this.mRemainingProgressStartAngle = mProgressAngle - 90;
        this.mRemainingProgressSweepAngle = 360 - mProgressAngle;
        this.mProgressSweepAngle = mProgressAngle;
        this.piw.postInvalidate();
    }

    @Override
    public void init(int progressColor, int progressCircleBorderWidth, int remainingProgressColor, int[] indeterminateProgressColorArray) {

        createAnimationIfNeeded();
        if(mProgressPaint == null) mProgressPaint = new Paint();
        if(mProgressRemainingPaint == null) mProgressRemainingPaint = new Paint();

        mIndeterminateProgressColorArray = indeterminateProgressColorArray;
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressRemainingPaint.setColor(remainingProgressColor);
        mProgressRemainingPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressRemainingPaint.setAntiAlias(true);
        mProgressRemainingPaint.setStyle(Paint.Style.STROKE);

        piw.clearAnimation();
        piw.startAnimation(progressAnimation);
    }


    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mRemainingProgressStartAngle, mRemainingProgressSweepAngle, false, mProgressRemainingPaint);
        canvas.drawArc(progressBounds, -90, mProgressSweepAngle, false, mProgressPaint);
    }



    public void setProgressAnimationInterpolator (Interpolator interpolator){
        createAnimationIfNeeded();
        progressAnimation.setInterpolator(interpolator);
    }

    private void createAnimationIfNeeded(){

        if(progressAnimation != null)
            return;

        progressAnimation = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                setProgressAngle((int) (360 * interpolatedTime));
            }
        };
        progressAnimation.setRepeatCount(Animation.INFINITE);
        progressAnimation.setRepeatMode(Animation.INFINITE);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimation.setDuration(1000);
        progressAnimation.setAnimationListener(new Animation.AnimationListener() {
            int i = 0;
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                i = (i+1) % mIndeterminateProgressColorArray.length;
                int i1 = i;
                int i2 = i+1;
                i2 = i2 % mIndeterminateProgressColorArray.length;

                mProgressRemainingPaint.setColor(mIndeterminateProgressColorArray[i1]);
                mProgressPaint.setColor(mIndeterminateProgressColorArray[i2]);
            }
        });
    }
}
