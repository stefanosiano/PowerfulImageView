package com.stefanosiano.progressimageview.progress;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.stefanosiano.progressimageview.ProgressImageView;

/**
 * Created by stefano on 3/12/17.
 */

public class DeterminateProgressDrawer implements ProgressDrawer {

    private final ProgressImageView piw;
    private int mRemainingProgressStartAngle, mRemainingProgressSweepAngle, mProgressSweepAngle;
    private Paint mProgressPaint, mProgressRemainingPaint;

    public DeterminateProgressDrawer(ProgressImageView piw) {
        this.piw = piw;
    }

    public void setProgressAngle(int progressAngle) {
        int mProgressAngle = progressAngle;
        if(mProgressAngle > 360)
            mProgressAngle = mProgressAngle % 360;

        this.mRemainingProgressStartAngle = mProgressAngle - 90;
        this.mRemainingProgressSweepAngle = 360 - mProgressAngle;
        this.mProgressSweepAngle = mProgressAngle;
        this.piw.postInvalidate();
    }

    @Override
    public void init(int progressColor, int progressCircleBorderWidth, int remainingProgressColor, int[] indeterminateProgressColorArray) {

        if(mProgressPaint == null) mProgressPaint = new Paint();
        if(mProgressRemainingPaint == null) mProgressRemainingPaint = new Paint();

        mProgressPaint.setColor(progressColor);
        mProgressPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressRemainingPaint.setColor(remainingProgressColor);
        mProgressRemainingPaint.setStrokeWidth(progressCircleBorderWidth);
        mProgressRemainingPaint.setAntiAlias(true);
        mProgressRemainingPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {
        canvas.drawArc(progressBounds, mRemainingProgressStartAngle, mRemainingProgressSweepAngle, false, mProgressRemainingPaint);
        canvas.drawArc(progressBounds, -90, mProgressSweepAngle, false, mProgressPaint);
    }
}
