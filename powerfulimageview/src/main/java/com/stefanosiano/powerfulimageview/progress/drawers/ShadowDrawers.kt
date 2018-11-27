package com.stefanosiano.powerfulimageview.progress.drawers

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.stefanosiano.powerfulimageview.progress.ProgressOptions


/**
 * Interface that handles options, drawing and updating of the progress indicators shadow on the View.
 */

internal interface ShadowDrawer {

    /**
     * Initialize or updates all the variables needed to work.
     *
     * @param progressOptions Options to take values from
     */
    fun setup(progressOptions: ProgressOptions)


    /**
     * Draws the cancel progress indicator.
     * No operation should be performed here, except drawing, for efficiency.
     * No object creation, no allocation, no calculation and no if/else. Just draw.
     *
     * @param canvas Canvas of the View
     * @param shadowBounds Bounds of the progress indicator shadow
     */
    fun draw(canvas: Canvas, shadowBorderBounds: RectF, shadowBounds: RectF)
}


/**
 * Dummy progress drawer that doesn't do anything.
 * Used when cancel progress is disabled, so functions can be called without checks with no problem.
 */
internal class DummyShadowDrawer : ShadowDrawer {
    override fun setup(progressOptions: ProgressOptions) {}
    override fun draw(canvas: Canvas, shadowBorderBounds: RectF, shadowBounds: RectF) {}
}

/** ShadowDrawer that shows a circular shadow background */
internal class CircularShadowDrawer : ShadowDrawer {

    /** Paint used to draw the shadow  */
    private var mShadowPaint = Paint()

    /** Paint used to draw the shadow border  */
    private var mShadowBorderPaint = Paint()

    override fun setup(progressOptions: ProgressOptions) {
        mShadowPaint.color = progressOptions.shadowColor
        mShadowPaint.strokeWidth = 0f
        mShadowPaint.isAntiAlias = true
        mShadowPaint.style = Paint.Style.FILL

        mShadowBorderPaint.color = progressOptions.shadowBorderColor
        mShadowBorderPaint.strokeWidth = progressOptions.shadowBorderWidth
        mShadowBorderPaint.isAntiAlias = true
        mShadowBorderPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas, shadowBorderBounds: RectF, shadowBounds: RectF) {
        canvas.drawArc(shadowBounds, 0f, 360f, true, mShadowPaint)
        canvas.drawArc(shadowBorderBounds, 0f, 360f, false, mShadowBorderPaint)
    }
}

/** ShadowDrawer that shows a rectangular shadow background */
internal class RectangularShadowDrawer : ShadowDrawer {

    /** Paint used to draw the shadow  */
    private var mShadowPaint: Paint = Paint()

    /** Paint used to draw the shadow border  */
    private var mShadowBorderPaint: Paint = Paint()

    override fun setup(progressOptions: ProgressOptions) {
        mShadowPaint.color = progressOptions.shadowColor
        mShadowPaint.strokeWidth = 0f
        mShadowPaint.isAntiAlias = true
        mShadowPaint.style = Paint.Style.FILL

        mShadowBorderPaint.color = progressOptions.shadowBorderColor
        mShadowBorderPaint.strokeWidth = progressOptions.shadowBorderWidth
        mShadowBorderPaint.isAntiAlias = true
        mShadowBorderPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas, shadowBorderBounds: RectF, shadowBounds: RectF) {
        canvas.drawRect(shadowBounds.left, shadowBounds.top, shadowBounds.right, shadowBounds.bottom, mShadowPaint)
        canvas.drawRect(shadowBorderBounds.left, shadowBorderBounds.top, shadowBorderBounds.right, shadowBorderBounds.bottom, mShadowBorderPaint)
    }
}
