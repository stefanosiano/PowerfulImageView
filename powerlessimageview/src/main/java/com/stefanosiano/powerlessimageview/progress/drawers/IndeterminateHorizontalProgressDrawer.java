package com.stefanosiano.powerlessimageview.progress.drawers;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.stefanosiano.powerlessimageview.progress.ProgressOptions;

/**
 * ProgressDrawer that shows an indeterminate animated bar as progress indicator.
 */
final class IndeterminateHorizontalProgressDrawer implements ProgressDrawer {
    
    
    /** Default animation duration */
    private final long DEFAULT_ANIMATION_DURATION = 1000;

    /** Left bound used to draw the rectangle */
    private float mLeft;

    /** Right bound used to draw the rectangle */
    private float mRight;

    /** Paint used to draw the rectangle */
    private Paint mProgressPaint;

    /** Animator that transforms the x coordinates used to draw the progress */
    private ValueAnimator mProgressAnimator;

    /** Start x coordinate of the rectangle */
    private float mStartX;

    /** End x coordinate of the rectangle */
    private float mEndX;

    /** Whether the progress is shrinking or expanding. Used to adjust behaviour during animation */
    private boolean isShrinking;

    /** Custom animation duration. If it's less then 0, default duration is used */
    private long mProgressAnimationDuration = -1;

    /** Listener to handle things from the drawer */
    private ProgressDrawerManager.ProgressDrawerListener listener;


    /**
     * ProgressDrawer that shows an indeterminate animated bar as progress indicator.
     */
    IndeterminateHorizontalProgressDrawer() {
        this.isShrinking = false;
        this.mStartX = 0;
        this.mEndX = 0;
    }


    @Override
    public void setProgressPercent(float progressPercent) {}

    @Override
    public void setAnimationEnabled(boolean enabled) {}

    @Override
    public void setup(ProgressOptions progressOptions) {

        createAnimationIfNeeded();
        if(mProgressPaint == null) mProgressPaint = new Paint();

        mProgressPaint.setColor(progressOptions.getIndeterminateColor());
        mProgressPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mLeft = progressOptions.getRect().left;
        mRight = progressOptions.getRect().right;
        setProgressValues(isShrinking ? mStartX : mEndX);
    }


    /**
     * Sets the x coordinate of the rectangle that will be drawn
     * @param currentX Used when progress is shrinking (as start) or expanding (as end)
     */
    private void setProgressValues(float currentX) {

        if(isShrinking) {
            this.mStartX = currentX;
            this.mEndX = mRight;
        }
        else {
            this.mStartX = mLeft;
            this.mEndX = currentX;
        }
        
        listener.onRequestInvalidate();
    }

    @Override
    public void startIndeterminateAnimation() {
        if(mProgressAnimator != null)
            this.mProgressAnimator.cancel();

        this.isShrinking = false;
        setProgressValues(mLeft);

        mProgressAnimator.start();
    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawRect(mStartX, progressBounds.top, mEndX, progressBounds.bottom, mProgressPaint);
    }

    @Override
    public void stopIndeterminateAnimation() {
        if(mProgressAnimator != null)
            mProgressAnimator.cancel();
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

    /**
     * Creates the animator objects if and only if it's null
     */
    private void createAnimationIfNeeded(){

        if(mProgressAnimator != null)
            return;

        mProgressAnimator = ValueAnimator.ofFloat(0f, 1f);
        mProgressAnimator.setDuration(mProgressAnimationDuration < 0 ? DEFAULT_ANIMATION_DURATION : mProgressAnimationDuration);
        mProgressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mProgressAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mProgressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {
                isShrinking = !isShrinking;
            }
        });
        mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //Using animation.getAnimatedFraction() because animation.getAnimatedValue() leaks memory
                setProgressValues(mLeft + ((mRight - mLeft) * animation.getAnimatedFraction()));
            }
        });
    }
}

