package com.stefanosiano.powerfulimageview.shape.drawers

import android.graphics.*
import android.graphics.drawable.Drawable
import com.stefanosiano.powerfulimageview.shape.PivShapeScaleType
import com.stefanosiano.powerfulimageview.shape.ShapeOptions


internal abstract class BaseRoundedDrawer
/** ShapeDrawer that draws an oval as shape. */
(bitmap: Bitmap?) : ShapeDrawer {

    /** Shader to efficiently draw the shape  */
    private var mBitmapShader: BitmapShader? = null

    /** Paint used to draw the image  */
    private var mBitmapPaint = Paint()

    /** Paint used to draw the shape background  */
    private var mBackPaint = Paint()

    /** Paint used to draw the shape border  */
    private var mBorderPaint = Paint()

    /** Paint used to draw the shape foreground  */
    private var mFrontPaint = Paint()

    /** Background drawable to draw under the shape  */
    private var mBackgroundDrawable: Drawable? = null

    /** Foreground drawable to draw over the shape  */
    private var mForegroundDrawable: Drawable? = null

    /** Matrix used to draw the shape  */
    private var mMatrix: Matrix? = null


    init {
        if (bitmap != null) {
            this.mBitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mBitmapShader?.setLocalMatrix(mMatrix)
        }
    }

    override fun changeDrawable(drawable: Drawable?) {}

    override fun requireBitmap(): Boolean = true

    override fun changeBitmap(bitmap: Bitmap?) {
        this.mBitmapShader = null
        if (bitmap != null) {
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

        //background
        if (mBackPaint.color != Color.TRANSPARENT) drawPaint(canvas, shapeBounds, mBackPaint)

        mBackgroundDrawable?.setBounds(imageBounds.left.toInt(), imageBounds.top.toInt(), imageBounds.right.toInt(), imageBounds.bottom.toInt())
        mBackgroundDrawable?.draw(canvas)

        //image
        drawPaint(canvas, imageBounds, mBackPaint)

        mForegroundDrawable?.setBounds(imageBounds.left.toInt(), imageBounds.top.toInt(), imageBounds.right.toInt(), imageBounds.bottom.toInt())
        mForegroundDrawable?.draw(canvas)

        //foreground
        if (mFrontPaint.color != Color.TRANSPARENT) drawPaint(canvas, shapeBounds, mFrontPaint)

        //border
        if (mBorderPaint.strokeWidth > 0 && mBorderPaint.color != Color.TRANSPARENT) drawBorder(canvas, borderBounds, shapeBounds, imageBounds, mBorderPaint)
    }

    protected abstract fun drawPaint(canvas: Canvas, bounds: RectF, paint: Paint)
    protected abstract fun drawBorder(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint)
}
