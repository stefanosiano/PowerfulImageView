package com.stefanosiano.powerfullibraries.imageview.shape.drawers

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.stefanosiano.powerfullibraries.imageview.shape.ShapeOptions


/**
 * ShapeDrawer that draws the drawable directly into the shape and then draws a solid color over it.
 */
internal class SolidRoundedRectangleShapeDrawer
/** ShapeDrawer that draws the drawable directly into the shape and then draws a solid color over it */
(drawable: Drawable?) : BaseNormalShapeDrawer(drawable) {

    /** Paint used to draw the solid color  */
    private val mSolidPaint = Paint()

    /** Paint used to draw the solid color  */
    private val mSolidRect = RectF()

    /** Variables used to draw the border and the solid color  */
    private var mRadiusX: Float = 0f
    private var mRadiusY: Float = 0f

    override fun setup(shapeOptions: ShapeOptions) {
        super.setup(shapeOptions)


        mRadiusX = shapeOptions.radiusX
        mRadiusY = shapeOptions.radiusY

        mBackPaint.isAntiAlias = true
        mBackPaint.style = Paint.Style.FILL_AND_STROKE
        mFrontPaint.isAntiAlias = true
        mFrontPaint.style = Paint.Style.FILL_AND_STROKE


        mSolidPaint.color = shapeOptions.solidColor
        mSolidPaint.isAntiAlias = true
        mSolidPaint.style = Paint.Style.STROKE

        //At maximum, the rounded rectangle can become an oval, so I calculate width in the same way of the oval.
        //I must be sure to fill the whole view -> the maximum distance of the rectangle of the view
        //that is the hypotenuse of the triangle built over radii of the rounded rectangle.
        //I also add any space between the image and the view (given by any padding)
        val width = Math.max(shapeOptions.viewBounds.width() - shapeOptions.borderBounds.width() + mRadiusX, shapeOptions.viewBounds.height() - shapeOptions.borderBounds.height() + mRadiusY) / 2

        mSolidPaint.strokeWidth = width

        mSolidRect.set(shapeOptions.borderBounds)
        mSolidRect.inset(-width / 2 - shapeOptions.borderWidth / 2, -width / 2 - shapeOptions.borderWidth / 2)
    }

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) = canvas.drawRoundRect(shapeBounds, mRadiusX, mRadiusY, paint)

    override fun drawBorder(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) =
            canvas.drawRoundRect(borderBounds, mRadiusX * (borderBounds.width() / shapeBounds.width()), mRadiusY * (borderBounds.height() / shapeBounds.height()), borderPaint)

    override fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) =
            canvas.drawRoundRect(mSolidRect, mRadiusX, mRadiusY, mSolidPaint)


}


/**
 * ShapeDrawer that draws the drawable directly into the shape and then draws a solid color over it.
 */
internal class SolidOvalShapeDrawer
/** ShapeDrawer that draws the drawable directly into the shape and then draws a solid color over it */
(drawable: Drawable?) : BaseNormalShapeDrawer(drawable) {

    /** Paint used to draw the solid color  */
    private val mSolidPaint = Paint()

    /** Paint used to draw the solid color  */
    private val mSolidRect = RectF()

    override fun setup(shapeOptions: ShapeOptions) {
        super.setup(shapeOptions)

        mBackPaint.isAntiAlias = true
        mBackPaint.style = Paint.Style.FILL_AND_STROKE
        mFrontPaint.isAntiAlias = true
        mFrontPaint.style = Paint.Style.FILL_AND_STROKE


        mSolidPaint.color = shapeOptions.solidColor
        mSolidPaint.isAntiAlias = true
        mSolidPaint.style = Paint.Style.STROKE

        //I must be sure to fill the whole view -> the maximum distance of the rectangle of the view
        //that is the hypotenuse of the triangle built over half width and half height of the rectangle.
        //I could use Pythagoras formula, but using triangles maths, we know that width+height > hypotenuse
        //Finally i subtract the shape radius, since it will
        val width = (shapeOptions.viewBounds.width() + shapeOptions.viewBounds.height() - shapeOptions.borderBounds.width()) / 2

        mSolidPaint.strokeWidth = width

        mSolidRect.set(shapeOptions.borderBounds)
        mSolidRect.inset(-width / 2 - shapeOptions.borderWidth / 2, -width / 2 - shapeOptions.borderWidth / 2)
    }

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) = canvas.drawOval(shapeBounds, paint)

    override fun drawBorder(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) = canvas.drawOval(borderBounds, mBorderPaint)

    override fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) = canvas.drawOval(mSolidRect, mSolidPaint)



}
/**
 * ShapeDrawer that draws the drawable directly into the shape and then draws a solid color over it.
 */
internal class SolidCircleShapeDrawer
/** ShapeDrawer that draws the drawable directly into the shape and then draws a solid color over it */
(drawable: Drawable?) : BaseNormalShapeDrawer(drawable) {

    /** Paint used to draw the solid color  */
    private val mSolidPaint = Paint()

    /** Variables used to draw the border and the solid color  */
    private var mCx: Float = 0f
    private var mCy: Float = 0f
    private var mRadius: Float = 0f
    private var mSolidRadius: Float = 0f
    private var mBorderRadius: Float = 0f

    override fun setup(shapeOptions: ShapeOptions) {
        super.setup(shapeOptions)

        mBackPaint.isAntiAlias = true
        mBackPaint.style = Paint.Style.FILL_AND_STROKE
        mFrontPaint.isAntiAlias = true
        mFrontPaint.style = Paint.Style.FILL_AND_STROKE


        mSolidPaint.color = shapeOptions.solidColor
        mSolidPaint.isAntiAlias = true
        mSolidPaint.style = Paint.Style.STROKE

        mCx = shapeOptions.borderBounds.centerX()
        mCy = shapeOptions.borderBounds.centerY()

        //I must be sure to fill the whole view -> the maximum distance of the rectangle of the view
        //that is the hypotenuse of the triangle built over half width and half height of the rectangle.
        //I could use Pythagoras formula, but using triangles maths, we know that width+height > hypotenuse
        //Finally i subtract the shape radius, since it will
        val width = (shapeOptions.viewBounds.width() + shapeOptions.viewBounds.height() - shapeOptions.borderBounds.width()) / 2

        mRadius = if (shapeOptions.shapeBounds.width() < shapeOptions.shapeBounds.height()) shapeOptions.shapeBounds.width() / 2 else shapeOptions.shapeBounds.height() / 2
        mSolidRadius = (shapeOptions.borderBounds.width() + width + shapeOptions.borderWidth.toFloat()) / 2
        mBorderRadius = if (shapeOptions.borderBounds.width() < shapeOptions.borderBounds.height()) shapeOptions.borderBounds.width() / 2 else shapeOptions.borderBounds.height() / 2
        mSolidPaint.strokeWidth = width
    }

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) = canvas.drawCircle(mCx, mCy, mRadius, mBackPaint)

    override fun drawBorder(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) = canvas.drawCircle(mCx, mCy, mBorderRadius, mBorderPaint)

    override fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) = canvas.drawCircle(mCx, mCy, mSolidRadius, mSolidPaint)
}

