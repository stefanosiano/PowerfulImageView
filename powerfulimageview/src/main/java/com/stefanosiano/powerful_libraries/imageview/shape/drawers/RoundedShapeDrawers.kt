package com.stefanosiano.powerful_libraries.imageview.shape.drawers

import android.graphics.*
import com.stefanosiano.powerful_libraries.imageview.shape.ShapeOptions

internal class RoundedRectangleShapeDrawer(bitmap: Bitmap?): BaseRoundedDrawer(bitmap) {

    /** Variables used to draw the rounded rectangle  */
    private var mRadiusX: Float = 0f
    private var mRadiusY: Float = 0f

    override fun setup(shapeOptions: ShapeOptions) {
        super.setup(shapeOptions)
        mRadiusX = shapeOptions.radiusX
        mRadiusY = shapeOptions.radiusY
    }

    override fun drawPaint(canvas: Canvas, bounds: RectF, paint: Paint) = canvas.drawRoundRect(bounds, mRadiusX, mRadiusY, paint)

    override fun drawBorder(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) =
        canvas.drawRoundRect(borderBounds, mRadiusX * borderBounds.height() / imageBounds.height(), mRadiusY * borderBounds.width() / imageBounds.width(), borderPaint)
}


internal class CircleShapeDrawer(bitmap: Bitmap?): BaseRoundedDrawer(bitmap) {

    /* Variables used to draw the circle  */
    private var mCx: Float = 0f
    private var mCy:Float = 0f
    private var mRadius:Float = 0f
    private var mBorderRadius:Float = 0f

    override fun setup(shapeOptions: ShapeOptions) {
        super.setup(shapeOptions)
        mCx = shapeOptions.shapeBounds.centerX()
        mCy = shapeOptions.shapeBounds.centerY()
        mRadius = if (shapeOptions.shapeBounds.width() < shapeOptions.shapeBounds.height()) shapeOptions.shapeBounds.width() / 2 else shapeOptions.shapeBounds.height() / 2
        mBorderRadius = if (shapeOptions.borderBounds.width() < shapeOptions.borderBounds.height()) shapeOptions.borderBounds.width() / 2 else shapeOptions.borderBounds.height() / 2
    }

    override fun drawPaint(canvas: Canvas, bounds: RectF, paint: Paint) = canvas.drawCircle(mCx, mCy, mRadius, paint)

    override fun drawBorder(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) =
        canvas.drawCircle(mCx, mCy, mBorderRadius, borderPaint)
}


internal class OvalShapeDrawer(bitmap: Bitmap?): BaseRoundedDrawer(bitmap) {
    override fun drawPaint(canvas: Canvas, bounds: RectF, paint: Paint) = canvas.drawOval(bounds, paint)
    override fun drawBorder(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) = canvas.drawOval(borderBounds, borderPaint)
}