package com.stefanosiano.powerfulimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.stefanosiano.powerfulimageview.progress.ProgressOptions;

/**
 * CancelDrawer that shows a cross as cancel progress indicator over a black background.
 */

final class CrossCancelDrawer implements CancelDrawer {

    /** Paint used to draw the background */
    private Paint mBackgroundPaint;

    /** Paint used to draw the cross */
    private Paint mPaint;

    @Override
    public void setup(ProgressOptions progressOptions) {
        if(mPaint == null) mPaint = new Paint();
        if(mBackgroundPaint == null) mBackgroundPaint = new Paint();

        mPaint.setColor(progressOptions.getFrontColor());
        mPaint.setStrokeWidth(progressOptions.getCalculatedBorderWidth());
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mBackgroundPaint.setColor(Color.parseColor("#a0000000"));
        mBackgroundPaint.setStrokeWidth(0);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas, RectF cancelBounds, RectF cancelIconBounds) {
        canvas.drawArc(cancelBounds, 0, 360, true, mBackgroundPaint);

        canvas.drawLine(cancelIconBounds.left, cancelIconBounds.top, cancelIconBounds.right, cancelIconBounds.bottom, mPaint);
        canvas.drawLine(cancelIconBounds.right, cancelIconBounds.top, cancelIconBounds.left, cancelIconBounds.bottom, mPaint);

    }
}
