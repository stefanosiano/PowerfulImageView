package com.stefanosiano.progressimageview.progress.drawers;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.stefanosiano.progressimageview.ProgressImageView;
import com.stefanosiano.progressimageview.progress.ProgressOptions;

/**
 * ProgressDrawer that shows an indeterminate animated bar as progress indicator.
 */
final class IndeterminateHorizontalProgressDrawer implements ProgressDrawer {



    /** View used to draw the rectangle onto */
    private final ProgressImageView mPiv;

    /** Bounds used to draw the rectangle into */
    private final RectF mProgressBounds;

    /** Default animation duration */
    private final long DEFAULT_ANIMATION_DURATION = 1000;

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


    /**
     * ProgressDrawer that shows an indeterminate animated bar as progress indicator.
     *
     * @param piv View
     * @param progressBounds Bounds to show the progress indicator into
     */
    IndeterminateHorizontalProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.mPiv = piv;
        this.mProgressBounds = progressBounds;
        this.isShrinking = false;
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

        this.isShrinking = false;
        setProgressValues(30);
    }


    /**
     * Sets the x coordinate of the rectangle that will be drawn
     * @param currentX Used when progress is shrinking (as start) or expanding (as end)
     */
    private void setProgressValues(float currentX) {

        if(isShrinking) {
            this.mStartX = currentX;
            this.mEndX = mProgressBounds.right;
        }
        else {
            this.mStartX = mProgressBounds.left;
            this.mEndX = currentX;
        }
        //invalidates only the area of the progress indicator, instead of the whole view. +1 e -1 are used to be sure to invalidate the whole progress indicator
        //It is more efficient then just postInvalidate(): if something is drawn outside the bounds, it will not be calculated again!
        this.mPiv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
    }

    @Override
    public void startIndeterminateAnimation() {
        if(mProgressAnimator != null)
            this.mProgressAnimator.cancel();

        this.isShrinking = false;
        setProgressValues(30);

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
                setProgressValues(mProgressBounds.left + ((mProgressBounds.right - mProgressBounds.left) * animation.getAnimatedFraction()));
            }
        });
    }
}

