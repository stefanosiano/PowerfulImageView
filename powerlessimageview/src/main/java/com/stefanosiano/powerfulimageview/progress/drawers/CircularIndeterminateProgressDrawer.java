package com.stefanosiano.powerfulimageview.progress.drawers;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.stefanosiano.powerfulimageview.progress.ProgressOptions;

/**
 * ProgressDrawer that shows an indeterminate animated circle as progress indicator.
 */

final class CircularIndeterminateProgressDrawer implements ProgressDrawer {


    /** Default animation duration */
    private final long DEFAULT_ANIMATION_DURATION = 800;

    /** Paint used to draw the arcs */
    private Paint mProgressPaint;

    /** Animator that rotates the whole circle */
    private ValueAnimator mOffsetAnimator;

    /** Animator that transforms the angles used to draw the progress */
    private ValueAnimator mProgressAnimator;

    /** Start angle of the arc */
    private int mProgressStartAngle;

    /** Sweep angle of the arc */
    private int mProgressSweepAngle;

    /** Whether the progress is shrinking or expanding. Used to adjust behaviour during animation */
    private boolean isShrinking;

    /** Custom animation duration. If it's less then 0, default duration is used */
    private long mProgressAnimationDuration = -1;

    /** offset of the start angle. It will change linearly continuously */
    private int mOffset;

    /** Last start angle offset. Used when calling setup(), so it doesn't change angle */
    private int mLastStartAngleOffset;

    /** Last sweep angle offset. Used when calling setup(), so it doesn't change angle */
    private int mLastSweepAngleOffset;

    /** Listener to handle things from the drawer */
    private ProgressDrawerManager.ProgressDrawerListener listener;

    /**
     * ProgressDrawer that shows an indeterminate animated circle as progress indicator.
     */
    CircularIndeterminateProgressDrawer() {
        this.mOffset = 0;
        this.isShrinking = false;
        this.mProgressStartAngle = -90;
        this.mProgressSweepAngle = 180;
        this.mLastStartAngleOffset = 0;
        this.mLastSweepAngleOffset = 0;
    }

    @Override
    public void setup(ProgressOptions progressOptions) {

        createAnimationIfNeeded();
        if(mProgressPaint == null) mProgressPaint = new Paint();

        mProgressPaint.setColor(progressOptions.getIndeterminateColor());
        mProgressPaint.setStrokeWidth(progressOptions.getCalculatedBorderWidth());
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);

        setProgressAngle(mLastStartAngleOffset, mLastSweepAngleOffset);
    }

    @Override
    public void startIndeterminateAnimation() {
        if(mOffsetAnimator != null)
            this.mOffsetAnimator.cancel();
        if(mProgressAnimator != null)
            this.mProgressAnimator.cancel();

        this.mOffset = 0;
        this.isShrinking = false;

        this.mLastStartAngleOffset = 0;
        this.mLastSweepAngleOffset = 0;
        setProgressAngle(mLastStartAngleOffset, mLastSweepAngleOffset);

        mProgressAnimator.start();
        mOffsetAnimator.start();
    }


    /**
     * Sets the offsets of the angles of the arcs that will be drawn
     * @param startAngleOffset Used when progress is shrinking (summing)
     * @param sweepAngleOffset Used when progress is shrinking (subtracting) or expanding (summing)
     */
    private void setProgressAngle(int startAngleOffset, int sweepAngleOffset) {
        mLastStartAngleOffset = startAngleOffset;
        mLastSweepAngleOffset = sweepAngleOffset;
        //mProgressSweepAngle when isShrinking and at the end of animation must be equal to itself when !isShrinking and at the beginning of the animation
        if(isShrinking) {
            this.mProgressStartAngle = -90 + startAngleOffset + mOffset;
            this.mProgressSweepAngle = 340 - sweepAngleOffset;//when sweepAngleOffset = 1 * 290 => 50.    when sweepAngleOffset = 0 * 290 => 340
        }
        else {
            this.mProgressStartAngle = -90 + mOffset;
            this.mProgressSweepAngle = sweepAngleOffset + 50;//when sweepAngleOffset = 0 * 290 => 50.   when sweepAngleOffset = 1 * 290 => 340
        }

        listener.onRequestInvalidate();
    }


    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mProgressStartAngle, mProgressSweepAngle, false, mProgressPaint);
    }

    @Override
    public void stopIndeterminateAnimation() {
        if(mOffsetAnimator != null)
            this.mOffsetAnimator.cancel();
        if(mProgressAnimator != null)
            this.mProgressAnimator.cancel();
    }

    @Override
    public void setProgressPercent(float progressPercent) {}

    @Override
    public void setAnimationEnabled(boolean enabled) {}

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
     * Creates the animator objects if and only if they are null
     */
    private void createAnimationIfNeeded(){

        if(mOffsetAnimator == null) {
            mOffsetAnimator = ValueAnimator.ofFloat(0f, 1f);
            mOffsetAnimator.setDuration(3000);
            mOffsetAnimator.setInterpolator(new LinearInterpolator());
            mOffsetAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    //Using animation.getAnimatedFraction() because animation.getAnimatedValue() leaks memory
                    mOffset = (int) (360 * animation.getAnimatedFraction());
                }
            });
        }

        if(mProgressAnimator == null) {
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
                public void onAnimationUpdate(ValueAnimator animation) {
                    //Using animation.getAnimatedFraction() because animation.getAnimatedValue() leaks memory
                    setProgressAngle((int) (360 * animation.getAnimatedFraction()), (int) (290 * animation.getAnimatedFraction()));
                }
            });
        }

    }
}
