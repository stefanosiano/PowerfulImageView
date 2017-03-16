package com.stefanosiano.progressimageview.progress;

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

    private int mProgressBackStartAngle, mProgressBackSweepAngle, mProgressFrontSweepAngle, mCurrentProgressFrontSweepAngle, mOldProgressFrontSweepAngle;
    private Paint mProgressFrontPaint, mProgressBackPaint;
    private Animation mProgressAnimation;
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
            piv.clearAnimation();
            piv.startAnimation(mProgressAnimation);
        }
        else {
            setRealProgressAngle(mProgressAngle);
        }
    }

    public void setUseAnimation(boolean useAnimation){
        this.mUseProgressAnimation = useAnimation;
    }

    @Override
    public void init(int progressFrontColor, int progressCircleBorderWidth, int progressBackColor, int indeterminateProgressColor) {

        if(mProgressFrontPaint == null) mProgressFrontPaint = new Paint();
        if(mProgressBackPaint == null) mProgressBackPaint = new Paint();

        mProgressFrontPaint.setColor(progressFrontColor);
        mProgressFrontPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressFrontPaint.setAntiAlias(true);
        mProgressFrontPaint.setStyle(Paint.Style.STROKE);
        mProgressBackPaint.setColor(progressBackColor);
        mProgressBackPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressBackPaint.setAntiAlias(true);
        mProgressBackPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mProgressBackStartAngle, mProgressBackSweepAngle, false, mProgressBackPaint);
        canvas.drawArc(progressBounds, -90, mCurrentProgressFrontSweepAngle, false, mProgressFrontPaint);
    }

    @Override
    public void clear() {
        piv.clearAnimation();
        if(mProgressAnimation != null)
            mProgressAnimation.reset();
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

        mProgressAnimation = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                setRealProgressAngle((int) (getOldSweepAngle() + ((getSweepAngle() - getOldSweepAngle()) * interpolatedTime)));
            }
        };
        mProgressAnimation.setInterpolator(new LinearInterpolator());
        mProgressAnimation.setDuration(100);
    }
}
