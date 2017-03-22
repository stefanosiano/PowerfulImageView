package com.stefanosiano.progressimageview.progress.drawers;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.stefanosiano.progressimageview.ProgressImageView;
import com.stefanosiano.progressimageview.progress.ProgressOptions;


/**
 * ProgressDrawer that shows a determinate bar as progress indicator.
 */

final class DeterminateHorizontalProgressDrawer implements ProgressDrawer {

    /** View used to draw the arcs onto */
    private final ProgressImageView mPiv;

    /** Bounds used to draw the arcs into */
    private final RectF mProgressBounds;

    /** Default animation duration */
    private final long DEFAULT_ANIMATION_DURATION = 100;

    /** Paint used to draw the front arc */
    private Paint mProgressFrontPaint;

    /** Paint used to draw the back arc */
    private Paint mProgressBackPaint;

    /** Animator that transforms the angles used to draw the progress */
    private ValueAnimator mProgressAnimator;

    /** Custom animation duration. If it's less then 0, default duration is used */
    private long mProgressAnimationDuration = -1;

    /** Whether to animate the progress change or not */
    private boolean mUseProgressAnimation;

    /** Real progress percentage of the front rectangle */
    private float mProgress;

    /** Shown progress percentage of the front rectangle */
    private float mCurrentProgress;

    /** Old progress percentage of the rectangle used to start animation from */
    private float mOldProgress;

    /** Shown progress x coordinate of the front rectangle */
    private float mCurrentFrontX;


    /**
     * ProgressDrawer that shows a determinate bar as progress indicator.
     *
     * @param piv View
     * @param progressBounds Bounds to show the progress indicator into
     */
    DeterminateHorizontalProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.mPiv = piv;
        this.mProgressBounds = progressBounds;
    }

    /**
     * Sets the progress that will be used to draw the rectangle
     * @param progress Progress used to calculate the front and back rectangles
     */
    private void setRealProgress(float progress) {
        this.mCurrentProgress = progress;
        this.mCurrentFrontX = mProgressBounds.left + ((mProgressBounds.right - mProgressBounds.left) * (progress/100));
        //invalidates only the area of the progress indicator, instead of the whole view. +1 e -1 are used to be sure to invalidate the whole progress indicator
        //It is more efficient then just postInvalidate(): if something is drawn outside the bounds, it will not be calculated again!
        this.mPiv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
    }


    @Override
    public void setProgressPercent(float progressPercent) {
        float mProgress = progressPercent;
        if(mProgress > 100)
            mProgress = mProgress % 100;

        //Saving last shown progress (will be used to animate, if needed)
        this.mOldProgress = mCurrentProgress;

        //Sets the value of the progress (the value the animation will go to)
        this.mProgress = mProgress;

        if(this.mUseProgressAnimation){
            //use the mProgress to set the animation accordingly, after cancelling it.
            //The animation will go from mOldProgress to mProgress
            if(mProgressAnimator != null)
                mProgressAnimator.cancel();
            createAnimationIfNeeded();
            mProgressAnimator.start();
        }
        else {
            //sets the mProgress as the real progress to show
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
        if(mProgressAnimator.isRunning()){
            mProgressAnimator.cancel();
            mProgressAnimator.start();
        }
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
    public void stopIndeterminateAnimation() {}

    /** Returns the last progress shown (used as start in animation) */
    private float getOldProgress(){
        return mOldProgress;
    }

    /** Returns the right progress to show (used as goal in animation) */
    private float getProgress(){
        return mProgress;
    }

    /**
     * Creates the animator objects if and only if it's null
     */
    private void createAnimationIfNeeded(){

        if(mProgressAnimator != null)
            return;

        mProgressAnimator = ValueAnimator.ofFloat(0f, 1f);
        mProgressAnimator.setInterpolator(new LinearInterpolator());
        mProgressAnimator.setDuration(mProgressAnimationDuration < 0 ? DEFAULT_ANIMATION_DURATION : mProgressAnimationDuration);
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //Using animation.getAnimatedFraction() because animation.getAnimatedValue() leaks memory
                setRealProgress(getOldProgress() + ((getProgress() - getOldProgress()) * animation.getAnimatedFraction()));
            }
        });
    }
}
