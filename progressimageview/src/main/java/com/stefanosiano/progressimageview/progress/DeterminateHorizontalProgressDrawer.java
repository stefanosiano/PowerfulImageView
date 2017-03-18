package com.stefanosiano.progressimageview.progress;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.stefanosiano.progressimageview.ProgressImageView;

/**
 * Created by stefano on 3/18/17.
 */

public class DeterminateHorizontalProgressDrawer implements ProgressDrawer {

    private final ProgressImageView piv;
    private final RectF mProgressBounds;

    private Paint mProgressFrontPaint, mProgressBackPaint;
    private ValueAnimator mProgressAnimation;
    private float mProgress, mCurrentProgress, mOldProgress, mCurrentFrontX;
    private boolean mUseProgressAnimation;

    public DeterminateHorizontalProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.piv = piv;
        this.mProgressBounds = progressBounds;
    }

    private void setRealProgress(float progress) {
        this.mCurrentProgress = progress;
        this.mCurrentFrontX = mProgressBounds.left + ((mProgressBounds.right - mProgressBounds.left) * (progress/100));
        this.piv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
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
            mProgressAnimation.start();
        }
        else {
            setRealProgress(mProgress);
        }
    }

    public void setUseAnimation(boolean useAnimation){
        this.mUseProgressAnimation = useAnimation;
    }

    @Override
    public void setup(ProgressOptions progressOptions) {

        if(mProgressFrontPaint == null) mProgressFrontPaint = new Paint();
        if(mProgressBackPaint == null) mProgressBackPaint = new Paint();

        mProgressFrontPaint.setColor(progressOptions.frontColor);
        mProgressFrontPaint.setStrokeWidth(progressOptions.circleBorderWidth);
        mProgressFrontPaint.setAntiAlias(true);
        mProgressFrontPaint.setStyle(Paint.Style.STROKE);
        mProgressBackPaint.setColor(progressOptions.backColor);
        mProgressBackPaint.setStrokeWidth(progressOptions.circleBorderWidth);
        mProgressBackPaint.setAntiAlias(true);
        mProgressBackPaint.setStyle(Paint.Style.STROKE);

        mUseProgressAnimation = progressOptions.isDeterminateAnimationEnabled;
        setProgressPercent(progressOptions.valuePercent);
    }

    @Override
    public void startIndeterminateAnimation() {

    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawLine(mCurrentFrontX, progressBounds.bottom, progressBounds.right, progressBounds.bottom, mProgressBackPaint);
        canvas.drawLine(progressBounds.left, progressBounds.bottom, mCurrentFrontX, progressBounds.bottom, mProgressFrontPaint);
    }

    @Override
    public void stopIndeterminateAnimation() {
        piv.clearAnimation();
        if(mProgressAnimation != null)
            mProgressAnimation.cancel();
        mCurrentProgress = 0;
    }

    private float getOldProgress(){
        return mOldProgress;
    }

    private float getProgress(){
        return mProgress;
    }

    private void createAnimationIfNeeded(){

        if(mProgressAnimation != null)
            return;

        mProgressAnimation = ValueAnimator.ofFloat(0f, 1f);
        mProgressAnimation.setInterpolator(new LinearInterpolator());
        mProgressAnimation.setDuration(100);
        mProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setRealProgress(getOldProgress() + ((getProgress() - getOldProgress()) * animation.getAnimatedFraction()));
            }
        });
    }
}
