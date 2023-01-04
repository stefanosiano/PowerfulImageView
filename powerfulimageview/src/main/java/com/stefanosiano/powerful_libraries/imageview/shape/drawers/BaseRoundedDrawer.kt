package com.stefanosiano.powerful_libraries.imageview.shape.drawers

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import com.stefanosiano.powerful_libraries.imageview.extensions.setBounds
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeScaleType
import com.stefanosiano.powerful_libraries.imageview.shape.ShapeOptions
import java.lang.ref.WeakReference

/** ShapeDrawer that draws an oval as shape. */
internal abstract class BaseRoundedDrawer(bitmap: Bitmap?) : ShapeDrawer {

    /** Shader to efficiently draw the shape. */
    private var mBitmapShader: BitmapShader? = null

    /** Paint used to draw the image. */
    private var mBitmapPaint = Paint()

    /** Paint used to draw the shape background. */
    private var mBackPaint = Paint()

    /** Paint used to draw the shape border. */
    private var mBorderPaint = Paint()

    /** Paint used to draw the shape foreground. */
    private var mFrontPaint = Paint()

    /** Background drawable to draw under the shape. */
    private var mBackgroundDrawable: Drawable? = null

    /** Foreground drawable to draw over the shape. */
    private var mForegroundDrawable: Drawable? = null

    /** Matrix used to draw the shape. */
    private var mMatrix: Matrix? = null

    /** Matrix used to draw the shape. */
    private var mOrigBitmap: WeakReference<Bitmap>? = null

    init {
        if (bitmap != null && !bitmap.isRecycled) {
            mOrigBitmap = WeakReference(bitmap)
            this.mBitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mBitmapShader?.setLocalMatrix(mMatrix)
        }

        mBitmapPaint.shader = mBitmapShader
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.style = Paint.Style.FILL
    }

    override fun changeDrawable(drawable: Drawable?) {}

    override fun requireBitmap(): Boolean = true

    override fun changeBitmap(bitmap: Bitmap?) {
        this.mBitmapShader = null
        mOrigBitmap?.clear()
        if (bitmap != null && !bitmap.isRecycled) {
            mOrigBitmap = WeakReference(bitmap)
            this.mBitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mBitmapShader?.setLocalMatrix(mMatrix)
        }

        mBitmapPaint.shader = mBitmapShader
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.style = Paint.Style.FILL
    }

    override fun setMatrix(scaleType: PivShapeScaleType, matrix: Matrix) {
        this.mMatrix = matrix
        mBitmapShader?.setLocalMatrix(matrix)
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
    }

    override fun draw(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) {
        // Background
        if (mBackPaint.color != Color.TRANSPARENT) drawPaint(canvas, shapeBounds, mBackPaint)

        mBackgroundDrawable?.setBounds(imageBounds)
        mBackgroundDrawable?.draw(canvas)

        // Image
        if (mOrigBitmap?.get()?.isRecycled != true) {
            drawPaint(canvas, imageBounds, mBitmapPaint)
        }

        mForegroundDrawable?.setBounds(imageBounds)
        mForegroundDrawable?.draw(canvas)

        // Foreground
        if (mFrontPaint.color != Color.TRANSPARENT) drawPaint(canvas, shapeBounds, mFrontPaint)

        // Border
        if (mBorderPaint.strokeWidth > 0 && mBorderPaint.color != Color.TRANSPARENT) {
            drawBorder(canvas, borderBounds, shapeBounds, imageBounds, mBorderPaint)
        }
    }

    protected abstract fun drawPaint(canvas: Canvas, bounds: RectF, paint: Paint)

    protected abstract fun drawBorder(
        canvas: Canvas,
        borderBounds: RectF,
        shapeBounds: RectF,
        imageBounds: RectF,
        borderPaint: Paint
    )
}
