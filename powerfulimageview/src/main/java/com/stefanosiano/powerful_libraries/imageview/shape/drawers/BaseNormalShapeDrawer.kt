package com.stefanosiano.powerful_libraries.imageview.shape.drawers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.stefanosiano.powerful_libraries.imageview.extensions.setBounds
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeScaleType
import com.stefanosiano.powerful_libraries.imageview.shape.ShapeOptions

internal abstract class BaseNormalShapeDrawer(drawable: Drawable?) : ShapeDrawer {

    /** Paint used to draw the shape background. */
    protected val mBackPaint = Paint()

    /** Paint used to draw the shape foreground. */
    protected val mFrontPaint = Paint()

    /** Paint used to draw the shape border. */
    protected val mBorderPaint = Paint()

    /** Matrix used to modify the canvas and draw. */
    private var mMatrix: Matrix = Matrix()

    /** Drawable to draw in the shape. */
    private var mDrawable: Drawable? = drawable

    /** Background drawable to draw under the shape. */
    private var mBackgroundDrawable: Drawable? = null

    /** Foreground drawable to draw over the shape. */
    private var mForegroundDrawable: Drawable? = null

    /** Scale type selected. */
    private var mScaleType: PivShapeScaleType? = null

    /** Solid color selected. */
    private var mSolidColor: Int? = null

    override fun changeDrawable(drawable: Drawable?) { this.mDrawable = drawable }

    override fun requireBitmap() = false

    override fun changeBitmap(bitmap: Bitmap?) {}

    override fun setMatrix(scaleType: PivShapeScaleType, matrix: Matrix) {
        this.mScaleType = scaleType
        this.mMatrix = matrix
    }

    override fun setup(shapeOptions: ShapeOptions) {
        mForegroundDrawable = shapeOptions.foregroundDrawable
        mBackgroundDrawable = shapeOptions.backgroundDrawable

        mBackPaint.color = shapeOptions.backgroundColor
        mBackPaint.isAntiAlias = true
        mBackPaint.style = Paint.Style.FILL

        mBorderPaint.color = shapeOptions.borderColor
        mBorderPaint.isAntiAlias = true
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.strokeWidth = shapeOptions.borderWidth.toFloat()

        mFrontPaint.color = shapeOptions.foregroundColor
        mFrontPaint.isAntiAlias = true
        mFrontPaint.style = Paint.Style.FILL

        mSolidColor = shapeOptions.solidColor
    }

    override fun draw(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) {
        // Background
        if (mBackPaint.color != Color.TRANSPARENT) drawPaint(canvas, shapeBounds, mBackPaint)

        // Image
        if (mDrawable != null) {
            // I save the state, apply the matrix and restore the state of the canvas
            val saveCount = canvas.saveCount

            // If scaleType is XY, we should draw the image on the whole view
            if (mScaleType == PivShapeScaleType.FIT_XY) {
                mDrawable?.setBounds(imageBounds)
            } else {
                canvas.save()
                canvas.concat(mMatrix)
            }

            mBackgroundDrawable?.bounds = mDrawable?.bounds ?: Rect()
            mBackgroundDrawable?.draw(canvas)

            mDrawable?.draw(canvas)

            mForegroundDrawable?.bounds = mDrawable?.bounds ?: Rect()
            mForegroundDrawable?.draw(canvas)

            if (mScaleType == null || mScaleType != PivShapeScaleType.FIT_XY) {
                canvas.restoreToCount(saveCount)
            }
        }

        // Foreground
        if (mFrontPaint.color != Color.TRANSPARENT) drawPaint(canvas, shapeBounds, mFrontPaint)

        // Border
        if (mBorderPaint.strokeWidth > 0 && mBorderPaint.color != Color.TRANSPARENT) {
            drawBorder(canvas, borderBounds, shapeBounds, imageBounds, mBorderPaint)
        }

        if (mSolidColor != Color.TRANSPARENT) {
            drawSolid(canvas, borderBounds, shapeBounds, imageBounds)
        }
    }

    protected abstract fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint)
    protected abstract fun drawBorder(
        canvas: Canvas,
        borderBounds: RectF,
        shapeBounds: RectF,
        imageBounds: RectF,
        borderPaint: Paint
    )
    protected abstract fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF)
}

/** ShapeDrawer that draws the drawable directly into the shape. */
internal class NormalShapeDrawer
/** ShapeDrawer that draws the drawable directly into the shape. */
(drawable: Drawable?) : BaseNormalShapeDrawer(drawable) {

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) = canvas.drawRect(shapeBounds, paint)

    override fun drawBorder(
        canvas: Canvas,
        borderBounds: RectF,
        shapeBounds: RectF,
        imageBounds: RectF,
        borderPaint: Paint
    ) = canvas.drawRect(borderBounds, borderPaint)

    override fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) {}
}
