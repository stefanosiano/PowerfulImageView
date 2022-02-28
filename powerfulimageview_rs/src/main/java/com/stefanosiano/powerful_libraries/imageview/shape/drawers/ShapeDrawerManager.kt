package com.stefanosiano.powerful_libraries.imageview.shape.drawers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import com.stefanosiano.powerful_libraries.imageview.extensions.isVector
import com.stefanosiano.powerful_libraries.imageview.extensions.rectF
import com.stefanosiano.powerful_libraries.imageview.extensions.safeHeight
import com.stefanosiano.powerful_libraries.imageview.extensions.safeWidth
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeMode
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeScaleType
import com.stefanosiano.powerful_libraries.imageview.shape.ShapeOptions
import java.lang.ref.WeakReference

/**
 * Manager class for shape drawers. Used to initialize use the needed drawers.
 */
@Suppress("TooManyFunctions")
internal class ShapeDrawerManager
/**
 * Manager class for shape drawers. Used to initialize and get the instances of the needed drawers.
 *
 * @param view View to show the image into
 * @param shapeOptions Options of the shape
 */(view: View, shapeOptions: ShapeOptions) : ShapeOptions.ShapeOptionsListener {

    // Using a weakRefence to be sure to not leak memory
    private val mView = WeakReference(view)

    /** Bounds of the shape. */
    private val mShapeBounds = RectF()

    /** Bounds of the shape border. */
    private val mBorderBounds = RectF()

    /** Bounds of the image. */
    private val mImageBounds = RectF()

    /** Matrix used by the drawers to scale the image. */
    private val mShaderMatrix = Matrix()

    /** Custom matrix used with MATRIX scale type. */
    private var mImageMatrix: Matrix? = null

    /** Scale type of the image. */
    private var mScaleType: PivShapeScaleType? = null

    /** Drawable of the view. */
    private var mDrawable: Drawable? = null

    /** Last bitmap calculated (i reuse this, so I don't decode it again). */
    private var mLastBitmap: Bitmap? = null

    /** Measured width, based on mode. */
    private var mMeasuredWidth: Float = 0f

    /** Measured height, based on mode. */
    private var mMeasuredHeight: Float = 0f

    // Drawers
    private val mCircleShapeDrawer by lazy { CircleShapeDrawer(getBitmapFromDrawable(mDrawable, mDrawable)) }
    private val mNormalShapeDrawer by lazy { NormalShapeDrawer(mDrawable) }
    private val mOvalShapeDrawer by lazy { OvalShapeDrawer(getBitmapFromDrawable(mDrawable, mDrawable)) }
    private val mSolidCircleShapeDrawer by lazy { SolidCircleShapeDrawer(mDrawable) }

    // private val mSolidArcShapeDrawer by lazy { SolidArcShapeDrawer(mDrawable) }
    // private val mSolidDiagonalShapeDrawer by lazy { SolidDiagonalShapeDrawer(mDrawable) }
    private val mRoundedRectangleShapeDrawer by lazy {
        RoundedRectangleShapeDrawer(getBitmapFromDrawable(mDrawable, mDrawable))
    }
    private val mSolidOvalShapeDrawer by lazy { SolidOvalShapeDrawer(mDrawable) }
    private val mSolidRoundedRectangleShapeDrawer by lazy { SolidRoundedRectangleShapeDrawer(mDrawable) }

    /** Interface used to switch between its implementations, based on the shape and options selected. */
    private var mShapeDrawer: ShapeDrawer = NormalShapeDrawer(null)

    /** Shape mode of the image. */
    private var mShapeMode: PivShapeMode = PivShapeMode.NORMAL

    /** Options used by shape drawers. */
    private var mShapeOptions: ShapeOptions = shapeOptions

    init {
        this.mShapeOptions.setListener(this)
        this.mShaderMatrix.reset()
    }

    /** Method that updates the [drawable] and bitmap to show. */
    fun changeDrawable(drawable: Drawable?) {
        val mLastDrawable = mDrawable
        this.mDrawable = drawable
        mShapeDrawer.changeDrawable(drawable)
        if (mShapeDrawer.requireBitmap()) {
            mLastBitmap = getBitmapFromDrawable(mLastDrawable, drawable)
            mShapeDrawer.changeBitmap(mLastBitmap)
        } else {
            mLastBitmap = null
        }
        setScaleType(mScaleType)
    }

    /** Returns the bitmap of the drawable. */
    private fun getBitmapFromDrawable(mLastDrawable: Drawable?, drawable: Drawable?): Bitmap? = when {
        drawable == null || mMeasuredWidth <= 0 || mMeasuredHeight <= 0 -> null
        drawable is BitmapDrawable && drawable.bitmap.isRecycled -> null
        drawable is BitmapDrawable -> drawable.bitmap
        else -> try {
            // Single color bitmap will be created of 1x1 pixel
            val bitmap: Bitmap =
                if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0 || drawable is ColorDrawable) {
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                } else {
                    createBitmap(drawable, mLastDrawable)
                }

            if (bitmap.isRecycled) {
                null
            } else {
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)

                bitmap.takeIf { !it.isRecycled }
            }
        } catch (e: IllegalArgumentException) {
            Log.e(ShapeDrawerManager::class.java.simpleName, e.message ?: "")
            null
        }
    }

    private fun createBitmap(drawable: Drawable, mLastDrawable: Drawable?): Bitmap {
        // Bitmap size should not be bigger than the view size
        val ratio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
        var sizeX: Int
        var sizeY: Int
        val maxWidth =
            mImageBounds.width().coerceAtLeast(mImageBounds.height() * ratio).toInt()
        val maxHeight =
            mImageBounds.height().coerceAtLeast(mImageBounds.width() / ratio).toInt()

        val isTooWide = drawable.intrinsicWidth > maxWidth && maxWidth > 0
        val isTooHigh = drawable.intrinsicHeight > maxHeight && maxHeight > 0
        if (isTooWide && isTooHigh) {
            sizeX = maxWidth
            sizeY = maxHeight
        } else {
            sizeX = drawable.intrinsicWidth
            sizeY = drawable.intrinsicHeight
        }

        // Vector drawables should always display at max resolution
        if (drawable.isVector()) {
            sizeX = maxWidth
            sizeY = maxHeight
        }

        // If i already decoded the bitmap i reuse it
        return if (sizeX > 0 && sizeY > 0 && mLastDrawable === mDrawable) {
            mLastBitmap?.takeIf { !it.isRecycled }
                ?: Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888)
        }
    }

    /**
     * Updates the drawers to use and chooses the right one to use based on the [shapeMode].
     * If the drawer doesn't exist, it will be instantiated.
     */
    private fun updateDrawers(shapeMode: PivShapeMode?) {
        // If there's no mode, i set it as normal
        mShapeDrawer = when (shapeMode ?: PivShapeMode.NORMAL) {
            PivShapeMode.CIRCLE -> mCircleShapeDrawer
            PivShapeMode.SQUARE -> mNormalShapeDrawer
            PivShapeMode.RECTANGLE -> mNormalShapeDrawer
            PivShapeMode.OVAL -> mOvalShapeDrawer
            PivShapeMode.ROUNDED_RECTANGLE -> mRoundedRectangleShapeDrawer
            PivShapeMode.SOLID_CIRCLE -> mSolidCircleShapeDrawer
            PivShapeMode.SOLID_OVAL -> mSolidOvalShapeDrawer
            PivShapeMode.SOLID_ROUNDED_RECTANGLE -> mSolidRoundedRectangleShapeDrawer
            PivShapeMode.NORMAL -> mNormalShapeDrawer
        }
    }

    /** It calculates the bounds of the image, using [w], [h] and [padding]. */
    fun onSizeChanged(w: Int, h: Int, padding: Rect) {
        mShapeOptions.calculateBounds(w, h, padding, mShapeMode)
        onSizeUpdated(mShapeOptions)
    }

    /** Measure the view and its content to determine the measured width and the measured height. */
    fun onMeasure(w: Float, h: Float, wMode: Int, hMode: Int, view: View) {
        // EXACTLY: size value was set to a specific value. This can also get triggered when match_parent
        // is used, to set the size exactly to the parent view (this is layout dependent).

        // AT_MOST: size value was set to match_parent or wrap_content where a maximum size is needed
        // (this is layout dependent). You should not be any larger than this size.

        // UNSPECIFIED: size value was set to wrap_content with no restrictions. You can be whatever
        // size you would like. Some layouts also use this callback to figure out your desired size
        // before determine what specs to actually pass you again in a second measure request.

        // Drawable width and height calculated if a mDrawable has been set. Used in calculations
        val drawableWidth = mDrawable?.intrinsicWidth?.plus(view.paddingLeft)?.plus(view.paddingRight)?.toFloat()
        val drawableHeight = mDrawable?.intrinsicHeight?.plus(view.paddingTop)?.plus(view.paddingBottom)?.toFloat()

        val usedRatio: Float = calculateRatioToUse(drawableWidth ?: w, drawableHeight ?: h)
        // Iif both are wrap_content (MeasureSpec.AT_MOST), size should be mDrawable size, but not bigger than view size
        val w2 = drawableWidth?.coerceAtMost(w) ?: w
        val h2 = drawableHeight?.coerceAtMost(h) ?: h

        when (wMode) {
            // Must be this size
            View.MeasureSpec.EXACTLY -> {
                mMeasuredWidth = w
                mMeasuredHeight = w / usedRatio

                if (hMode == View.MeasureSpec.EXACTLY) mMeasuredHeight = h
                if (hMode == View.MeasureSpec.AT_MOST) mMeasuredHeight = (w / usedRatio).coerceAtMost(h)
            }

            // Can't be bigger than...
            View.MeasureSpec.AT_MOST -> {
                mMeasuredWidth = w2
                mMeasuredHeight = w2 / usedRatio

                if (hMode == View.MeasureSpec.EXACTLY) {
                    mMeasuredWidth = (h * usedRatio).coerceAtMost(w)
                    mMeasuredHeight = h
                }

                if (hMode == View.MeasureSpec.AT_MOST) {
                    mMeasuredWidth = (h2 * usedRatio).coerceAtMost(w2)
                    mMeasuredHeight = h2.coerceAtMost(w2 / usedRatio)
                }
            }

            // Be whatever you want
            // View.MeasureSpec.UNSPECIFIED -> same as else
            else -> {
                mMeasuredWidth = w2
                mMeasuredHeight = w2 / usedRatio

                if (hMode == View.MeasureSpec.EXACTLY) {
                    mMeasuredWidth = h * usedRatio
                    mMeasuredHeight = h
                }

                if (hMode == View.MeasureSpec.AT_MOST) {
                    mMeasuredWidth = h2 * usedRatio
                    mMeasuredHeight = h2
                }
            }
        }
    }

    /** Calculates the ratio to use considering the shape mode and drawable's width and height. */
    private fun calculateRatioToUse(w: Float, h: Float) = when {
        mShapeMode.isSquared() -> 1f
        mShapeOptions.ratio <= 0 -> w / h
        else -> mShapeOptions.ratio
    }

    /** Sets the custom [matrix] to be used with the MATRIX scale type. */
    fun setImageMatrix(matrix: Matrix) {
        this.mImageMatrix = matrix
        setScaleType(PivShapeScaleType.MATRIX)
    }

    /** Sets the [scaleType] used to draw the image. */
    fun setScaleType(scaleType: PivShapeScaleType?) {
        mScaleType = scaleType

        if (mDrawable == null || scaleType == null) {
            return
        }

        // Scale used for vector drawable fix (other bitmaps have dwidth = bitmap width)
        var scaleX = 1f
        var scaleY = 1f
        // If drawable is ColorDrawable, dWidth and dHeight is -1 -> let's convert it to 1
        val dWidth = mDrawable?.intrinsicWidth?.let { if (it == -1) 1 else it } ?: 0
        val dHeight = mDrawable?.intrinsicHeight?.let { if (it == -1) 1 else it } ?: 0

        if (mLastBitmap?.isRecycled == false) {
            scaleX = dWidth.toFloat() / mLastBitmap.safeWidth(dWidth).toFloat()
            scaleY = dHeight.toFloat() / mLastBitmap.safeHeight(dHeight).toFloat()
        }

        recalculateShaderMatrix(scaleType, scaleX, scaleY, dWidth, dHeight)
        mShapeDrawer.setMatrix(scaleType, mShaderMatrix)
    }

    private fun recalculateShaderMatrix(
        scaleType: PivShapeScaleType,
        scaleX: Float,
        scaleY: Float,
        dWidth: Int,
        dHeight: Int
    ) {
        val vWidth = mImageBounds.width()
        val vHeight = mImageBounds.height()
        var scale: Float
        var dx: Float
        val dy: Float
        val lastBitmapBounds = rectF(mLastBitmap.safeWidth(dWidth), mLastBitmap.safeHeight(dHeight))

        /** Whether scaling should consider width or height (if increasing proportionally width and height makes the
         * drawable width reach the view width before the height or not). */
        val scaleOnWidth = dWidth * vHeight > vWidth * dHeight
        if (scaleOnWidth) {
            scale = vHeight / dHeight.toFloat()
            dx = (vWidth - dWidth * scale) / 2
        } else {
            scale = vWidth / dWidth.toFloat()
            dx = 0f
        }

        mShaderMatrix.reset()

        when (scaleType) {
            PivShapeScaleType.CENTER_CROP -> {
                dy = if (scaleOnWidth) 0f else (vHeight - dHeight * scale) / 2
                mShaderMatrix.setScale(scale * scaleX, scale * scaleY)
                mShaderMatrix.postTranslate(dx + mImageBounds.left, dy + mImageBounds.top)
            }

            PivShapeScaleType.TOP_CROP -> {
                dy = 0f
                mShaderMatrix.setScale(scale * scaleX, scale * scaleY)
                mShaderMatrix.postTranslate(dx + mImageBounds.left, dy + mImageBounds.top)
            }

            PivShapeScaleType.BOTTOM_CROP -> {
                dy = if (scaleOnWidth) 0f else vHeight - dHeight * scale
                mShaderMatrix.setScale(scale * scaleX, scale * scaleY)
                mShaderMatrix.postTranslate(dx + mImageBounds.left, dy + mImageBounds.top)
            }

            PivShapeScaleType.CENTER_INSIDE -> {
                // We scale down in case the drawable is bigger than the view, otherwise we don't scale (scale = 1)
                scale = vWidth.div(dWidth).coerceAtMost(vHeight.div(dHeight)).coerceAtMost(1f)
                dx = (vWidth - dWidth * scale) / 2
                dy = (vHeight - dHeight * scale) / 2
                mShaderMatrix.setScale(scale * scaleX, scale * scaleY)
                mShaderMatrix.postTranslate(dx + mImageBounds.left, dy + mImageBounds.top)
            }

            PivShapeScaleType.FIT_CENTER, PivShapeScaleType.FIT_END, PivShapeScaleType.FIT_START,
            PivShapeScaleType.FIT_XY -> {
                val scaleToFit = scaleType.scaleToFit() ?: return
                mShaderMatrix.setRectToRect(lastBitmapBounds, mImageBounds, scaleToFit)
            }

            PivShapeScaleType.MATRIX -> {
                mShaderMatrix.preScale(scaleX, scaleY)
                mImageMatrix?.let { mShaderMatrix.set(it) }
                mShaderMatrix.postTranslate(mImageBounds.left, mImageBounds.top)
            }

            PivShapeScaleType.CENTER -> {
                mShaderMatrix.setScale(scaleX, scaleY)
                mShaderMatrix.postTranslate(
                    (vWidth - dWidth) / 2 + mImageBounds.left,
                    (vHeight - dHeight) / 2 + mImageBounds.top
                )
            }
        }
    }

    /**
     * Returns the [PivShapeScaleType].
     * If a normal [android.widget.ImageView.ScaleType] was set, the corresponding [PivShapeScaleType] is returned.
     */
    fun getScaleType(): PivShapeScaleType? = mScaleType

    /** Changes the [shapeMode] of the image. */
    fun changeShapeMode(shapeMode: PivShapeMode) {
        if (mShapeMode == shapeMode) {
            return
        }

        mShapeMode = shapeMode
        updateDrawers(mShapeMode)
        mShapeDrawer.setup(mShapeOptions)
        setScaleType(mScaleType)
        mView.get()?.postInvalidate()
    }

    /** Draws the image. */
    fun onDraw(canvas: Canvas) = mShapeDrawer.draw(canvas, mBorderBounds, mShapeBounds, mImageBounds)

    /** @return The options of the shape. */
    fun getShapeOptions(): ShapeOptions = mShapeOptions

    /** Called when an option that requires onMeasure() call is updated. */
    override fun onRequestMeasure(options: ShapeOptions) {
        mShapeOptions = options
        mView.get()?.requestLayout()
        mShapeDrawer.setup(options)
    }

    /** Called when an option is updated. It propagates the update to the shape drawers. */
    override fun onOptionsUpdated(options: ShapeOptions) {
        mShapeOptions = options
        mShapeDrawer.setup(options)
        mView.get()?.postInvalidate()
    }

    /**
     * Called when an option that changes the size of the shape is updated.
     * The bounds are calculated again, and it propagates the update to the shape drawers.
     */
    override fun onSizeUpdated(options: ShapeOptions) {
        mShapeOptions = options
        // Set calculated bounds to our progress bounds
        mShapeBounds.set(mShapeOptions.shapeBounds)
        mBorderBounds.set(mShapeOptions.borderBounds)
        mImageBounds.set(mShapeOptions.imageBounds)

        changeDrawable(mDrawable)

        mShapeDrawer.setup(mShapeOptions)
        mView.get()?.postInvalidate()
    }

    /**
     * Returns the measured height to be used in onMeasure() method of the view.
     * It is calculated based on the shape of the image and its size.
     */
    fun getMeasuredHeight(): Int = mMeasuredHeight.toInt()

    /**
     * Returns the measured width to be used in onMeasure() method of the view.
     * It is calculated based on the shape of the image and its size.
     */
    fun getMeasuredWidth(): Int = mMeasuredWidth.toInt()

    /** Returns the current shape mode. */
    fun getShapeMode(): PivShapeMode = mShapeMode
}
