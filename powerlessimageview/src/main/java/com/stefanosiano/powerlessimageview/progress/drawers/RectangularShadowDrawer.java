package com.stefanosiano.powerlessimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.stefanosiano.powerlessimageview.progress.ProgressOptions;

/**
 * ShadowDrawer that shows a rectangular shadow background.
 */

final class RectangularShadowDrawer implements ShadowDrawer {

    /** Paint used to draw the shadow */
    private Paint mShadowPaint;

    /** Paint used to draw the shadow border */
    private Paint mShadowBorderPaint;

    @Override
    public void setup(ProgressOptions progressOptions) {
        if(mShadowPaint == null) mShadowPaint = new Paint();
        if(mShadowBorderPaint == null) mShadowBorderPaint = new Paint();

        mShadowPaint.setColor(progressOptions.getShadowColor());
        mShadowPaint.setStrokeWidth(0);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mShadowBorderPaint.setColor(progressOptions.getShadowBorderColor());
        mShadowBorderPaint.setStrokeWidth(progressOptions.getShadowBorderWidth());
        mShadowBorderPaint.setAntiAlias(true);
        mShadowBorderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas, RectF shadowBorderBounds, RectF shadowBounds) {
        canvas.drawRect(shadowBounds.left, shadowBounds.top, shadowBounds.right, shadowBounds.bottom, mShadowPaint);
        canvas.drawRect(shadowBorderBounds.left, shadowBorderBounds.top, shadowBorderBounds.right, shadowBorderBounds.bottom, mShadowBorderPaint);
    }
}
