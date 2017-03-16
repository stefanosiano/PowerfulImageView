package com.stefanosiano.progressimageview.progress;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.stefanosiano.progressimageview.ProgressImageView;

/**
 * Created by stefano on 3/12/17.
 */

public class IndeterminateProgressDrawer implements ProgressDrawer {

    private final ProgressImageView piv;
    private final RectF mProgressBounds;

    private ValueAnimator mOffsetAnimator, mProgressAnimator;
    private Paint mProgressPaint;
    private int mProgressStartAngle, mProgressSweepAngle;
    private int offset;
    private boolean isShrinking;

    public IndeterminateProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.piv = piv;
        this.mProgressBounds = progressBounds;
        this.offset = 0;
        this.isShrinking = false;
        this.mProgressStartAngle = -90;
        this.mProgressSweepAngle = 180;
    }

    @Override
    public void init(int progressFrontColor, int progressCircleBorderWidth, int progressBackColor, int indeterminateProgressColor) {

        createAnimationIfNeeded();
        if(mProgressPaint == null) mProgressPaint = new Paint();

        mProgressPaint.setColor(indeterminateProgressColor);
        mProgressPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);

        setProgressAngle(0, 0);

        mProgressAnimator.start();
        mOffsetAnimator.start();
    }


    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mProgressStartAngle, mProgressSweepAngle, false, mProgressPaint);
    }

    @Override
    public void clear() {
        if(mProgressAnimator != null)
            this.mProgressAnimator.cancel();
        if(mProgressAnimator != null)
            this.mProgressAnimator.cancel();

        this.offset = 0;
        this.isShrinking = false;
        this.mProgressStartAngle = -90;
        this.mProgressSweepAngle = 180;
    }


    private void createAnimationIfNeeded(){

        if(mOffsetAnimator == null) {
            mOffsetAnimator = ValueAnimator.ofFloat(0f, 1f);
            mOffsetAnimator.setDuration(3000);
            mOffsetAnimator.setInterpolator(new LinearInterpolator());
            mOffsetAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    offset = (int) (360 * (float) animation.getAnimatedValue());
                }
            });
        }

        if(mProgressAnimator == null) {
            mProgressAnimator = ValueAnimator.ofFloat(0f, 1f);
            mProgressAnimator.setDuration(1000);
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
                    setProgressAngle((int) (360 * (float) animation.getAnimatedValue()), (int) (290 * (float) animation.getAnimatedValue()));
                }
            });
        }

    }

    private void setProgressAngle(int startAngleOffset, int sweepAngleOffset) {
        if(isShrinking) {
            this.mProgressStartAngle = -90 + startAngleOffset + offset;
            this.mProgressSweepAngle = 340 - sweepAngleOffset;
        }
        else {
            this.mProgressStartAngle = -90 + offset;
            this.mProgressSweepAngle = sweepAngleOffset + 50;
        }
        this.piv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
    }
}
