package com.stefanosiano.progressimageview.progress;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import com.stefanosiano.progressimageview.ProgressImageView;

/**
 * Created by stefano on 3/12/17.
 */

public class IndeterminateProgressDrawer implements ProgressDrawer {

    private final ProgressImageView piv;
    private final RectF mProgressBounds;

    private Animation mProgressAnimation;
    private ValueAnimator mOffsetAnimator;
    private Paint mProgressFrontPaint, mProgressBackPaint;
    private int mProgressBackStartAngle, mProgressBackSweepAngle, mProgressFrontStartAngle, mProgressFrontSweepAngle;
    private int[] mIndeterminateProgressColorArray;
    private int colorIndex;
    private int mLastSweepAngle;
    private int offset, lastOffset, lastx;
    private boolean changed = false;

    public IndeterminateProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.piv = piv;
        this.mProgressBounds = progressBounds;
        this.colorIndex = 0;
        changed = false;
        offset = 0;
        lastx = 0;
    }

    private void setProgressAngle(int progressAngle, int frontChange) {
        //frontChange = 0;
        if(!changed) {
            this.mProgressFrontStartAngle = - 90 + frontChange + offset;
            this.mProgressFrontSweepAngle = progressAngle + 60;
            this.mProgressBackStartAngle = (mProgressFrontStartAngle + mProgressFrontSweepAngle) % 360;
            this.mProgressBackSweepAngle = 360 - mProgressFrontSweepAngle;
        }
        else {
            this.mProgressFrontStartAngle = -90 + progressAngle + frontChange + offset;
            this.mProgressFrontSweepAngle = mLastSweepAngle - progressAngle + frontChange + 90 - lastOffset;

            this.mProgressBackStartAngle = (mProgressFrontStartAngle + mProgressFrontSweepAngle) % 360;
            this.mProgressBackSweepAngle = 360 - mProgressFrontSweepAngle;
        }
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
        mOffsetAnimator.start();
    }


    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        //canvas.drawArc(progressBounds, mProgressBackStartAngle, mProgressBackSweepAngle, false, mProgressBackPaint);
        canvas.drawArc(progressBounds, mProgressFrontStartAngle, mProgressFrontSweepAngle, false, mProgressFrontPaint);
    }

    @Override
    public void clear() {
        piv.clearAnimation();
        if(mProgressAnimation != null)
            mProgressAnimation.reset();
        if(mOffsetAnimator != null)
            mOffsetAnimator.cancel();
        colorIndex = 0;
    }


    private void createAnimationIfNeeded(){

        if(mOffsetAnimator == null) {
            mOffsetAnimator = ValueAnimator.ofFloat(0f, 1f);
            mOffsetAnimator.setDuration(5000);
            mOffsetAnimator.setInterpolator(new LinearInterpolator());
            mOffsetAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    //offset = (int) (360 * (float) animation.getAnimatedValue());
                    //offset = 50;
                }
            });
        }

        if(mProgressAnimation == null) {

            mProgressAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    setProgressAngle(
                            changed ? (int) (300 * interpolatedTime) : (int) (240 * interpolatedTime),
                            changed ? (int) (60 * interpolatedTime) : (int) 0);
                }
            };
            mProgressAnimation.setRepeatCount(Animation.INFINITE);
            mProgressAnimation.setRepeatMode(Animation.INFINITE);
            mProgressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            mProgressAnimation.setDuration(2000);
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
                    changed = !changed;
                    lastOffset = offset;
                    if(changed)
                        lastx -= 30;
                    mLastSweepAngle = mProgressFrontSweepAngle + mProgressFrontStartAngle;
                    /*
                    colorIndex = (colorIndex + 1) % mIndeterminateProgressColorArray.length;
                    i1 = colorIndex;
                    i2 = (colorIndex + 1) % mIndeterminateProgressColorArray.length;

                    mProgressBackPaint.setColor(mIndeterminateProgressColorArray[i1]);
                    mProgressFrontPaint.setColor(mIndeterminateProgressColorArray[i2]);*/
                }
            });
        }
    }
}
