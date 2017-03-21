package com.stefanosiano.progressimageview.progress.drawers;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.stefanosiano.progressimageview.ProgressImageView;
import com.stefanosiano.progressimageview.progress.ProgressOptions;

/**
 * Created by stefano on 3/18/17.
 */

final class DeterminateHorizontalProgressDrawer implements ProgressDrawer {

    private final ProgressImageView mPiv;
    private final RectF mProgressBounds;
    private final long DEFAULT_ANIMATION_DURATION = 100;

    private Paint mProgressFrontPaint, mProgressBackPaint;
    private ValueAnimator mProgressAnimator;
    private float mProgress, mCurrentProgress, mOldProgress, mCurrentFrontX;
    private boolean mUseProgressAnimation;
    private long mProgressAnimationDuration = -1;

    DeterminateHorizontalProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.mPiv = piv;
        this.mProgressBounds = progressBounds;
    }

    private void setRealProgress(float progress) {
        this.mCurrentProgress = progress;
        this.mCurrentFrontX = mProgressBounds.left + ((mProgressBounds.right - mProgressBounds.left) * (progress/100));
        this.mPiv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
    }


    @Override
    public void setProgressPercent(float progressPercent) {
        float mProgress = progressPercent;
        if(mProgress > 100)
            mProgress = mProgress % 100;

        this.mOldProgress = mCurrentProgress;
        this.mProgress = mProgress;

        if(this.mUseProgressAnimation){
            createAnimationIfNeeded();
            mProgressAnimator.start();
        }
        else {
            setRealProgress(mProgress);
        }
    }

    @Override
    public void setAnimationEnabled(boolean enabled) {
        this.mUseProgressAnimation = enabled;
    }

    @Override
    public void setAnimationDuration(long millis) {
        this.mProgressAnimationDuration = millis;
        createAnimationIfNeeded();
        mProgressAnimator.setDuration(millis);
    }

    @Override
    public void setup(ProgressOptions progressOptions) {

        if(mProgressFrontPaint == null) mProgressFrontPaint = new Paint();
        if(mProgressBackPaint == null) mProgressBackPaint = new Paint();

        mProgressFrontPaint.setColor(progressOptions.getFrontColor());
        mProgressFrontPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mProgressBackPaint.setColor(progressOptions.getBackColor());
        mProgressBackPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mUseProgressAnimation = progressOptions.isDeterminateAnimationEnabled();
        setProgressPercent(progressOptions.getValuePercent());
    }

    @Override
    public void startIndeterminateAnimation() {

    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawRect(mCurrentFrontX, progressBounds.top, progressBounds.right, progressBounds.bottom, mProgressBackPaint);
        canvas.drawRect(progressBounds.left, progressBounds.top, mCurrentFrontX, progressBounds.bottom, mProgressFrontPaint);
    }

    @Override
    public void stopIndeterminateAnimation() {
        if(mProgressAnimator != null)
            mProgressAnimator.cancel();
        mCurrentProgress = 0;
    }

    private float getOldProgress(){
        return mOldProgress;
    }

    private float getProgress(){
        return mProgress;
    }

    private void createAnimationIfNeeded(){

        if(mProgressAnimator != null)
            return;

        mProgressAnimator = ValueAnimator.ofFloat(0f, 1f);
        mProgressAnimator.setInterpolator(new LinearInterpolator());
        mProgressAnimator.setDuration(mProgressAnimationDuration < 0 ? DEFAULT_ANIMATION_DURATION : mProgressAnimationDuration);
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setRealProgress(getOldProgress() + ((getProgress() - getOldProgress()) * animation.getAnimatedFraction()));
            }
        });
    }
}
