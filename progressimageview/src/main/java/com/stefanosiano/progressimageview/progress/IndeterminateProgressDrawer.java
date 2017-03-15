package com.stefanosiano.progressimageview.progress;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.stefanosiano.progressimageview.ProgressImageView;

/**
 * Created by stefano on 3/12/17.
 */

public class IndeterminateProgressDrawer implements ProgressDrawer {

    private final ProgressImageView piv;
    private final RectF mProgressBounds;
    private final int mSweepAngleChange = 40;

    private Animation mProgressAnimation;
    private Paint mProgressFrontPaint, mProgressBackPaint;
    private int mProgressBackStartAngle, mProgressBackSweepAngle, mProgressFrontStartAngle, mProgressFrontSweepAngle;
    private int[] mIndeterminateProgressColorArray;
    private int colorIndex;
    private int mLastStartSweep;

    public IndeterminateProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.piv = piv;
        this.mProgressBounds = progressBounds;
        this.colorIndex = 0;
        this.mLastStartSweep = 0;
    }

    private void setProgressAngle(int progressAngle, int frontChange) {
        this.mProgressFrontStartAngle = mLastStartSweep + -90 + frontChange;
        this.mProgressFrontSweepAngle = progressAngle - mLastStartSweep;
        this.mProgressBackStartAngle = (mProgressFrontStartAngle + mProgressFrontSweepAngle) % 360;
        this.mProgressBackSweepAngle = 360 - mProgressFrontSweepAngle;
        this.piv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
    }

    @Override
    public void init(int progressFrontColor, int progressCircleBorderWidth, int progressBackColor, int[] indeterminateProgressColorArray) {

        createAnimationIfNeeded();
        if(mProgressFrontPaint == null) mProgressFrontPaint = new Paint();
        if(mProgressBackPaint == null) mProgressBackPaint = new Paint();

        mIndeterminateProgressColorArray = indeterminateProgressColorArray;
        mProgressBackPaint.setColor(indeterminateProgressColorArray[0]);
        mProgressBackPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressBackPaint.setAntiAlias(true);
        mProgressBackPaint.setStyle(Paint.Style.STROKE);
        mProgressFrontPaint.setColor(indeterminateProgressColorArray[1]);
        mProgressFrontPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressFrontPaint.setAntiAlias(true);
        mProgressFrontPaint.setStyle(Paint.Style.STROKE);
        colorIndex = 0;
        setProgressAngle(0, 0);

        piv.clearAnimation();
        piv.startAnimation(mProgressAnimation);
    }


    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mProgressBackStartAngle, mProgressBackSweepAngle, false, mProgressBackPaint);
        canvas.drawArc(progressBounds, mProgressFrontStartAngle, mProgressFrontSweepAngle, false, mProgressFrontPaint);
    }

    @Override
    public void clear() {
        piv.clearAnimation();
        if(mProgressAnimation != null)
            mProgressAnimation.reset();
        colorIndex = 0;
    }


    private void createAnimationIfNeeded(){

        if(mProgressAnimation != null)
            return;

        mProgressAnimation = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                setProgressAngle(mLastStartSweep + (int) (360 * interpolatedTime), (int) (mSweepAngleChange*interpolatedTime));
            }
        };
        mProgressAnimation.setRepeatCount(Animation.INFINITE);
        mProgressAnimation.setRepeatMode(Animation.INFINITE);
        mProgressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mProgressAnimation.setDuration(1000);
        mProgressAnimation.setAnimationListener(new Animation.AnimationListener() {
            private int i1, i2;
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mLastStartSweep = (mLastStartSweep + mSweepAngleChange) % 360;
                colorIndex = (colorIndex + 1) % mIndeterminateProgressColorArray.length;
                i1 = colorIndex;
                i2 = (colorIndex + 1) % mIndeterminateProgressColorArray.length;

                mProgressBackPaint.setColor(mIndeterminateProgressColorArray[i1]);
                mProgressFrontPaint.setColor(mIndeterminateProgressColorArray[i2]);
            }
        });
    }
}
