package com.stefanosiano.progressimageview.progress;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.stefanosiano.progressimageview.ProgressImageView;

/**
 * Created by stefano on 3/12/17.
 */

public class DeterminateProgressDrawer implements ProgressDrawer {

    private final ProgressImageView piw;
    private int mRemainingProgressStartAngle, mRemainingProgressSweepAngle, mProgressSweepAngle, mCurrentProgressSweepAngle, mOldProgressSweepAngle;
    private Paint mProgressPaint, mProgressRemainingPaint;
    private Animation progressAnimation;
    private boolean mUseProgressAnimation;

    public DeterminateProgressDrawer(ProgressImageView piw) {
        this.piw = piw;
    }

    private void setRealProgressAngle(int progressAngle) {

        this.mRemainingProgressStartAngle = progressAngle - 90;
        this.mRemainingProgressSweepAngle = 360 - progressAngle;
        this.mCurrentProgressSweepAngle = progressAngle;
        this.piw.postInvalidate();
    }

    public void setProgressAngle(int progressAngle) {
        int mProgressAngle = progressAngle;
        if(mProgressAngle > 360)
            mProgressAngle = mProgressAngle % 360;

        this.mOldProgressSweepAngle = this.mCurrentProgressSweepAngle;
        this.mProgressSweepAngle = mProgressAngle;

        if(this.mUseProgressAnimation){
            createAnimationIfNeeded();
            piw.clearAnimation();
            piw.startAnimation(progressAnimation);
        }
        else {
            setRealProgressAngle(mProgressAngle);
        }
    }

    public void setUseAnimation(boolean useAnimation){
        this.mUseProgressAnimation = useAnimation;
    }

    @Override
    public void init(int progressColor, int progressCircleBorderWidth, int remainingProgressColor, int[] indeterminateProgressColorArray) {

        if(mProgressPaint == null) mProgressPaint = new Paint();
        if(mProgressRemainingPaint == null) mProgressRemainingPaint = new Paint();

        mProgressPaint.setColor(progressColor);
        mProgressPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressRemainingPaint.setColor(remainingProgressColor);
        mProgressRemainingPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressRemainingPaint.setAntiAlias(true);
        mProgressRemainingPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mRemainingProgressStartAngle, mRemainingProgressSweepAngle, false, mProgressRemainingPaint);
        canvas.drawArc(progressBounds, -90, mCurrentProgressSweepAngle, false, mProgressPaint);
    }

    @Override
    public void clear() {
        piw.clearAnimation();
        progressAnimation.reset();
        mCurrentProgressSweepAngle = 0;
    }

    private int getOldSweepAngle(){
        return mOldProgressSweepAngle;
    }

    private int getSweepAngle(){
        return mProgressSweepAngle;
    }

    private void createAnimationIfNeeded(){

        if(progressAnimation != null)
            return;

        progressAnimation = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                setRealProgressAngle((int) (getOldSweepAngle() + ((getSweepAngle() - getOldSweepAngle()) * interpolatedTime)));
            }
        };
        progressAnimation.setInterpolator(new LinearInterpolator());
        progressAnimation.setDuration(3000);
    }
}
