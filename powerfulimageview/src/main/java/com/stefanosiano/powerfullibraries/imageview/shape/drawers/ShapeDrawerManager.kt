package com.stefanosiano.powerfullibraries.imageview.shape.drawers

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.stefanosiano.powerfullibraries.imageview.shape.PivShapeMode
import com.stefanosiano.powerfullibraries.imageview.shape.PivShapeScaleType
import com.stefanosiano.powerfullibraries.imageview.shape.ShapeOptions
import java.lang.ref.WeakReference


/**
 * Manager class for shape drawers. Used to initialize use the needed drawers.
 */

internal class ShapeDrawerManager
/**
 * Manager class for shape drawers. Used to initialize and get the instances of the needed drawers.
 *
 * @param view View to show the image into
 * @param shapeOptions Options of the shape
 */(view: View, shapeOptions: ShapeOptions) : ShapeOptions.ShapeOptionsListener {

    //Using a weakRefence to be sure to not leak memory
    private val mView = WeakReference(view)

    /** Bounds of the shape  */
    private val mShapeBounds = RectF()

    /** Bounds of the shape border  */
    private val mBorderBounds = RectF()

    /** Bounds of the image  */
    private val mImageBounds = RectF()

    /** Matrix used by the drawers to scale the image  */
    private val mShaderMatrix = Matrix()

    /** Custom matrix used with MATRIX scale type  */
    private var mImageMatrix: Matrix? = null

    /** Scale type of the image  */
    private var mScaleType: PivShapeScaleType? = null

    /** Drawable of the view  */
    private var mDrawable: Drawable? = null

    /** Last bitmap calculated (i reuse this, so I don't decode it again)  */
    private var mLastBitmap: Bitmap? = null

    /** Measured width, based on mode  */
    private var mMeasuredWidth: Float = 0f

    /** Measured height, based on mode  */
    private var mMeasuredHeight: Float = 0f


    //Drawers
    private val mCircleShapeDrawer by lazy { CircleShapeDrawer(getBitmapFromDrawable(mDrawable, mDrawable)) }
    private val mNormalShapeDrawer by lazy { NormalShapeDrawer(mDrawable) }
    private val mOvalShapeDrawer by lazy { OvalShapeDrawer(getBitmapFromDrawable(mDrawable, mDrawable)) }
    private val mSolidCircleShapeDrawer by lazy { SolidCircleShapeDrawer(mDrawable) }
    private val mRoundedRectangleShapeDrawer by lazy { RoundedRectangleShapeDrawer(getBitmapFromDrawable(mDrawable, mDrawable)) }
    private val mSolidOvalShapeDrawer by lazy { SolidOvalShapeDrawer(mDrawable) }
    private val mSolidRoundedRectangleShapeDrawer by lazy { SolidRoundedRectangleShapeDrawer(mDrawable) }

    /** Interface used to switch between its implementations, based on the shape and options selected.  */
    private var mShapeDrawer: ShapeDrawer = NormalShapeDrawer(null)

    /** Shape mode of the image  */
    private var mShapeMode: PivShapeMode = PivShapeMode.NORMAL

    /** Options used by shape drawers  */
    private var mShapeOptions: ShapeOptions = shapeOptions


    init {
        this.mShapeOptions.setListener(this)
        this.mShaderMatrix.reset()
    }


    /**
     * Method that updates the drawable and bitmap to show
     *
     * @param drawable drawable to show
     */
    fun changeDrawable(drawable: Drawable?) {
        val mLastDrawable = mDrawable
        this.mDrawable = drawable
        mShapeDrawer.changeDrawable(drawable)
        if (mShapeDrawer.requireBitmap()) {
            mLastBitmap = getBitmapFromDrawable(mLastDrawable, drawable)
            mShapeDrawer.changeBitmap(mLastBitmap)
        } else
            mLastBitmap = null
        setScaleType(mScaleType)
    }

    /**
     * @return Returns the bitmap of the drawable
     */
    private fun getBitmapFromDrawable(mLastDrawable: Drawable?, drawable: Drawable?): Bitmap? {
        if (drawable == null || mMeasuredWidth <= 0 || mMeasuredHeight <= 0)
            return null


        if (drawable is BitmapDrawable)
            return drawable.bitmap


        try {
            val bitmap: Bitmap = when {

                drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0 -> Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel

                drawable is ColorDrawable -> Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

                else -> {
                    //bitmap size should not be bigger than the view size
                    val ratio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
                    var sizeX: Int
                    var sizeY: Int
                    val maxWidth = Math.max(mImageBounds.width(), mImageBounds.height() * ratio).toInt()
                    val maxHeight = Math.max(mImageBounds.height(), mImageBounds.width() / ratio).toInt()

                    if (drawable.intrinsicWidth > maxWidth && maxWidth > 0 && drawable.intrinsicHeight > maxHeight && maxHeight > 0) {
                        sizeX = maxWidth
                        sizeY = maxHeight
                    } else {
                        sizeX = drawable.intrinsicWidth
                        sizeY = drawable.intrinsicHeight
                    }

                    //vector drawables should always display at max resolution
                    if (drawable.javaClass.name == "android.graphics.drawable.VectorDrawable" || drawable is VectorDrawableCompat) {
                        sizeX = maxWidth
                        sizeY = maxHeight
                    }

                    //if i already decoded the bitmap i reuse it
                    if (sizeX > 0 && sizeY > 0 && mLastBitmap != null && mLastDrawable === mDrawable)
                        return mLastBitmap

                    //otherwise I free its memory
                    mLastBitmap?.recycle()

                    Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888)
                }
            }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }


    /**
     * Updates the drawers to use and chooses the right one to use based on the mode.
     * If the drawer doesn't exist, it will be instantiated.
     *
     * @param shapeMode Mode of the shape, used to choose the right drawers.
     */
    private fun updateDrawers(shapeMode: PivShapeMode?) {

        //If there's no mode, i set it as normal
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
            else -> mNormalShapeDrawer
        }
    }


    /**
     * It calculates the bounds of the image.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     */
    fun onSizeChanged(w: Int, h: Int, paddingLeft: Int, paddingTop: Int, paddingRight: Int, paddingBottom: Int) {
        mShapeOptions.calculateBounds(w, h, paddingLeft, paddingTop, paddingRight, paddingBottom, mShapeMode)
        onSizeUpdated(mShapeOptions)
    }


    /**
     * Measure the view and its content to determine the measured width and the measured height
     */
    fun onMeasure(w: Float, h: Float, wMode: Int, hMode: Int, view: View) {
        var w = w
        var h = h

        // EXACTLY: size value was set to a specific value. This can also get triggered when match_parent
        // is used, to set the size exactly to the parent view (this is layout dependent).

        // AT_MOST: size value was set to match_parent or wrap_content where a maximum size is needed
        // (this is layout dependent). You should not be any larger than this size.

        // UNSPECIFIED: size value was set to wrap_content with no restrictions. You can be whatever
        // size you would like. Some layouts also use this callback to figure out your desired size
        // before determine what specs to actually pass you again in a second measure request.

        //Drawable width and height calculated if a mDrawable has been set. Used in calculations
        val drawableWidth = mDrawable?.intrinsicWidth?.plus(view.paddingLeft)?.plus(view.paddingRight)?.toFloat()
                ?: 0f
        val drawableHeight = mDrawable?.intrinsicHeight?.plus(view.paddingTop)?.plus(view.paddingBottom)?.toFloat()
                ?: 0f

        val usedRatio: Float
        usedRatio = when (mShapeMode) {
            PivShapeMode.CIRCLE, PivShapeMode.SQUARE, PivShapeMode.SOLID_CIRCLE -> 1f
            PivShapeMode.RECTANGLE, PivShapeMode.ROUNDED_RECTANGLE, PivShapeMode.SOLID_ROUNDED_RECTANGLE, PivShapeMode.OVAL, PivShapeMode.SOLID_OVAL -> if (mShapeOptions.ratio <= 0) w / h else mShapeOptions.ratio
            else -> if (mShapeOptions.ratio <= 0) w / h else mShapeOptions.ratio
        }


        when (wMode) {

            //Must be this size
            View.MeasureSpec.EXACTLY -> {
                mMeasuredWidth = w
                mMeasuredHeight = w / usedRatio

                when (hMode) {
                    View.MeasureSpec.EXACTLY -> mMeasuredHeight = h
                    View.MeasureSpec.AT_MOST -> mMeasuredHeight = Math.min(w / usedRatio, h)
                    View.MeasureSpec.UNSPECIFIED -> w / usedRatio
                }
            }

            //Can't be bigger than...
            View.MeasureSpec.AT_MOST -> {

                mMeasuredWidth = w
                mMeasuredHeight = w / usedRatio

                if (hMode == View.MeasureSpec.EXACTLY) {
                    mMeasuredWidth = Math.min(h * usedRatio, w)
                    mMeasuredHeight = h
                }

                if (hMode == View.MeasureSpec.AT_MOST) {
                    //if both are wrap_content, size should be mDrawable size
                    w = if (drawableWidth > 0) Math.min(drawableWidth, w) else w
                    h = if (drawableHeight > 0) Math.min(drawableHeight, h) else h
                    mMeasuredWidth = Math.min(h * usedRatio, w)
                    mMeasuredHeight = Math.min(h, w / usedRatio)
                }

                if (hMode == View.MeasureSpec.UNSPECIFIED) {
                    w = if (drawableWidth > 0) Math.min(drawableWidth, w) else w
                    mMeasuredWidth = w
                    mMeasuredHeight = w / usedRatio
                }
            }

            View.MeasureSpec.UNSPECIFIED -> {
                w = if (drawableWidth > 0) drawableWidth else w
                mMeasuredWidth = w
                mMeasuredHeight = w / usedRatio

                when (hMode) {
                    View.MeasureSpec.EXACTLY -> {
                        mMeasuredWidth = h * usedRatio
                        mMeasuredHeight = h
                    }

                    View.MeasureSpec.AT_MOST -> {
                        h = if (drawableHeight > 0) Math.min(drawableHeight, h) else h
                        mMeasuredWidth = h * usedRatio
                        mMeasuredHeight = h
                    }

                    View.MeasureSpec.UNSPECIFIED -> {
                        w = if (drawableWidth > 0) drawableWidth else w
                        mMeasuredWidth = w
                        mMeasuredHeight = w / usedRatio
                    }
                }
            }

            //Be whatever you want
            else -> {
                w = if (drawableWidth > 0) drawableWidth else w
                mMeasuredWidth = w
                mMeasuredHeight = w / usedRatio

                when (hMode) {
                    View.MeasureSpec.EXACTLY -> {
                        mMeasuredWidth = h * usedRatio
                        mMeasuredHeight = h
                    }

                    View.MeasureSpec.AT_MOST -> {
                        h = if (drawableHeight > 0) Math.min(drawableHeight, h) else h
                        mMeasuredWidth = h * usedRatio
                        mMeasuredHeight = h
                    }

                    View.MeasureSpec.UNSPECIFIED -> {
                        w = if (drawableWidth > 0) drawableWidth else w
                        mMeasuredWidth = w
                        mMeasuredHeight = w / usedRatio
                    }
                }
            }

        }
    }

    /**
     * Sets the custom matrix to be used with the MATRIX scale type
     *
     * @param matrix Custom matrix to be used with the MATRIX scale type
     */
    fun setImageMatrix(matrix: Matrix) {
        this.mImageMatrix = matrix
        setScaleType(PivShapeScaleType.MATRIX)
    }

    /**
     * Sets the scale type used to draw the image
     *
     * @param scaleType Scale type used to draw the image
     */
    fun setScaleType(scaleType: PivShapeScaleType?) {

        mScaleType = scaleType

        if (mDrawable == null || scaleType == null)
            return

        mShaderMatrix.reset()

        //scale used for vector drawable fix (other bitmaps have dwidth = bitmap width)
        var scaleX = 1f
        var scaleY = 1f
        val dWidth = mDrawable?.intrinsicWidth ?: 0
        val dHeight = mDrawable?.intrinsicHeight ?: 0

        if (mLastBitmap != null) {
            scaleX = dWidth.toFloat() / mLastBitmap!!.width.toFloat()
            scaleY = dHeight.toFloat() / mLastBitmap!!.height.toFloat()
        }

        val vWidth = mImageBounds.width()
        val vHeight = mImageBounds.height()
        val padding = Rect(0, 0, 0, 0)

        if (mView.get() != null) {
            padding.set(
                    mView.get()?.paddingLeft ?: padding.left,
                    mView.get()?.paddingBottom ?: padding.right,
                    mView.get()?.paddingRight ?: padding.bottom,
                    mView.get()?.paddingTop ?: padding.top)
        }

        val scale: Float
        val dx: Float
        val dy: Float

        when (scaleType) {

            PivShapeScaleType.CENTER_CROP -> {
                if (dWidth * vHeight > vWidth * dHeight) {
                    scale = vHeight / dHeight.toFloat()
                    dx = (vWidth - dWidth * scale) * 0.5f
                    dy = 0f

                } else {
                    scale = vWidth / dWidth.toFloat()
                    dy = (vHeight - dHeight * scale) * 0.5f
                    dx = 0f
                }

                mShaderMatrix.setScale(scale * scaleX, scale * scaleY)
                mShaderMatrix.postTranslate(dx + mImageBounds.left, dy + mImageBounds.top)
            }

            PivShapeScaleType.TOP_CROP -> {
                if (dWidth * vHeight > vWidth * dHeight) {
                    scale = vHeight / dHeight.toFloat()
                    dx = (vWidth - dWidth * scale) * 0.5f
                    dy = 0f

                } else {
                    scale = vWidth / dWidth.toFloat()
                    dy = 0f
                    dx = 0f
                }

                mShaderMatrix.setScale(scale * scaleX, scale * scaleY)
                mShaderMatrix.postTranslate(dx + mImageBounds.left, dy + mImageBounds.top)
            }

            PivShapeScaleType.BOTTOM_CROP -> {
                if (dWidth * vHeight > vWidth * dHeight) {
                    scale = vHeight / dHeight.toFloat()
                    dx = (vWidth - dWidth * scale) * 0.5f
                    dy = 0f

                } else {
                    scale = vWidth / dWidth.toFloat()
                    dy = vHeight - dHeight * scale
                    dx = 0f
                }

                mShaderMatrix.setScale(scale * scaleX, scale * scaleY)
                mShaderMatrix.postTranslate(dx + mImageBounds.left, dy + mImageBounds.top)
            }

            PivShapeScaleType.CENTER_INSIDE -> {
                if (dWidth <= vWidth && dHeight <= vHeight) {
                    scale = 1.0f
                } else {
                    scale = Math.min(vWidth / dWidth.toFloat(), vHeight / dHeight.toFloat())
                }

                dx = (vWidth - dWidth * scale) * 0.5f
                dy = (vHeight - dHeight * scale) * 0.5f

                mShaderMatrix.setScale(scale * scaleX, scale * scaleY)
                mShaderMatrix.postTranslate(dx + mImageBounds.left, dy + mImageBounds.top)
            }

            PivShapeScaleType.FIT_CENTER -> mShaderMatrix.setRectToRect(RectF(0f, 0f, (mLastBitmap?.width ?: dWidth).toFloat(), (mLastBitmap?.height ?: dHeight).toFloat()), mImageBounds, Matrix.ScaleToFit.CENTER)


            PivShapeScaleType.FIT_END -> mShaderMatrix.setRectToRect(RectF(0f, 0f, (mLastBitmap?.width ?: dWidth).toFloat(), (mLastBitmap?.height ?: dHeight).toFloat()), mImageBounds, Matrix.ScaleToFit.END)

            PivShapeScaleType.FIT_START -> mShaderMatrix.setRectToRect(RectF(0f, 0f, (mLastBitmap?.width ?: dWidth).toFloat(), (mLastBitmap?.height ?: dHeight).toFloat()), mImageBounds, Matrix.ScaleToFit.START)


            PivShapeScaleType.FIT_XY -> mShaderMatrix.setRectToRect(RectF(0f, 0f, (mLastBitmap?.width ?: dWidth).toFloat(), (mLastBitmap?.height ?: dHeight).toFloat()), mImageBounds, Matrix.ScaleToFit.FILL)


            PivShapeScaleType.MATRIX -> {
                mShaderMatrix.preScale(scaleX, scaleY)

                if (mImageMatrix != null)
                    mShaderMatrix.set(mImageMatrix)

                mShaderMatrix.postTranslate(mImageBounds.left, mImageBounds.top)
            }

            PivShapeScaleType.CENTER -> {
                mShaderMatrix.setScale(scaleX, scaleY)
                mShaderMatrix.postTranslate(
                        (vWidth - dWidth) * 0.5f + mImageBounds.left,
                        (vHeight - dHeight) * 0.5f + mImageBounds.top)
            }

            else -> {
                mShaderMatrix.setScale(scaleX, scaleY)
                mShaderMatrix.postTranslate((vWidth - dWidth) * 0.5f + mImageBounds.left, (vHeight - dHeight) * 0.5f + mImageBounds.top)
            }
        }

        mShapeDrawer.setMatrix(scaleType, mShaderMatrix)
    }


    /**
     * Changes the shape mode of the image.
     *
     * @param shapeMode mode to change the image into
     */
    fun changeShapeMode(shapeMode: PivShapeMode) {
        if (mShapeMode == shapeMode)
            return

        mShapeMode = shapeMode
        updateDrawers(mShapeMode)
        mShapeDrawer.setup(mShapeOptions)
        mView.get()?.postInvalidate()
    }

    /** Draws the image  */
    fun onDraw(canvas: Canvas) = mShapeDrawer.draw(canvas, mBorderBounds, mShapeBounds, mImageBounds)

    /** @return The options of the shape */
    fun getShapeOptions(): ShapeOptions = mShapeOptions


    /** Called when an option that requires onMeasure() call is updated. */
    override fun onRequestMeasure(options: ShapeOptions) {
        mShapeOptions = options
        mView.get()?.requestLayout()
        mShapeDrawer.setup(options)
    }

    /**
     * Called when an option is updated. It propagates the update to the shape drawers.
     */
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
        //set calculated bounds to our progress bounds
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
     *
     * @return Measured height to be used in onMeasure() method of the view.
     */
    fun getMeasuredHeight(): Int = mMeasuredHeight.toInt()

    /**
     * Returns the measured width to be used in onMeasure() method of the view.
     * It is calculated based on the shape of the image and its size.
     *
     * @return Measured width to be used in onMeasure() method of the view.
     */
    fun getMeasuredWidth(): Int = mMeasuredWidth.toInt()

    /** @return The shape selected mode */
    fun getShapeMode(): PivShapeMode = mShapeMode


    /** Saves state into a bundle.  */
    fun saveInstanceState(): Bundle {
        val bundle = Bundle()
        bundle.putParcelable("shape_options", mShapeOptions)
        bundle.putInt("shape_mode", mShapeMode.value)

        return bundle
    }

    /** Restores state from a bundle.  */
    fun restoreInstanceState(state: Bundle?) {
        if (state == null)
            return

        mShapeOptions.setOptions(state.getParcelable<Parcelable>("shape_options") as ShapeOptions)
        val shapeMode = PivShapeMode.fromValue(state.getInt("shape_mode"))
        onSizeUpdated(mShapeOptions)
        changeShapeMode(shapeMode)
    }
}
