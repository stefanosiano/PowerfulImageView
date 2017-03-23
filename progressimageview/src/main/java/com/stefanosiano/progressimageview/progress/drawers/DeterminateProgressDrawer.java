package com.stefanosiano.progressimageview.progress.drawers;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.stefanosiano.progressimageview.ProgressImageView;
import com.stefanosiano.progressimageview.progress.ProgressOptions;


/**
 * ProgressDrawer that shows a determinate circle as progress indicator.
 */

final class DeterminateProgressDrawer implements ProgressDrawer {


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



    /** Start angle of the back arc */
    private int mProgressBackStartAngle;

    /** Sweep angle of the back arc */
    private int mProgressBackSweepAngle;

    /** Real sweep angle of the front arc */
    private int mProgressFrontSweepAngle;

    /** Shown sweep angle of the front arc */
    private int mCurrentProgressFrontSweepAngle;

    /** Old angle of the front arc used to start animation from */
    private int mOldProgressFrontSweepAngle;

    /** Whether to animate the progress change or not */
    private boolean mUseProgressAnimation;

    /** Whether to draw wedges or simple arcs */
    private boolean drawWedge;

    /**
     * ProgressDrawer that shows a determinate circle as progress indicator.
     *
     * @param piv View
     * @param progressBounds Bounds to show the progress indicator into
     */
    DeterminateProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.mPiv = piv;
        this.mProgressBounds = progressBounds;
    }

    /**
     * Sets the angle that will be used to draw the arcs
     * @param progressAngle Angle used to calculate the front and back arcs
     */
    private void setRealProgressAngle(int progressAngle) {

        this.mProgressBackStartAngle = progressAngle - 90;
        this.mProgressBackSweepAngle = 360 - progressAngle;
        this.mCurrentProgressFrontSweepAngle = progressAngle;
        //invalidates only the area of the progress indicator, instead of the whole view. +1 e -1 are used to be sure to invalidate the whole progress indicator
        //It is more efficient then just postInvalidate(): if something is drawn outside the bounds, it will not be calculated again!
        this.mPiv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
    }

    @Override
    public void setProgressPercent(float progressPercent) {
        int mProgressAngle = (int) (progressPercent * 3.6f);
        if(mProgressAngle > 360)
            mProgressAngle = mProgressAngle % 360;

        //Saving last shown angle (will be used to animate, if needed)
        this.mOldProgressFrontSweepAngle = this.mCurrentProgressFrontSweepAngle;

        //Sets the value of the progress angle (the value the animation will go to)
        this.mProgressFrontSweepAngle = mProgressAngle;

        if(this.mUseProgressAnimation){
            //use the mProgressFrontSweepAngle to set the animation accordingly, after cancelling it.
            //The animation will go from mOldProgressFrontSweepAngle to mProgressFrontSweepAngle
            if(mProgressAnimator != null)
                mProgressAnimator.cancel();
            createAnimationIfNeeded();
            mProgressAnimator.start();
        }
        else {
            //sets the mProgressFrontSweepAngle as the real angle to show
            setRealProgressAngle(mProgressAngle);
        }
    }

    @Override
    public void setAnimationEnabled(boolean enabled){
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

        mProgressFrontSweepAngle = 0;
        mUseProgressAnimation = progressOptions.isDeterminateAnimationEnabled();
        drawWedge = progressOptions.isDrawWedge();

        if(mProgressFrontPaint == null) mProgressFrontPaint = new Paint();
        if(mProgressBackPaint == null) mProgressBackPaint = new Paint();

        mProgressFrontPaint.setColor(progressOptions.getFrontColor());
        mProgressFrontPaint.setStrokeWidth(progressOptions.getCalculatedBorderWidth());
        mProgressFrontPaint.setAntiAlias(true);
        mProgressFrontPaint.setStyle(drawWedge ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);
        mProgressBackPaint.setColor(progressOptions.getBackColor());
        mProgressBackPaint.setStrokeWidth(progressOptions.getCalculatedBorderWidth());
        mProgressBackPaint.setAntiAlias(true);
        mProgressBackPaint.setStyle(Paint.Style.STROKE);

        setProgressPercent(progressOptions.getValuePercent());
    }

    @Override
    public void startIndeterminateAnimation() {}

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mProgressBackStartAngle, mProgressBackSweepAngle, drawWedge, mProgressBackPaint);
        canvas.drawArc(progressBounds, -90, mCurrentProgressFrontSweepAngle, drawWedge, mProgressFrontPaint);
    }

    @Override
    public void stopIndeterminateAnimation() {}

    /** Returns the last angle shown (used as start in animation) */
    private int getOldSweepAngle(){
        return mOldProgressFrontSweepAngle;
    }

    /** Returns the right angle to show (used as goal in animation) */
    private int getSweepAngle(){
        return mProgressFrontSweepAngle;
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
                setRealProgressAngle((int) (getOldSweepAngle() + ((getSweepAngle() - getOldSweepAngle()) * animation.getAnimatedFraction())));
            }
        });
    }
}
