package com.stefanosiano.powerful_libraries.imageview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions
import com.stefanosiano.powerful_libraries.imageview.blur.PivBlurMode
import com.stefanosiano.powerful_libraries.imageview.blur.algorithms.BlurManager
import com.stefanosiano.powerful_libraries.imageview.progress.PivProgressGravity
import com.stefanosiano.powerful_libraries.imageview.progress.PivProgressMode
import com.stefanosiano.powerful_libraries.imageview.progress.PivShapeCutGravity
import com.stefanosiano.powerful_libraries.imageview.progress.ProgressOptions
import com.stefanosiano.powerful_libraries.imageview.progress.drawers.ProgressDrawerManager
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeMode
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeScaleType
import com.stefanosiano.powerful_libraries.imageview.shape.ShapeOptions
import com.stefanosiano.powerful_libraries.imageview.shape.drawers.ShapeDrawerManager

/**
 * Powerful ImageView with several added features (highly customizable):
 *     -Progress indicator: it can be circular, horizontal or disabled.
 *     -Shapes: it can be normal, circle, solid_circle, oval, solid_oval, rounded_rectangle, solid_rounded_rectangle,
 *      rectangle, square.
 *     -Blur: it can use several algorithms, like gaussian, box and stack blur
 *
 * It extends AppCompatImageView, allowing the use of VectorDrawables and all AppCompat stuff.
 * The downside is that it needs the Android appcompat-v7 library.
 */
@Suppress("MagicNumber", "TooManyFunctions")
open class PowerfulImageView : ImageViewWrapper {

    // Progress initialization constants
    private val defaultProgressUseDeterminateAnimation = true
    private val defaultProgressAnimationDuration: Int = 100
    private val defaultProgressIndeterminateAnimationDuration: Int = 600
    private val defaultProgressWidth = -1
    private val defaultProgressWidthPercent = 10f
    private val defaultProgressSize = -1
    private val defaultProgressSizePercent = 40f
    private val defaultProgressPadding = 2
    private val defaultProgressValue = 0
    private val defaultProgressGravity = PivProgressGravity.CENTER.value
    private val defaultProgressDisableRtlSupport = false
    private val defaultProgressIndeterminate = true
    private val defaultProgressDeterminateDrawWedge = false
    private val defaultProgressShadowEnabled = true
    private val defaultProgressReversed = false
    private val defaultProgressRemovedOnChange = true
    private val defaultProgressMode = PivProgressMode.NONE.value
    private val defaultProgressShadowPadding = -1
    private val defaultProgressShadowPaddingPercent = 0f
    private val defaultProgressShadowBorderWidth = 1

    // Shape initialization constants
    private val defaultShapeMode = PivShapeMode.NORMAL.value
    private val defaultShapeCutGravity = PivShapeCutGravity.BOTTOM.value
    private val defaultShapeInnerPadding = -1
    private val defaultShapeInnerPaddingPercent = 0f
    private val defaultShapeBorderOverlay = false
    private val defaultShapeBorderWidth = 0
    private val defaultShapeRatio = 0f
    private val defaultShapeRadiusX = 1f
    private val defaultShapeRadiusY = 1f
    private val defaultShapeCutRadius1 = 0
    private val defaultShapeCutRadius1Percent = 25f
    private val defaultShapeCutRadius2 = 0
    private val defaultShapeCutRadius2Percent = 100f

    // Blur initialization constants
    private val defaultBlurRadius = 0
    private val defaultBlurDownSamplingRate = 4f
    private val defaultBlurUseRenderscriptFallback = true
    private val defaultBlurNumThreads = 0
    private val defaultBlurStatic = false
    private val defaultBlurMode = PivBlurMode.DISABLED.value

    /** Helper class to manage the progress indicator and its options. */
    private val mProgressDrawerManager: ProgressDrawerManager

    /** Helper class to manage the shape of the image and its options. */
    private val mShapeDrawerManager: ShapeDrawerManager

    /** Helper class to manage the blurring of the image and its options. */
    private val mBlurManager: BlurManager

    /** Flag used to control if blurring bitmap should be checked. */
    private var mCheckBlur = false

    /** Flag used to control if I should try to remove the progress on drawable changed
     * (used in constructor and on size change). */
    private var mShouldCheckRemoveProgress = true

    private var initialized = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialized = true

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.PowerfulImageView, defStyleAttr, 0)

        // Using typed values to remove useless redundancy of attributes (dimension and percentage)
        val tvSize = TypedValue()
        val tvBorderWidth = TypedValue()
        val tvShadowPadding = TypedValue()
        val tvCutRadius1 = TypedValue()
        val tvCutRadius2 = TypedValue()
        a.getValue(R.styleable.PowerfulImageView_pivProgressSize, tvSize)
        a.getValue(R.styleable.PowerfulImageView_pivProgressBorderWidth, tvBorderWidth)
        a.getValue(R.styleable.PowerfulImageView_pivProgressShadowPadding, tvShadowPadding)
        a.getValue(R.styleable.PowerfulImageView_pivShapeCutRadius1, tvCutRadius1)
        a.getValue(R.styleable.PowerfulImageView_pivShapeCutRadius2, tvCutRadius2)

        val progressMode =
            PivProgressMode.fromValue(a.getInteger(R.styleable.PowerfulImageView_pivProgressMode, defaultProgressMode))
        val useDeterminateAnimation = a.getBoolean(
            R.styleable.PowerfulImageView_pivProgressDeterminateAnimationEnabled,
            defaultProgressUseDeterminateAnimation
        )

        // Get all the options from xml or default constants and initialize ProgressOptions object
        val progressOptions = ProgressOptions(
            // Using animations will cause the progress not to be drawn immediately in layout editor
            if (isInEditMode) false else useDeterminateAnimation,
            a.getInt(
                R.styleable.PowerfulImageView_pivProgressAnimationDuration,
                if (useDeterminateAnimation) {
                    defaultProgressIndeterminateAnimationDuration
                } else {
                    defaultProgressAnimationDuration
                }
            ),
            if (tvBorderWidth.type == TypedValue.TYPE_DIMENSION) {
                tvBorderWidth.getDimension(resources.displayMetrics).toInt()
            } else {
                defaultProgressWidth
            },
            if (tvBorderWidth.type == TypedValue.TYPE_FRACTION) {
                tvBorderWidth.getFraction(100f, 100f)
            } else {
                defaultProgressWidthPercent
            },
            if (tvSize.type == TypedValue.TYPE_DIMENSION) {
                tvSize.getDimension(resources.displayMetrics).toInt()
            } else {
                defaultProgressSize
            },
            if (tvSize.type == TypedValue.TYPE_FRACTION) {
                tvSize.getFraction(100f, 100f)
            } else {
                defaultProgressSizePercent
            },
            a.getDimensionPixelSize(
                R.styleable.PowerfulImageView_pivProgressPadding,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    defaultProgressPadding.toFloat(),
                    resources.displayMetrics
                ).toInt()
            ),
            a.getFloat(R.styleable.PowerfulImageView_pivProgressValue, defaultProgressValue.toFloat()),
            getColor(
                a,
                R.styleable.PowerfulImageView_pivProgressFrontColor,
                R.color.piv_default_progress_front_color
            ),
            getColor(
                a,
                R.styleable.PowerfulImageView_pivProgressBackColor,
                R.color.piv_default_progress_back_color
            ),
            getColor(
                a,
                R.styleable.PowerfulImageView_pivProgressIndeterminateColor,
                R.color.piv_default_progress_indeterminate_color
            ),
            a.getInteger(R.styleable.PowerfulImageView_pivProgressGravity, defaultProgressGravity),
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && layoutDirection == LAYOUT_DIRECTION_RTL,
            a.getBoolean(R.styleable.PowerfulImageView_pivProgressRtlDisabled, defaultProgressDisableRtlSupport),
            a.getBoolean(R.styleable.PowerfulImageView_pivProgressIndeterminate, defaultProgressIndeterminate),
            a.getBoolean(R.styleable.PowerfulImageView_pivProgressDrawWedge, defaultProgressDeterminateDrawWedge),
            a.getBoolean(R.styleable.PowerfulImageView_pivProgressShadowEnabled, defaultProgressShadowEnabled),
            getColor(
                a,
                R.styleable.PowerfulImageView_pivProgressShadowColor,
                R.color.piv_default_progress_shadow_color
            ),
            if (tvShadowPadding.type == TypedValue.TYPE_DIMENSION) {
                tvShadowPadding.getDimension(resources.displayMetrics).toInt()
            } else {
                defaultProgressShadowPadding
            },
            if (tvShadowPadding.type == TypedValue.TYPE_FRACTION) {
                tvShadowPadding.getFraction(100f, 100f)
            } else {
                defaultProgressShadowPaddingPercent
            },
            a.getDimensionPixelSize(
                R.styleable.PowerfulImageView_pivProgressShadowBorderWidth,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    defaultProgressShadowBorderWidth.toFloat(),
                    resources.displayMetrics
                ).toInt()
            ).toFloat(),
            getColor(
                a,
                R.styleable.PowerfulImageView_pivProgressShadowBorderColor,
                R.color.piv_default_progress_shadow_border_color
            ),
            a.getBoolean(R.styleable.PowerfulImageView_pivProgressReversed, defaultProgressReversed),
            a.getBoolean(R.styleable.PowerfulImageView_pivProgressRemovedOnChange, defaultProgressRemovedOnChange)
        )

        val tvShapeInnerPadding = TypedValue()
        a.getValue(R.styleable.PowerfulImageView_pivShapeInnerPadding, tvShapeInnerPadding)

        val shapeOptions = ShapeOptions(
            getColor(a, R.styleable.PowerfulImageView_pivShapeBackgroundColor, android.R.color.transparent),
            getColor(a, R.styleable.PowerfulImageView_pivShapeForegroundColor, android.R.color.transparent),
            if (tvShapeInnerPadding.type == TypedValue.TYPE_DIMENSION) {
                tvShapeInnerPadding.getDimension(resources.displayMetrics).toInt()
            } else {
                defaultShapeInnerPadding
            },
            if (tvShapeInnerPadding.type == TypedValue.TYPE_FRACTION) {
                tvShapeInnerPadding.getFraction(100f, 100f)
            } else {
                defaultShapeInnerPaddingPercent
            },
            a.getBoolean(R.styleable.PowerfulImageView_pivShapeBorderOverlay, defaultShapeBorderOverlay),
            a.getInteger(R.styleable.PowerfulImageView_pivShapeCutGravity, defaultShapeCutGravity),
            getColor(a, R.styleable.PowerfulImageView_pivShapeBorderColor, android.R.color.transparent),
            a.getDimensionPixelSize(
                R.styleable.PowerfulImageView_pivShapeBorderWidth,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    defaultShapeBorderWidth.toFloat(),
                    resources.displayMetrics
                ).toInt()
            ),
            a.getFloat(R.styleable.PowerfulImageView_pivShapeRatio, defaultShapeRatio),
            a.getFloat(R.styleable.PowerfulImageView_pivShapeRadiusX, defaultShapeRadiusX),
            a.getFloat(R.styleable.PowerfulImageView_pivShapeRadiusY, defaultShapeRadiusY),
            getColor(a, R.styleable.PowerfulImageView_pivShapeSolidColor, R.color.piv_default_shape_solid_color),
            a.getDrawable(R.styleable.PowerfulImageView_pivShapeBackground),
            a.getDrawable(R.styleable.PowerfulImageView_pivShapeForeground),
            if (tvCutRadius1.type == TypedValue.TYPE_DIMENSION) {
                tvCutRadius1.getDimension(resources.displayMetrics).toInt()
            } else {
                defaultShapeCutRadius1
            },
            if (tvCutRadius1.type == TypedValue.TYPE_FRACTION) {
                tvCutRadius1.getFraction(100f, 100f)
            } else {
                defaultShapeCutRadius1Percent
            },
            if (tvCutRadius2.type == TypedValue.TYPE_DIMENSION) {
                tvCutRadius2.getDimension(resources.displayMetrics).toInt()
            } else {
                defaultShapeCutRadius2
            },
            if (tvCutRadius2.type == TypedValue.TYPE_FRACTION) {
                tvCutRadius2.getFraction(100f, 100f)
            } else {
                defaultShapeCutRadius2Percent
            }
        )

        val shapeMode =
            PivShapeMode.fromValue(a.getInteger(R.styleable.PowerfulImageView_pivShapeMode, defaultShapeMode))
        // I use the android scale type used as default, overridden if a PivShapeScaleType type is passed
        val scaleType = PivShapeScaleType.fromValue(
            a.getInteger(
                R.styleable.PowerfulImageView_pivShapeScaleType,
                PivShapeScaleType.getFromScaleType(scaleType).value
            )
        )

        val blurOptions = BlurOptions(
            a.getFloat(R.styleable.PowerfulImageView_pivBlurDownSamplingRate, defaultBlurDownSamplingRate),
            a.getBoolean(R.styleable.PowerfulImageView_pivBlurStatic, defaultBlurStatic),
            a.getBoolean(R.styleable.PowerfulImageView_pivBlurUseRsFallback, defaultBlurUseRenderscriptFallback),
            a.getInteger(R.styleable.PowerfulImageView_pivBlurNumThreads, defaultBlurNumThreads)
        )

        val blurRadius = a.getInteger(R.styleable.PowerfulImageView_pivBlurRadius, defaultBlurRadius)
        val blurModeValue = a.getInteger(R.styleable.PowerfulImageView_pivBlurMode, defaultBlurMode)
        val blurMode = PivBlurMode.fromValue(blurModeValue)

        a.recycle()

        this.mProgressDrawerManager = ProgressDrawerManager(this, progressOptions)
        this.mShapeDrawerManager = ShapeDrawerManager(this, shapeOptions)
        this.mBlurManager = BlurManager(this, blurOptions)
        mShapeDrawerManager.setScaleType(scaleType)
        mShouldCheckRemoveProgress = false

        // The first time it was called, mShapeDrawerManager is null, so it's skipped.
        // So i call it here, after everything else is instantiated.
        onDrawableChanged()

        setProgressMode(progressMode)
        setShapeMode(shapeMode)
        setBlurMode(blurMode, blurRadius)

        mShouldCheckRemoveProgress = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // If it's called in super constructor, I don't have the objects instantiated
        if (!initialized) return

        // If size didn't change, i don't update anything!
        if (w == oldw && h == oldh) return

        // Updates progress bounds
        mProgressDrawerManager.onSizeChanged(w, h)

        mShapeDrawerManager.onSizeChanged(w, h, Rect(paddingLeft, paddingTop, paddingRight, paddingBottom))

        mBlurManager.onSizeChanged(
            mShapeDrawerManager.getMeasuredWidth(),
            mShapeDrawerManager.getMeasuredHeight(),
            drawable?.current ?: drawable
        )

        mShouldCheckRemoveProgress = false
        blurBitmap(false)
        mShouldCheckRemoveProgress = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // If it's called in super constructor, I don't have the objects instantiated
        // I go further only if there is a custom shape selected
        if (!initialized) { // || mShapeDrawerManager.getShapeMode() == PivShapeMode.NORMAL)
            return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        mShapeDrawerManager.onMeasure(widthSize.toFloat(), heightSize.toFloat(), widthMode, heightMode, this)

        // MUST CALL THIS
        setMeasuredDimension(mShapeDrawerManager.getMeasuredWidth(), mShapeDrawerManager.getMeasuredHeight())
    }

    /** Method called when the drawable has been changed. */
    override fun onDrawableChanged() {
        // If it's called in super constructor, I don't have the objects instantiated
        if (!initialized) return

        // If the image comes from super methods and i need to blur it, I blur it
        if (blurBitmap(true)) return

        // When initializing (in constructor) it gets called, but it is still null
        if (mShouldCheckRemoveProgress && drawable != null) {
            mProgressDrawerManager.changeDrawable()
        }

        // When initializing (in constructor) it gets called, but it is still null
        if (drawable != null) {
            mShapeDrawerManager.changeDrawable(drawable.current)
        }
    }

    override fun setScaleType(scaleType: ScaleType) {
        super.setScaleType(scaleType)

        // If it's called in super constructor, I don't have the objects instantiated
        if (!initialized) return

        mShapeDrawerManager.setScaleType(PivShapeScaleType.getFromScaleType(scaleType))
    }

    /**
     * Set the [scaleType] to control how the image should be resized or moved to match the size of this ImageView.
     * Added to provide additional custom scale types.
     */
    fun setShapeScaleType(scaleType: PivShapeScaleType) {
        super.setScaleType(ScaleType.MATRIX)
        mShapeDrawerManager.setScaleType(scaleType)
    }

    /**
     * Returns the [PivShapeScaleType] used from this ImageView.
     * If a normal [android.widget.ImageView.ScaleType] was set, the corresponding [PivShapeScaleType] is returned.
     */
    fun getShapeScaleType() = mShapeDrawerManager.getScaleType() ?: PivShapeScaleType.getFromScaleType(scaleType)

    override fun setImageMatrix(matrix: Matrix) {
        if (!initialized) return super.setImageMatrix(matrix)
        super.setImageMatrix(matrix)
        mShapeDrawerManager.setImageMatrix(matrix)
    }

    override fun onDraw(canvas: Canvas) {
        if (!initialized) return super.onDraw(canvas)
        // Draw image shape
        mShapeDrawerManager.onDraw(canvas)

        // Draw progress indicator
        mProgressDrawerManager.onDraw(canvas)
    }

    /**
     * Changes the [progressMode] of the indicator (e.g. passing from determinate to indeterminate).
     * It also starts animation of indeterminate progress indicator.
     */
    fun setProgressMode(progressMode: PivProgressMode) =
        mProgressDrawerManager.changeProgressMode(progressMode, false)

    /** Changes the [shapeMode] of the image. */
    fun setShapeMode(shapeMode: PivShapeMode) = mShapeDrawerManager.changeShapeMode(shapeMode)

    /** Changes the [blurMode] and [radius] to blur the image. */
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
        if (!mCheckBlur || drawable == null) {
            return false
        }

        val shouldBlur = mBlurManager.shouldBlur(drawable.current, changeDrawable)

        if (changeDrawable || mBlurManager.getOriginalBitmap() == null) {
            mBlurManager.changeDrawable(drawable.current)
        }

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

    /** Set whether the progress indicator [isIndeterminate]. */
    fun setProgressIndeterminate(isIndeterminate: Boolean) =
        mProgressDrawerManager.getProgressOptions().setIsIndeterminate(isIndeterminate)

    /** @return The options of the progress indicator. */
    fun getProgressOptions(): ProgressOptions = mProgressDrawerManager.getProgressOptions()

    /** @return The progress, expressed as a percentage, from 0 to 100. */
    fun getProgress(): Float = mProgressDrawerManager.getProgressOptions().valuePercent

    /** @return The selected progress mode. */
    fun getProgressMode(): PivProgressMode = mProgressDrawerManager.getProgressMode()

    /** @return The selected shape mode. */
    fun getShapeMode(): PivShapeMode = mShapeDrawerManager.getShapeMode()

    /** @return The selected shape mode. */
    fun getBlurMode(): PivBlurMode = mBlurManager.getBlurMode()

    /** @return The options of the shape. */
    fun getShapeOptions(): ShapeOptions = mShapeDrawerManager.getShapeOptions()

    /** @return The options of the blur. */
    fun getBlurOptions(): BlurOptions = mBlurManager.getBlurOptions()

    /** @return The selected radius used for blurring. */
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
     * Set the progress of the current indicator.
     * If the drawer is indeterminate, it will change its state and make it determinate.
     *
     * @param progress Percentage value of the progress
     */
    fun setProgressValue(progress: Float) = getProgressOptions().setValue(progress)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!initialized) return
        mBlurManager.addContext(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (!initialized) return
        mBlurManager.removeContext(true)
    }

    /** Set the [progressGravity] of the progress indicator. It will follow rtl (on api 17+), if not disabled. */
    fun setPivProgressGravity(progressGravity: PivProgressGravity) { getProgressOptions().gravity = progressGravity }

    /**
     * Changes the [progressMode] of the indicator (e.g. passing from determinate to indeterminate).
     * It also starts animation of indeterminate progress indicator.
     */
    fun setPivProgressMode(progressMode: PivProgressMode) { setProgressMode(progressMode) }

    /** Set whether the progress indicator is [progressIndeterminate]. */
    fun setPivProgressIndeterminate(progressIndeterminate: Boolean) = setProgressIndeterminate(progressIndeterminate)

    /** Set the progress animation [duration] in milliseconds. */
    fun setPivProgressAnimationDuration(duration: Int) { getProgressOptions().animationDuration = duration }

    /**
     * Set the [progressValue] of the progress indicator, used by determinate drawers.
     * If the drawer is indeterminate, it will change its state and make it determinate.
     * If the percentage is higher than 100, it is treated as (value % 100).
     * If the percentage is lower than 0, it is treated as 0.
     * If the drawer is not determinate or horizontal_determinate it's ignored.
     * Note: multiplies of 100 (e.g. 200, 300, ...) will be treated as 0!
     */
    fun setPivProgressValue(progressValue: Float) {
        if (getProgress() == 100F && progressValue != 100F && getProgressOptions().determinateAnimationEnabled) {
            getProgressOptions().determinateAnimationEnabled = false
            setProgressValue(0F)
            getProgressOptions().determinateAnimationEnabled = true
        }
        setProgressValue(progressValue)
    }

    /**
     * Set the [progressSize] of the progress indicator.
     *
     * Note that it may be different from the actual size used to draw the progress, since it is
     * calculated based on the View size and on the padding option.
     * To use dp, use TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, resources.displayMetrics)
     */
    fun setPivProgressSize(progressSize: Int) { getProgressOptions().setSize(progressSize) }

    /**
     * Set the [progressSize] of the progress indicator as a percentage of the whole View.
     * If the percentage is higher than 100, it is treated as (value % 100).
     */
    fun setPivProgressSizePercent(progressSize: Float) { getProgressOptions().setSize(progressSize) }

    /**
     * Set the [progressPadding] of the progress indicator.
     * To use dp, use TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, resources.displayMetrics).
     */
    fun setPivProgressPadding(progressPadding: Int) { getProgressOptions().padding = progressPadding }

    /**
     * Set the [progressBorderWidth] of the progress indicator. If it's lower than 0, it is ignored.
     * To use dp, use TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, resources.displayMetrics).
     * If you want the real progress border width, check [ProgressOptions.calculatedBorderWidth].
     */
    fun setPivProgressBorderWidth(progressBorderWidth: Int) { getProgressOptions().borderWidth = progressBorderWidth }

    /**
     * Set the [progressBorderWidth] of the progress indicator as percentage of the progress indicator size.
     * It's used only if borderWidth is less than 0.
     * If the percentage is higher than 100, it is treated as (value % 100).
     */
    fun setPivProgressBorderWidthPercent(progressBorderWidth: Float) {
        getProgressOptions().setBorderWidth(progressBorderWidth)
    }

    /**
     * Set the [progressShadowBorderWidth] of the progress indicator shadow border.
     * To use dp, use TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, resources.displayMetrics)
     * If you want the real shadow border width, check [ProgressOptions.calculatedShadowBorderWidth].
     */
    fun setPivProgressShadowBorderWidth(progressShadowBorderWidth: Float) {
        getProgressOptions().shadowBorderWidth = progressShadowBorderWidth
    }

    /**
     * Set the [progressShadowPadding] of the progress indicator relative to its shadow.
     * If it's lower than 0, it is ignored.
     * If you want the real shadow padding, check [ProgressOptions.calculatedShadowPadding].
     */
    fun setPivProgressShadowPadding(progressShadowPadding: Int) {
        getProgressOptions().shadowPadding = progressShadowPadding
    }

    /**
     * Set the [progressShadowPadding] of the progress indicator relative to its shadow as a percentage of the shadow.
     * If the percentage is higher than 100, it is treated as (value % 100).
     */
    fun setPivProgressShadowPaddingPercent(progressShadowPadding: Float) {
        getProgressOptions().setShadowPadding(progressShadowPadding)
    }

    /** Set if [progressShadowEnabled], that is, if the progress indicator should show a shadow. */
    fun setPivProgressShadowEnabled(progressShadowEnabled: Boolean) {
        getProgressOptions().shadowEnabled = progressShadowEnabled
    }

    /** Set if a determinate progress mode should update its progress with an animation. */
    fun setPivProgressDeterminateAnimationEnabled(progressDeterminateAnimationEnabled: Boolean) {
        getProgressOptions().determinateAnimationEnabled = progressDeterminateAnimationEnabled
    }

    /** Set whether the view should disable right to left layout (used for gravity option and progress direction). */
    fun setPivProgressRtlDisabled(progressRtlDisabled: Boolean) {
        getProgressOptions().isRtlDisabled = progressRtlDisabled
    }

    /** Set if the circular progress mode should show a wedge. If the mode is not determinate it's ignored. */
    fun setPivProgressDrawWedge(progressDrawWedge: Boolean) { getProgressOptions().drawWedge = progressDrawWedge }

    /** Set whether the progress should reverse its animation. */
    fun setPivProgressReversed(progressReversed: Boolean) {
        getProgressOptions().isProgressReversed = progressReversed
    }

    /** Set whether the progress should be reset on drawable change. */
    fun setPivProgressRemovedOnChange(progressRemovedOnChange: Boolean) {
        getProgressOptions().isRemovedOnChange = progressRemovedOnChange
    }

    /** Set the [progressFrontColor] of the progress indicator, used by determinate modes. */
    fun setPivProgressFrontColor(progressFrontColor: Int) { getProgressOptions().frontColor = progressFrontColor }

    /** Set the [progressBackColor] of the progress indicator, used by determinate modes. */
    fun setPivProgressBackColor(progressBackColor: Int) { getProgressOptions().backColor = progressBackColor }

    /** Set the [progressIndeterminateColor] of the progress indicator, used by indeterminate modes. */
    fun setPivProgressIndeterminateColor(progressIndeterminateColor: Int) {
        getProgressOptions().indeterminateColor = progressIndeterminateColor
    }

    /** Set the [progressShadowColor] of the progress indicator's shadow. */
    fun setPivProgressShadowColor(progressShadowColor: Int) { getProgressOptions().shadowColor = progressShadowColor }

    /** Set the [progressShadowBorderColor] of the progress indicator's shadow. */
    fun setPivProgressShadowBorderColor(progressShadowBorderColor: Int) {
        getProgressOptions().shadowBorderColor = progressShadowBorderColor
    }

    /** Changes the [shapeMode] of the image. */
    fun setPivShapeMode(shapeMode: PivShapeMode) { setShapeMode(shapeMode) }

    /**
     * Set the [shapeScaleType] to control how the image should be resized or moved to match the size of this ImageView.
     * Added to provide additional custom scale types.
     */
    fun setPivShapeScaleType(shapeScaleType: PivShapeScaleType) { setShapeScaleType(shapeScaleType) }

    /** Set the [shapeInnerPadding] of the image relative to the shape. If it's lower than 0, it is ignored. */
    fun setPivShapeInnerPadding(shapeInnerPadding: Int) { getShapeOptions().setInnerPadding(shapeInnerPadding) }

    /**
     * Set the [shapeInnerPadding] of the image relative to the shape, as a percentage of the shape size.
     * If the percentage is higher than 100, it is treated as (value % 100).
     */
    fun setPivShapeInnerPaddingPercent(shapeInnerPadding: Float) {
        getShapeOptions().setInnerPadding(shapeInnerPadding)
    }

    /**
     * Set the [shapeBorderWidth] of the shape border.
     * To use dp, use TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, resources.displayMetrics).
     */
    fun setPivShapeBorderWidth(shapeBorderWidth: Int) { getShapeOptions().borderWidth = shapeBorderWidth }

    /** Set the [shapeRatio] of the shape. Ignored in Circle and Square shapes. Width = height * ratio. */
    fun setPivShapeRatio(shapeRatio: Float) { getShapeOptions().ratio = shapeRatio }

    /** Set the [shapeRadiusX] radius of the rounded rectangles. */
    fun setPivShapeRadiusX(shapeRadiusX: Float) { getShapeOptions().radiusX = shapeRadiusX }

    /** Set the [shapeRadiusY] radius of the rounded rectangles. */
    fun setPivShapeRadiusY(shapeRadiusY: Float) { getShapeOptions().radiusY = shapeRadiusY }

    /** Set if the border should be drawn over the image ([shapeBorderOverlay] true) or the shape should be shrank. */
    fun setPivShapeBorderOverlay(shapeBorderOverlay: Boolean) { getShapeOptions().borderOverlay = shapeBorderOverlay }

    /** Set the [shapeSolidColor] used by solid shapes. */
    fun setPivShapeSolidColor(shapeSolidColor: Int) { getShapeOptions().solidColor = shapeSolidColor }

    /** Set the [shapeBackgroundColor] of the shape. */
    fun setPivShapeBackgroundColor(shapeBackgroundColor: Int) {
        getShapeOptions().backgroundColor = shapeBackgroundColor
    }

    /** Set the [shapeForegroundColor] of the shape. */
    fun setPivShapeForegroundColor(shapeForegroundColor: Int) {
        getShapeOptions().foregroundColor = shapeForegroundColor
    }

    /** Set the [shapeBackground] drawable to be drawn under the image, using the shape. Ignored on rounded shapes. */
    fun setPivShapeBackground(shapeBackground: Drawable) { getShapeOptions().backgroundDrawable = shapeBackground }

    /** Set the [shapeForeground] drawable to be drawn over the image, using the shape. Ignored on rounded shapes. */
    fun setPivShapeForeground(shapeForeground: Drawable) { getShapeOptions().foregroundDrawable = shapeForeground }

    /** Set the [shapeBorderColor] of the shape border. */
    fun setPivShapeBorderColor(shapeBorderColor: Int) { getShapeOptions().borderColor = shapeBorderColor }

    /** Changes the [blurMode] to blur the image. */
    fun setPivBlurMode(blurMode: PivBlurMode) { setBlurMode(blurMode, getBlurRadius()) }

    /** Changes the [blurRadius] to blur the image. */
    fun setPivBlurRadius(blurRadius: Int) { setBlurRadius(blurRadius) }

    /**
     * Set the [blurDownSamplingRate] to blur the image, so that it's no more than the view size divided by the rate.
     * If a value less than 1 is passed, downSampling rate 1 is used.
     */
    fun setPivBlurDownSamplingRate(blurDownSamplingRate: Int) {
        getBlurOptions().downSamplingRate = blurDownSamplingRate.toFloat()
    }

    /**
     * Set whether the original bitmap should be blurred only once.
     * If [blurStatic] is true, optimizations occur.
     * If [blurStatic]is false, trying to blur a second time won't have effect.
     */
    fun setPivBlurStatic(blurStatic: Boolean) { getBlurOptions().isStaticBlur = blurStatic }

    /** Set if the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs. */
    fun setPivBlurUseRsFallback(blurUseRsFallback: Boolean) { getBlurOptions().useRsFallback = blurUseRsFallback }

    /**
     * Set the number of threads to use to blur the image, ignored when using native renderscript modes.
     * If [blurNumThreads] is 0 or negative, available cores number will be used.
     */
    fun setPivBlurNumThreads(blurNumThreads: Int) { getBlurOptions().numThreads = blurNumThreads }
}
