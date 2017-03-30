package com.stefanosiano.powerfulimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.stefanosiano.powerfulimageview.progress.ProgressOptions;

/**
 * ShadowDrawer that shows a circular shadow background.
 */

final class CircularShadowDrawer implements ShadowDrawer {

    /** Paint used to draw the shadow */
    private Paint mShadowPaint;

    @Override
    public void setup(ProgressOptions progressOptions) {
        if(mShadowPaint == null) mShadowPaint = new Paint();

        mShadowPaint.setColor(progressOptions.getShadowColor());
        mShadowPaint.setStrokeWidth(0);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas, RectF shadowBounds) {
        canvas.drawArc(shadowBounds, 0, 360, true, mShadowPaint);
    }
}
