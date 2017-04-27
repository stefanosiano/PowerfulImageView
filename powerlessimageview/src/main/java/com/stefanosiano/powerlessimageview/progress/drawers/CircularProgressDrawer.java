package com.stefanosiano.powerlessimageview.progress.drawers;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.stefanosiano.powerlessimageview.progress.ProgressOptions;


/**
 * ProgressDrawer that shows a determinate circle as progress indicator.
 */

final class CircularProgressDrawer implements ProgressDrawer {


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

    /** Listener to handle things from the drawer */
    private ProgressDrawerManager.ProgressDrawerListener listener;

    /**
     * ProgressDrawer that shows a determinate circle as progress indicator.
     */
    CircularProgressDrawer() {
    }

    /**
     * Sets the angle that will be used to draw the arcs
     * @param progressAngle Angle used to calculate the front and back arcs
     */
    private void setRealProgressAngle(int progressAngle) {

        this.mProgressBackStartAngle = progressAngle - 90;
        this.mProgressBackSweepAngle = 360 - progressAngle;
        this.mCurrentProgressFrontSweepAngle = progressAngle;

        listener.onRequestInvalidate();
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
    public void setListener(ProgressDrawerManager.ProgressDrawerListener listener) {
        this.listener = listener;
    }

    @Override
    public void setup(ProgressOptions progressOptions) {

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
