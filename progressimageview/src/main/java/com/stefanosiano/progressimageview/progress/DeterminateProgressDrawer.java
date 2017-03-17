package com.stefanosiano.progressimageview.progress;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.stefanosiano.progressimageview.ProgressImageView;

/**
 * Created by stefano on 3/12/17.
 */

public class DeterminateProgressDrawer implements ProgressDrawer {

    private final ProgressImageView piv;
    private final RectF mProgressBounds;

    private Paint mProgressFrontPaint, mProgressBackPaint;
    private ValueAnimator mProgressAnimation;
    private int mProgressBackStartAngle, mProgressBackSweepAngle, mProgressFrontSweepAngle, mCurrentProgressFrontSweepAngle, mOldProgressFrontSweepAngle;
    private boolean mUseProgressAnimation;

    public DeterminateProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.piv = piv;
        this.mProgressBounds = progressBounds;
    }

    private void setRealProgressAngle(int progressAngle) {

        this.mProgressBackStartAngle = progressAngle - 90;
        this.mProgressBackSweepAngle = 360 - progressAngle;
        this.mCurrentProgressFrontSweepAngle = progressAngle;
        this.piv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
    }

    public void setProgressAngle(int progressAngle) {
        int mProgressAngle = progressAngle;
        if(mProgressAngle > 360)
            mProgressAngle = mProgressAngle % 360;

        this.mOldProgressFrontSweepAngle = this.mCurrentProgressFrontSweepAngle;
        this.mProgressFrontSweepAngle = mProgressAngle;

        if(this.mUseProgressAnimation){
            createAnimationIfNeeded();
            mProgressAnimation.start();
        }
        else {
            setRealProgressAngle(mProgressAngle);
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
    }

    @Override
    public void start() {

    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mProgressBackStartAngle, mProgressBackSweepAngle, false, mProgressBackPaint);
        canvas.drawArc(progressBounds, -90, mCurrentProgressFrontSweepAngle, false, mProgressFrontPaint);
    }

    @Override
    public void stop() {
        piv.clearAnimation();
        if(mProgressAnimation != null)
            mProgressAnimation.cancel();
        mCurrentProgressFrontSweepAngle = 0;
    }

    private int getOldSweepAngle(){
        return mOldProgressFrontSweepAngle;
    }

    private int getSweepAngle(){
        return mProgressFrontSweepAngle;
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
                setRealProgressAngle((int) (getOldSweepAngle() + ((getSweepAngle() - getOldSweepAngle()) * animation.getAnimatedFraction())));
            }
        });
    }
}
