package com.stefanosiano.powerfulimageview.progress.drawers;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.stefanosiano.powerfulimageview.progress.ProgressOptions;


/**
 * ProgressDrawer that shows a determinate bar as progress indicator.
 */

final class HorizontalProgressDrawer implements ProgressDrawer {


    /** Default animation duration */
    private final long DEFAULT_ANIMATION_DURATION = 100;

    /** Left bound used to draw the rectangle */
    private float mLeft;

    /** Right bound used to draw the rectangle */
    private float mRight;

    /** Paint used to draw the front rectangle */
    private Paint mProgressFrontPaint;

    /** Paint used to draw the back rectangle */
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

    /** Whether to reverse the progress */
    private boolean mIsProgressReversed;

    /** Listener to handle things from the drawer */
    private ProgressDrawerManager.ProgressDrawerListener listener;


    /**
     * ProgressDrawer that shows a determinate bar as progress indicator.
     */
    HorizontalProgressDrawer() {
    }

    /**
     * Sets the progress that will be used to draw the rectangle
     * @param progress Progress used to calculate the front and back rectangles
     */
    private void setRealProgress(float progress) {
        this.mCurrentProgress = progress;
        this.mCurrentFrontX = mLeft + ((mRight - mLeft) * (progress/100));

        listener.onRequestInvalidate();
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
    public void setListener(ProgressDrawerManager.ProgressDrawerListener listener) {
        this.listener = listener;
    }

    @Override
    public void setup(ProgressOptions progressOptions) {

        if(mProgressFrontPaint == null) mProgressFrontPaint = new Paint();
        if(mProgressBackPaint == null) mProgressBackPaint = new Paint();

        mProgressFrontPaint.setColor(progressOptions.getFrontColor());
        mProgressFrontPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mProgressBackPaint.setColor(progressOptions.getBackColor());
        mProgressBackPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mLeft = progressOptions.getRect().left;
        mRight = progressOptions.getRect().right;
        mUseProgressAnimation = progressOptions.isDeterminateAnimationEnabled();

        mIsProgressReversed = progressOptions.isProgressReversed();

        setProgressPercent(progressOptions.getValuePercent());
    }

    @Override
    public void startIndeterminateAnimation() {

    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        if(!mIsProgressReversed) {
            canvas.drawRect(mCurrentFrontX, progressBounds.top, progressBounds.right, progressBounds.bottom, mProgressBackPaint);
            canvas.drawRect(progressBounds.left, progressBounds.top, mCurrentFrontX, progressBounds.bottom, mProgressFrontPaint);
        }
        else {
            canvas.drawRect(progressBounds.left, progressBounds.top, progressBounds.right - mCurrentFrontX, progressBounds.bottom, mProgressBackPaint);
            canvas.drawRect(progressBounds.right - mCurrentFrontX, progressBounds.top, progressBounds.right, progressBounds.bottom, mProgressFrontPaint);
        }
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
