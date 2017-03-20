package com.stefanosiano.progressimageview.progress;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.stefanosiano.progressimageview.ProgressImageView;

/**
 * Created by stefano on 3/18/17.
 */

public class IndeterminateHorizontalProgressDrawer implements ProgressDrawer {

    private final ProgressImageView piv;
    private final RectF mProgressBounds;

    private Paint mProgressPaint;
    private ValueAnimator mProgressAnimation;
    private float mStartX, mEndX;
    private boolean isShrinking;

    public IndeterminateHorizontalProgressDrawer(ProgressImageView piv, RectF progressBounds) {
        this.piv = piv;
        this.mProgressBounds = progressBounds;
        this.isShrinking = false;
    }


    @Override
    public void setProgressPercent(float progressPercent) {}

    @Override
    public void setup(ProgressOptions progressOptions) {

        createAnimationIfNeeded();
        if(mProgressPaint == null) mProgressPaint = new Paint();

        mProgressPaint.setColor(progressOptions.indeterminateColor);
        mProgressPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        this.isShrinking = false;
        setProgressValues(30);
    }

    private void setProgressValues(float currentX) {

        if(isShrinking) {
            this.mStartX = currentX;
            this.mEndX = mProgressBounds.right;
        }
        else {
            this.mStartX = mProgressBounds.left;
            this.mEndX = currentX;
        }
        this.piv.postInvalidate((int)mProgressBounds.left-1, (int)mProgressBounds.top-1, (int)mProgressBounds.right+1, (int)mProgressBounds.bottom+1);
    }

    @Override
    public void startIndeterminateAnimation() {
        if(mProgressAnimation != null)
            this.mProgressAnimation.cancel();

        this.isShrinking = false;
        setProgressValues(30);

        mProgressAnimation.start();
    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawRect(mStartX, progressBounds.top, mEndX, progressBounds.bottom, mProgressPaint);
    }

    @Override
    public void stopIndeterminateAnimation() {
        piv.clearAnimation();
        if(mProgressAnimation != null)
            mProgressAnimation.cancel();
    }

    private void createAnimationIfNeeded(){

        if(mProgressAnimation != null)
            return;

        mProgressAnimation = ValueAnimator.ofFloat(0f, 1f);
        mProgressAnimation.setDuration(1000);
        mProgressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mProgressAnimation.setRepeatCount(ValueAnimator.INFINITE);
        mProgressAnimation.addListener(new Animator.AnimatorListener() {
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
        mProgressAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgressValues(
                        mProgressBounds.left + ((mProgressBounds.right - mProgressBounds.left) * animation.getAnimatedFraction()));
            }
        });
    }
}

