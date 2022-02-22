package com.stefanosiano.powerful_libraries.imageview.shape.drawers

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.stefanosiano.powerful_libraries.imageview.progress.PivShapeCutGravity
import com.stefanosiano.powerful_libraries.imageview.shape.ShapeOptions
import kotlin.math.absoluteValue


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
        val width = Math.max(
            shapeOptions.viewBounds.width() - shapeOptions.borderBounds.width() + mRadiusX,
            shapeOptions.viewBounds.height() - shapeOptions.borderBounds.height() + mRadiusY
        ) / 2

        mSolidPaint.strokeWidth = width

        mSolidRect.set(shapeOptions.borderBounds)
        mSolidRect.inset(-width / 2 - shapeOptions.borderWidth / 2, -width / 2 - shapeOptions.borderWidth / 2)
    }

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) =
        canvas.drawRoundRect(shapeBounds, mRadiusX, mRadiusY, paint)

    override fun drawBorder(
        canvas: Canvas,
        borderBounds: RectF,
        shapeBounds: RectF,
        imageBounds: RectF,
        borderPaint: Paint) =
        canvas.drawRoundRect(
                borderBounds,
            mRadiusX * (borderBounds.width() / shapeBounds.width()),
            mRadiusY * (borderBounds.height() / shapeBounds.height()),
            borderPaint)

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
        val width =
            (shapeOptions.viewBounds.width() + shapeOptions.viewBounds.height() - shapeOptions.borderBounds.width()) / 2

        mSolidPaint.strokeWidth = width

        mSolidRect.set(shapeOptions.borderBounds)
        mSolidRect.inset(-width / 2 - shapeOptions.borderWidth / 2, -width / 2 - shapeOptions.borderWidth / 2)
    }

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) = canvas.drawOval(shapeBounds, paint)

    override fun drawBorder(
        canvas: Canvas,
        borderBounds: RectF,
        shapeBounds: RectF,
        imageBounds: RectF,
        borderPaint: Paint) =
        canvas.drawOval(borderBounds, mBorderPaint)

    override fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) =
        canvas.drawOval(mSolidRect, mSolidPaint)



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
        val width =
            (shapeOptions.viewBounds.width() + shapeOptions.viewBounds.height() - shapeOptions.borderBounds.width()) / 2

        mRadius = if (shapeOptions.shapeBounds.width() < shapeOptions.shapeBounds.height())
            shapeOptions.shapeBounds.width() / 2
        else shapeOptions.shapeBounds.height() / 2
        mSolidRadius = (shapeOptions.borderBounds.width() + width + shapeOptions.borderWidth.toFloat()) / 2
        mBorderRadius = if (shapeOptions.borderBounds.width() < shapeOptions.borderBounds.height())
            shapeOptions.borderBounds.width() / 2
        else shapeOptions.borderBounds.height() / 2
        mSolidPaint.strokeWidth = width
    }

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) =
        canvas.drawCircle(mCx, mCy, mRadius, mBackPaint)

    override fun drawBorder(
        canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) =
        canvas.drawCircle(mCx, mCy, mBorderRadius, mBorderPaint)

    override fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) =
        canvas.drawCircle(mCx, mCy, mSolidRadius, mSolidPaint)
}



internal class SolidDiagonalShapeDrawer(drawable: Drawable?): BaseNormalShapeDrawer(drawable) {
//internal class SolidCircleShapeDrawer(drawable: Drawable?): BaseNormalShapeDrawer(drawable) {
    private val mSolidPaint = Paint()
    private var mSolidBounds = RectF()

    override fun setup(shapeOptions: ShapeOptions) {
        super.setup(shapeOptions)

        mSolidPaint.color = shapeOptions.solidColor
        mSolidPaint.isAntiAlias = true
        mSolidPaint.style = Paint.Style.FILL//_AND_STROKE
        mSolidPaint.strokeWidth = shapeOptions.shapeBounds.height()*0.75F

        //calculate solid line coordinates
        val b = shapeOptions.shapeBounds
        val cat1 = if(shapeOptions.cutRadius1 != 0)
            shapeOptions.cutRadius1.toFloat()
        else shapeOptions.cutRadius1Percent/100F*b.height()
        val cat2 = if(shapeOptions.cutRadius2 != 0)
            shapeOptions.cutRadius2.absoluteValue.toFloat()
        else shapeOptions.cutRadius2Percent.absoluteValue/100F*b.width()
        val hypo = Math.sqrt((cat1*cat1 + cat2*cat2).toDouble())
        val angle = Math.acos(cat1/hypo)
        val h = (mSolidPaint.strokeWidth/2F*Math.sin(angle)).toFloat()
        val w = (mSolidPaint.strokeWidth/2F*Math.cos(angle)).toFloat()

        when {
            shapeOptions.cutGravity.isGravityTop() || shapeOptions.cutGravity == PivShapeCutGravity.END -> {
                mSolidBounds.left = b.left - (if (cat1 >= 0) cat2 - w - b.width() else -w)
                mSolidBounds.top = b.top - h - cat1.coerceAtMost(0F)
                mSolidBounds.right = b.right - (if (cat1 >= 0) -w else b.width() - cat2 - w)
                mSolidBounds.bottom = b.top + cat1.coerceAtLeast(0F) - h
            }
//            shapeOptions.cutGravity.isGravityBottom() || shapeOptions.cutGravity == PivShapeCutGravity.END -> {
            else -> {
                mSolidBounds.left = b.left - (if (cat1 >= 0) cat2 - w - b.width() else -w)
                mSolidBounds.top = b.bottom + h + cat1.coerceAtMost(0F)
                mSolidBounds.right = b.right - (if (cat1 >= 0) -w else b.width() - cat2 - w)
                mSolidBounds.bottom = b.bottom - cat1.coerceAtLeast(0F) + h
            }
        }
    }

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) = canvas.drawRect(shapeBounds, paint)

    override fun drawBorder(
        canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) =
        canvas.drawRect(borderBounds, borderPaint)

    override fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) {
        canvas.drawLine(mSolidBounds.left, mSolidBounds.top, mSolidBounds.right, mSolidBounds.bottom, mSolidPaint)
    }
}






internal class SolidArcShapeDrawer(drawable: Drawable?): BaseNormalShapeDrawer(drawable) {
//internal class SolidCircleShapeDrawer(drawable: Drawable?): BaseNormalShapeDrawer(drawable) {
    private val mSolidPaint = Paint()
    private var mSolidBounds = RectF()
    private var mStartAngle = 180f

    override fun setup(shapeOptions: ShapeOptions) {
        super.setup(shapeOptions)

        mSolidPaint.color = shapeOptions.solidColor
        mSolidPaint.isAntiAlias = true

        //calculate solid line coordinates
        val b = shapeOptions.shapeBounds

        val h = if(shapeOptions.cutRadius1 != 0)
            shapeOptions.cutRadius1.toFloat()
        else 2f*shapeOptions.cutRadius1Percent/100F*b.height()
        val w = if(shapeOptions.cutRadius2 != 0)
            shapeOptions.cutRadius2.absoluteValue/2f
        else (shapeOptions.cutRadius2Percent.absoluteValue/2f)/100F*b.width()

        mSolidPaint.strokeWidth = if (h > 0) ((shapeOptions.viewBounds.width()-w)*0.75f) else w

        mSolidPaint.style = if (h > 0) Paint.Style.STROKE else Paint.Style.FILL
        val sW = if (h > 0) mSolidPaint.strokeWidth/2f else h

        mSolidBounds.left = if (h > 0) (b.left + b.width() / 2f - sW - w) else b.left + b.width() / 2f - w
        mSolidBounds.right = if (h > 0) (b.right - b.width() / 2f + sW + w) else b.left + b.width() / 2f + w
        when  {
            shapeOptions.cutGravity.isGravityBottom() -> {
                mStartAngle = if (h > 0) 0f else 180f
                mSolidBounds.top = if (h > 0) (b.bottom - sW - h) else b.bottom + h / 2
                mSolidBounds.bottom = if (h > 0) (b.bottom + sW) else b.bottom - h
            }

            shapeOptions.cutGravity.isGravityTop() -> {
                mStartAngle = if (h > 0) 180f else 0f
                mSolidBounds.top = if (h > 0) (b.top - sW) else b.top + h
                mSolidBounds.bottom = if (h > 0) (b.top + sW + h) else b.top - h / 2
            }

            shapeOptions.cutGravity == PivShapeCutGravity.START -> {
                mStartAngle = if (h > 0) 90f else 270f
                mSolidBounds.top = if (h > 0) (b.bottom - sW - h) else b.bottom + h / 2
                mSolidBounds.bottom = if (h > 0) (b.bottom + sW) else b.bottom - h
            }

            shapeOptions.cutGravity == PivShapeCutGravity.END -> {
                mStartAngle = if (h > 0) 270f else 90f
                mSolidBounds.top = if (h > 0) (b.bottom - sW - h) else b.bottom + h / 2
                mSolidBounds.bottom = if (h > 0) (b.bottom + sW) else b.bottom - h
            }

        }

    }

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) = canvas.drawRect(shapeBounds, paint)

    override fun drawBorder(
        canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) =
        canvas.drawRect(borderBounds, borderPaint)

    override fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) {
        canvas.drawArc(mSolidBounds, mStartAngle, 180f, false, mSolidPaint)
    }
}








//internal class SolidCornerShapeDrawer(drawable: Drawable?): BaseNormalShapeDrawer(drawable) {
internal class SolidCircleShapeDrawer12(drawable: Drawable?): BaseNormalShapeDrawer(drawable) {
    private val mSolidPaint = Paint()
    private var mSolidBounds = RectF()

    override fun setup(shapeOptions: ShapeOptions) {
        super.setup(shapeOptions)

        mSolidPaint.color = shapeOptions.solidColor
        mSolidPaint.isAntiAlias = true

        //calculate solid line coordinates
        val b = shapeOptions.shapeBounds

        val h = (if(shapeOptions.cutRadius1 != 0)
            2f * shapeOptions.cutRadius1.toFloat()
        else 2f*shapeOptions.cutRadius1Percent/100F*b.biggest())
            .absoluteValue.coerceAtMost(2f*b.biggest())

        val w = (if(shapeOptions.cutRadius2 != 0)
            shapeOptions.cutRadius2.toFloat()
        else shapeOptions.cutRadius2Percent/100F*b.biggest())
            .coerceAtMost(b.biggest())/2

        mSolidPaint.strokeWidth = h*0.75f
        mSolidPaint.style = Paint.Style.STROKE

        val sW = mSolidPaint.strokeWidth/2f

        //todo check this algorithm... something's wrong
        mSolidBounds.left = b.right - h - sW +w
        mSolidBounds.top = b.top - sW
        mSolidBounds.right = b.right + sW
        mSolidBounds.bottom = b.top + h + sW -w
    }

    override fun drawPaint(canvas: Canvas, shapeBounds: RectF, paint: Paint) = canvas.drawRect(shapeBounds, paint)

    override fun drawBorder(
        canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF, borderPaint: Paint) =
        canvas.drawRect(borderBounds, borderPaint)

    override fun drawSolid(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF) {
        canvas.drawArc(mSolidBounds, -90f, 90f, false, mSolidPaint)
    }
}

fun RectF.biggest() = width().coerceAtMost(height())


