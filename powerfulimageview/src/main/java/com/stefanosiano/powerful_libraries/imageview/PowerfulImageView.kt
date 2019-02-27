package com.stefanosiano.powerful_libraries.imageview


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView

import com.stefanosiano.powerful_libraries.imageview.blur.algorithms.BlurManager
import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions
import com.stefanosiano.powerful_libraries.imageview.blur.PivBlurMode
import com.stefanosiano.powerful_libraries.imageview.progress.PivProgressGravity
import com.stefanosiano.powerful_libraries.imageview.progress.PivProgressMode
import com.stefanosiano.powerful_libraries.imageview.progress.ProgressOptions
import com.stefanosiano.powerful_libraries.imageview.progress.drawers.ProgressDrawerManager
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeMode
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeScaleType
import com.stefanosiano.powerful_libraries.imageview.shape.ShapeOptions
import com.stefanosiano.powerful_libraries.imageview.shape.drawers.ShapeDrawerManager

/**
 * Powerful ImageView with several added features (highly customizable):
 *     -Progress indicator: it can be circular, horizontal or disabled.
 *     -Shapes: it can be normal, circle, solid_circle, oval, solid_oval, rounded_rectangle, solid_rounded_rectangle, rectangle, square.
 *     -Blur: it can use several algorithms, like gaussian, box and stack blur
 *
 * It extends AppCompatImageView, allowing the use of VectorDrawables and all AppCompat stuff.
 * The downside is that it needs the Android appcompat-v7 library.
 */
open class PowerfulImageView : ImageViewWrapper {

    //Progress initialization constants
    private val DEFAULT_PROGRESS_USE_DETERMINATE_ANIMATION = true
    private val DEFAULT_PROGRESS_ANIMATION_DURATION: Int = 100
    private val DEFAULT_PROGRESS_INDETEMINATE_ANIMATION_DURATION: Int = 600
    private val DEFAULT_PROGRESS_WIDTH = -1
    private val DEFAULT_PROGRESS_WIDTH_PERCENT = 10f
    private val DEFAULT_PROGRESS_SIZE = -1
    private val DEFAULT_PROGRESS_SIZE_PERCENT = 40f
    private val DEFAULT_PROGRESS_PADDING = 2
    private val DEFAULT_PROGRESS_VALUE = 0
    private val DEFAULT_PROGRESS_GRAVITY = PivProgressGravity.CENTER.value
    private val DEFAULT_PROGRESS_DISABLE_RTL_SUPPORT = false
    private val DEFAULT_PROGRESS_INDETERMINATE = true
    private val DEFAULT_PROGRESS_DETERMINATE_DRAW_WEDGE = false
    private val DEFAULT_PROGRESS_SHADOW_ENABLED = true
    private val DEFAULT_PROGRESS_REVERSED = false
    private val DEFAULT_PROGRESS_REMOVED_ON_CHANGE = true
    private val DEFAULT_PROGRESS_MODE = PivProgressMode.NONE.value
    private val DEFAULT_PROGRESS_SHADOW_PADDING = -1
    private val DEFAULT_PROGRESS_SHADOW_PADDING_PERCENT = 0f
    private val DEFAULT_PROGRESS_SHADOW_BORDER_WIDTH = 1

    //Shape initialization constants
    private val DEFAULT_SHAPE_MODE = PivShapeMode.NORMAL.value
    private val DEFAULT_SHAPE_INNER_PADDING = -1
    private val DEFAULT_SHAPE_INNER_PADDING_PERCENT = 0f
    private val DEFAULT_SHAPE_BORDER_OVERLAY = false
    private val DEFAULT_SHAPE_BORDER_WIDTH = 0
    private val DEFAULT_SHAPE_RATIO = 0f
    private val DEFAULT_SHAPE_RADIUS_X = 1f
    private val DEFAULT_SHAPE_RADIUS_Y = 1f

    //Blur initialization constants
    private val DEFAULT_BLUR_RADIUS = 0
    private val DEFAULT_BLUR_DOWNSAMPLING_RATE = 4f
    private val DEFAULT_BLUR_USE_RENDERSCRIPT_FALLBACK = true
    private val DEFAULT_BLUR_NUM_THREADS = 0
    private val DEFAULT_BLUR_STATIC = false
    private val DEFAULT_BLUR_MODE = PivBlurMode.DISABLED.value

    /** Helper class to manage the progress indicator and its options  */
    private val mProgressDrawerManager: ProgressDrawerManager

    /** Helper class to manage the shape of the image and its options  */
    private val mShapeDrawerManager: ShapeDrawerManager

    /** Helper class to manage the blurring of the image and its options  */
    private val mBlurManager: BlurManager

    /** Flag used to control if blurring bitmap should be checked  */
    private var mCheckBlur = false

    /** Flag used to control if I should try to remove the progress on drawable changed (used in constructor and on size change)  */
    private var mShouldCheckRemoveProgress = true

    private var initialized = false


    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initialized = true

        val a = context.theme.obtainStyledAttributes(attrs, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView, defStyleAttr, 0)

        //using typed values to remove useless redundancy of attributes (dimension and percentage)
        val tvSize = TypedValue()
        val tvBorderWidth = TypedValue()
        val tvShadowPadding = TypedValue()
        a.getValue(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_size, tvSize)
        a.getValue(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_border_width, tvBorderWidth)
        a.getValue(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_shadow_padding, tvShadowPadding)


        val progressMode = PivProgressMode.fromValue(a.getInteger(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_mode, DEFAULT_PROGRESS_MODE))
        val useDeterminateAnimation = a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_determinate_animation_enabled, DEFAULT_PROGRESS_USE_DETERMINATE_ANIMATION)

        //get all the options from xml or default constants and initialize ProgressOptions object
        val progressOptions = ProgressOptions(
                //Using animations will cause the progress not to be drawn immediately in layout editor
                if(isInEditMode) false else useDeterminateAnimation,
                a.getInt(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_animation_duration, if(useDeterminateAnimation) DEFAULT_PROGRESS_INDETEMINATE_ANIMATION_DURATION else DEFAULT_PROGRESS_ANIMATION_DURATION),
                if (tvBorderWidth.type == TypedValue.TYPE_DIMENSION) tvBorderWidth.getDimension(resources.displayMetrics).toInt() else DEFAULT_PROGRESS_WIDTH,
                if (tvBorderWidth.type == TypedValue.TYPE_FRACTION) tvBorderWidth.getFraction(100f, 100f) else DEFAULT_PROGRESS_WIDTH_PERCENT,
                if (tvSize.type == TypedValue.TYPE_DIMENSION) tvSize.getDimension(resources.displayMetrics).toInt() else DEFAULT_PROGRESS_SIZE,
                if (tvSize.type == TypedValue.TYPE_FRACTION) tvSize.getFraction(100f, 100f) else DEFAULT_PROGRESS_SIZE_PERCENT,
                a.getDimensionPixelSize(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_padding, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROGRESS_PADDING.toFloat(), resources.displayMetrics).toInt()),
                a.getFloat(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_value, DEFAULT_PROGRESS_VALUE.toFloat()),
                getColor(a, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_front_color, com.stefanosiano.powerful_libraries.imageview.R.color.piv_default_progress_front_color),
                getColor(a, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_back_color, com.stefanosiano.powerful_libraries.imageview.R.color.piv_default_progress_back_color),
                getColor(a, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_indeterminate_color, com.stefanosiano.powerful_libraries.imageview.R.color.piv_default_progress_indeterminate_color),
                a.getInteger(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_gravity, DEFAULT_PROGRESS_GRAVITY),
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && layoutDirection == View.LAYOUT_DIRECTION_RTL,
                a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_rtl_disabled, DEFAULT_PROGRESS_DISABLE_RTL_SUPPORT),
                a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_indeterminate, DEFAULT_PROGRESS_INDETERMINATE),
                a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_draw_wedge, DEFAULT_PROGRESS_DETERMINATE_DRAW_WEDGE),
                a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_shadow_enabled, DEFAULT_PROGRESS_SHADOW_ENABLED),
                getColor(a, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_shadow_color, com.stefanosiano.powerful_libraries.imageview.R.color.piv_default_progress_shadow_color),
                if (tvShadowPadding.type == TypedValue.TYPE_DIMENSION) tvShadowPadding.getDimension(resources.displayMetrics).toInt() else DEFAULT_PROGRESS_SHADOW_PADDING,
                if (tvShadowPadding.type == TypedValue.TYPE_FRACTION) tvShadowPadding.getFraction(100f, 100f) else DEFAULT_PROGRESS_SHADOW_PADDING_PERCENT,
                a.getDimensionPixelSize(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_shadow_border_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROGRESS_SHADOW_BORDER_WIDTH.toFloat(), resources.displayMetrics).toInt()).toFloat(),
                getColor(a, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_shadow_border_color, com.stefanosiano.powerful_libraries.imageview.R.color.piv_default_progress_shadow_border_color),
                a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_reversed, DEFAULT_PROGRESS_REVERSED),
                a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_progress_removed_on_change, DEFAULT_PROGRESS_REMOVED_ON_CHANGE)
        )


        val tvShapeInnerPadding = TypedValue()
        a.getValue(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_inner_padding, tvShapeInnerPadding)

        val shapeOptions = ShapeOptions(
                getColor(a, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_background_color, android.R.color.transparent),
                getColor(a, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_foreground_color, android.R.color.transparent),
                if (tvShapeInnerPadding.type == TypedValue.TYPE_DIMENSION) tvShapeInnerPadding.getDimension(resources.displayMetrics).toInt() else DEFAULT_SHAPE_INNER_PADDING,
                if (tvShapeInnerPadding.type == TypedValue.TYPE_FRACTION) tvShapeInnerPadding.getFraction(100f, 100f) else DEFAULT_SHAPE_INNER_PADDING_PERCENT,
                a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_border_overlay, DEFAULT_SHAPE_BORDER_OVERLAY),
                getColor(a, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_border_color, android.R.color.transparent),
                a.getDimensionPixelSize(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_border_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SHAPE_BORDER_WIDTH.toFloat(), resources.displayMetrics).toInt()),
                a.getFloat(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_ratio, DEFAULT_SHAPE_RATIO),
                a.getFloat(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_radius_x, DEFAULT_SHAPE_RADIUS_X),
                a.getFloat(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_radius_y, DEFAULT_SHAPE_RADIUS_Y),
                getColor(a, com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_solid_color, com.stefanosiano.powerful_libraries.imageview.R.color.piv_default_shape_solid_color),
                a.getDrawable(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_background),
                a.getDrawable(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_foreground)
        )


        val shapeMode = PivShapeMode.fromValue(a.getInteger(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_mode, DEFAULT_SHAPE_MODE))
        //I use the android scale type used as default. If a PivShapeScaleType type is passed, it overrides Android scaleType
        val scaleType = PivShapeScaleType.fromValue(a.getInteger(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_shape_scaleType, PivShapeScaleType.getFromScaleType(scaleType).value))


        val blurOptions = BlurOptions(
                a.getFloat(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_blur_down_sampling_rate, DEFAULT_BLUR_DOWNSAMPLING_RATE),
                a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_blur_static, DEFAULT_BLUR_STATIC),
                a.getBoolean(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_blur_use_rs_fallback, DEFAULT_BLUR_USE_RENDERSCRIPT_FALLBACK),
                a.getInteger(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_blur_num_threads, DEFAULT_BLUR_NUM_THREADS)
        )

        val blurRadius = a.getInteger(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_blur_radius, DEFAULT_BLUR_RADIUS)
        val blurModeValue = a.getInteger(com.stefanosiano.powerful_libraries.imageview.R.styleable.PowerfulImageView_piv_blur_mode, DEFAULT_BLUR_MODE)
        val blurMode = PivBlurMode.fromValue(blurModeValue)

        a.recycle()

        this.mProgressDrawerManager = ProgressDrawerManager(this, progressOptions)
        this.mShapeDrawerManager = ShapeDrawerManager(this, shapeOptions)
        this.mBlurManager = BlurManager(this, blurOptions)
        mShapeDrawerManager.setScaleType(scaleType)
        mShouldCheckRemoveProgress = false

        //the first time it was called, mShapeDrawerManager is null, so it's skipped.
        //So i call it here, after everything else is instantiated.
        onDrawableChanged()

        setProgressMode(progressMode)
        setShapeMode(shapeMode)
        setBlurMode(blurMode, blurRadius)

        mShouldCheckRemoveProgress = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //if it's called in super constructor, I don't have the objects instantiated
        if(!initialized) return

        //if size didn't change, i don't update anything!
        if(w == oldw && h == oldh) return

        //updates progress bounds
        mProgressDrawerManager.onSizeChanged(w, h)

        mShapeDrawerManager.onSizeChanged(w, h, paddingLeft, paddingTop, paddingRight, paddingBottom)

        mBlurManager.onSizeChanged(mShapeDrawerManager.getMeasuredWidth(), mShapeDrawerManager.getMeasuredHeight(), drawable?.current ?: drawable)

        mShouldCheckRemoveProgress = false
        blurBitmap(false)
        mShouldCheckRemoveProgress = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //if it's called in super constructor, I don't have the objects instantiated
        //I go further only if there is a custom shape selected
        if (!initialized)// || mShapeDrawerManager.getShapeMode() == PivShapeMode.NORMAL)
            return super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        mShapeDrawerManager.onMeasure(widthSize.toFloat(), heightSize.toFloat(), widthMode, heightMode, this)

        //MUST CALL THIS
        setMeasuredDimension(mShapeDrawerManager.getMeasuredWidth(), mShapeDrawerManager.getMeasuredHeight())
    }

    /** Method called when the drawable has been changed */
    override fun onDrawableChanged() {

        //if it's called in super constructor, I don't have the objects instantiated
        if(!initialized) return

        //if the image comes from super methods and i need to blur it, I blur it
        if (blurBitmap(true)) return

        //when initializing (in constructor) it gets called, but it is still null
        if (mShouldCheckRemoveProgress && drawable != null)
            mProgressDrawerManager.changeDrawable(drawable.current ?: drawable)

        //when initializing (in constructor) it gets called, but it is still null
        if (drawable != null)
            mShapeDrawerManager.changeDrawable(drawable.current ?: drawable)

    }

    override fun setScaleType(scaleType: ImageView.ScaleType) {
        super.setScaleType(scaleType)

        //if it's called in super constructor, I don't have the objects instantiated
        if(!initialized) return

        mShapeDrawerManager.setScaleType(PivShapeScaleType.getFromScaleType(scaleType))
    }

    /**
     *
     * Controls how the image should be resized or moved to match the size of this ImageView.
     * Added to provide additional custom scale types.
     * Overrides ImageView's changeScaleType(ImageView.ScaleType) method.
     *
     * @param scaleType The desired scaling mode.
     */
    fun setShapeScaleType(scaleType: PivShapeScaleType) {
        super.setScaleType(ImageView.ScaleType.MATRIX)
        mShapeDrawerManager.setScaleType(scaleType)
    }


    override fun setImageMatrix(matrix: Matrix) {
        if(!initialized) return super.setImageMatrix(matrix)
        super.setImageMatrix(matrix)
        mShapeDrawerManager.setImageMatrix(matrix)
    }

    override fun onDraw(canvas: Canvas) {
        if(!initialized) return super.onDraw(canvas)
        //draw image shape
        mShapeDrawerManager.onDraw(canvas)

        //draw progress indicator
        mProgressDrawerManager.onDraw(canvas)
    }


    /**
     * Changes the progress mode of the indicator (e.g. passing from determinate to indeterminate).
     * It also starts animation of indeterminate progress indicator.
     *
     * @param progressMode mode to change the progress indicator into
     */
    fun setProgressMode(progressMode: PivProgressMode) = mProgressDrawerManager.changeProgressMode(progressMode, false)


    /**
     * Changes the shape of the image.
     *
     * @param shapeMode shape to change the image into
     */
    fun setShapeMode(shapeMode: PivShapeMode) = mShapeDrawerManager.changeShapeMode(shapeMode)


    /**
     * Changes the blur mode and the radius to blur the image.
     *
     * @param blurMode mode to use to blur the image
     * @param radius radius to use when blurring the image: the higher the radius, the more the blurring.
     */
    fun setBlurMode(blurMode: PivBlurMode, radius: Int) {
        mCheckBlur = blurMode != PivBlurMode.DISABLED

        mBlurManager.changeMode(blurMode, radius)
        blurBitmap(false)
    }


    /**
     * Changes the blur radius to blur the image.
     *
     * @param radius radius to use when blurring the image: the higher the radius, the more but slower the blurring.
     */
    fun setBlurRadius(radius: Int) {
        mCheckBlur = mBlurManager.getBlurMode() !== PivBlurMode.DISABLED

        mBlurManager.changeRadius(radius)
        blurBitmap(false)
    }

    /**
     * Blur the image, if needed.
     * @return True if the image is blurred and will be set through setBitmap()
     * False if the image doesn't need to be blurred
     */
    private fun blurBitmap(changeDrawable: Boolean): Boolean {

        if (!mCheckBlur || drawable == null)
            return false

        val shouldBlur = mBlurManager.shouldBlur(drawable.current, changeDrawable)

        if (changeDrawable)
            mBlurManager.changeDrawable(drawable.current)

        var blurredBitmap: Bitmap? = null
        if (shouldBlur) {
            blurredBitmap = mBlurManager.getLastBlurredBitmap()

            if (blurredBitmap != null) {
                mCheckBlur = false
                setImageBitmap(blurredBitmap)
                mCheckBlur = true
            }
        }

        return shouldBlur && blurredBitmap != null && !blurredBitmap.isRecycled
    }


    /** @param isIndeterminate whether the progress indicator is indeterminate or not */
    fun setProgressIndeterminate(isIndeterminate: Boolean) = mProgressDrawerManager.getProgressOptions().setIsIndeterminate(isIndeterminate)

    /** @return The options of the progress indicator */
    fun getProgressOptions(): ProgressOptions = mProgressDrawerManager.getProgressOptions()

    /** @return The progress, expressed as a percentage, from 0 to 100 */
    fun getProgress(): Float = mProgressDrawerManager.getProgressOptions().valuePercent

    /** @return The selected progress mode */
    fun getProgressMode(): PivProgressMode = mProgressDrawerManager.getProgressMode()

    /** @return The selected shape mode */
    fun getShapeMode(): PivShapeMode = mShapeDrawerManager.getShapeMode()

    /** @return The selected shape mode */
    fun getBlurMode(): PivBlurMode = mBlurManager.getBlurMode()

    /** @return The options of the shape */
    fun getShapeOptions(): ShapeOptions = mShapeDrawerManager.getShapeOptions()

    /** @return The options of the blur */
    fun getBlurOptions(): BlurOptions = mBlurManager.getBlurOptions()

    /** @return The selected radius used for blurring */
    fun getBlurRadius(): Int = mBlurManager.getRadius()

    /**
     * Returns the last blurred bitmap. If the bitmap was never blurred, or blur options, mode or radius
     * changed since the last blur, the bitmap will be blurred again (if static option is disabled).
     *
     * Don't use this method if you didn't enable blur!
     *
     * @return The blurred bitmap. If any problem occurs, the original bitmap (nullable) will be returned.
     */
    fun getBlurBlurredBitmap(): Bitmap? = mBlurManager.getLastBlurredBitmap()

    /**
     * Returns the original bitmap used to blur. If static blur option is enabled, this will be the
     * same as the blurred one, since the original bitmap has been released.
     *
     * Don't use this method if you didn't enable blur!
     *
     * @return The original bitmap, or the blurred one if static blur is enabled.
     */
    fun getBlurOriginalBitmap(): Bitmap? = mBlurManager.getOriginalBitmap()

    /**
     * Sets the progress of the current indicator.
     * If the drawer is indeterminate, it will change its state and make it determinate.
     *
     * @param progress Percentage value of the progress
     */
    fun setProgressValue(progress: Float) = getProgressOptions().setValue(progress)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(!initialized) return
        mBlurManager.addContext(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if(!initialized) return
        mBlurManager.removeContext(true)
    }

    /** Save the state of the view.  */
    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("super_state", super.onSaveInstanceState())
        bundle.putParcelable("progress_drawer_manager", mProgressDrawerManager.saveInstanceState())
        bundle.putParcelable("shape_drawer_manager", mShapeDrawerManager.saveInstanceState())
        bundle.putParcelable("blur_manager", mBlurManager.saveInstanceState())

        return bundle
    }

    /** Restore the state of the view.  */
    override fun onRestoreInstanceState(state: Parcelable?) {
        var state = state
        if (state is Bundle) {// implicit null check
            val bundle = state as Bundle?
            mProgressDrawerManager.restoreInstanceState(bundle!!.getParcelable<Parcelable>("progress_drawer_manager") as Bundle)
            mShapeDrawerManager.restoreInstanceState(bundle.getParcelable<Parcelable>("shape_drawer_manager") as Bundle)
            mBlurManager.restoreInstanceState(bundle.getParcelable<Parcelable>("blur_manager") as Bundle)
            state = bundle.getParcelable("super_state")
        }
        super.onRestoreInstanceState(state)
        blurBitmap(true)
    }
}

