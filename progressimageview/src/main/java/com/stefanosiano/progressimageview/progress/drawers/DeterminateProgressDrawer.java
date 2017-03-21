package com.stefanosiano.progressimageview.progress.drawers;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.stefanosiano.progressimageview.ProgressImageView;
import com.stefanosiano.progressimageview.progress.ProgressOptions;

/**
 * Created by stefano on 3/12/17.
 */

final class DeterminateProgressDrawer implements ProgressDrawer {

    private final ProgressImageView mPiv;
    private final RectF mProgressBounds;
    private final long DEFAULT_ANIMATION_DURATION = 100;

    private Paint mProgressFrontPaint, mProgressBackPaint;
    private ValueAnimator mProgressAnimator;
    private int mProgressBackStartAngle, mProgressBackSweepAngle, mProgressFrontSweepAngle, mCurrentProgressFrontSweepAngle, mOldProgressFrontSweepAngle;
    private boolean mUseProgressAnimation, drawWedge;
    private long mProgressAnimationDuration = -1;

    DeterminateProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.mPiv = piv;
        this.mProgressBounds = progressBounds;
    }

    private void setRealProgressAngle(int progressAngle) {

        this.mProgressBackStartAngle = progressAngle - 90;
        this.mProgressBackSweepAngle = 360 - progressAngle;
        this.mCurrentProgressFrontSweepAngle = progressAngle;
        this.mPiv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
    }

    @Override
    public void setProgressPercent(float progressPercent) {
        int mProgressAngle = (int) (progressPercent * 3.6f);
        if(mProgressAngle > 360)
            mProgressAngle = mProgressAngle % 360;

        this.mOldProgressFrontSweepAngle = this.mCurrentProgressFrontSweepAngle;
        this.mProgressFrontSweepAngle = mProgressAngle;

        if(this.mUseProgressAnimation){
            createAnimationIfNeeded();
            mProgressAnimator.start();
        }
        else {
            setRealProgressAngle(mProgressAngle);
        }
    }

    public void setAnimationEnabled(boolean enabled){
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

        mProgressFrontSweepAngle = 0;
        mUseProgressAnimation = progressOptions.isDeterminateAnimationEnabled();
        drawWedge = progressOptions.isDrawWedge();

        if(mProgressFrontPaint == null) mProgressFrontPaint = new Paint();
        if(mProgressBackPaint == null) mProgressBackPaint = new Paint();

        mProgressFrontPaint.setColor(progressOptions.getFrontColor());
        mProgressFrontPaint.setStrokeWidth(progressOptions.getBorderWidth());
        mProgressFrontPaint.setAntiAlias(true);
        mProgressFrontPaint.setStyle(drawWedge ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
        mProgressBackPaint.setColor(progressOptions.getBackColor());
        mProgressBackPaint.setStrokeWidth(progressOptions.getBorderWidth());
        mProgressBackPaint.setAntiAlias(true);
        mProgressBackPaint.setStyle(Paint.Style.STROKE);

        setProgressPercent(progressOptions.getValuePercent());
    }

    @Override
    public void startIndeterminateAnimation() {

    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mProgressBackStartAngle, mProgressBackSweepAngle, drawWedge, mProgressBackPaint);
        canvas.drawArc(progressBounds, -90, mCurrentProgressFrontSweepAngle, drawWedge, mProgressFrontPaint);
    }

    @Override
    public void stopIndeterminateAnimation() {
        if(mProgressAnimator != null)
            mProgressAnimator.cancel();
        mCurrentProgressFrontSweepAngle = mProgressFrontSweepAngle;
    }

    private int getOldSweepAngle(){
        return mOldProgressFrontSweepAngle;
    }

    private int getSweepAngle(){
        return mProgressFrontSweepAngle;
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
                setRealProgressAngle((int) (getOldSweepAngle() + ((getSweepAngle() - getOldSweepAngle()) * animation.getAnimatedFraction())));
            }
        });
    }
}
