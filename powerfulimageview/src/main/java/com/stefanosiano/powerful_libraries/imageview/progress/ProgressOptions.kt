package com.stefanosiano.powerful_libraries.imageview.progress

import android.graphics.RectF
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

/**
 * Class that helps managing the options that will be used by the progress drawers.
 */

class ProgressOptions() {

    // Options used directly by drawers

    /** If the determinate drawer should update its progress with an animation.
     * If the drawer is not determinate or horizontal_determinate it's ignored. */
    var determinateAnimationEnabled: Boolean = false
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Width of the progress indicator. If it's lower than 0, it is ignored.
     * If you want to use dp, use
     *  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, resources.displayMetrics)
     * If you want the real progress border width, check [calculatedBorderWidth] */
    var borderWidth: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
        }

    /** Progress animation duration (in milliseconds)  */
    var animationDuration: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                listener.get()?.onOptionsUpdated(this)
        }

    /** Width of the progress indicator as percentage of the progress indicator size.
     * If you want the real progress border width, check [calculatedBorderWidth] */
    var borderWidthPercent: Float = 0f

    /** Percentage value of the progress indicator, used by determinate drawers  */
    var valuePercent: Float = 0f

    /** Front color of the progress indicator, used by determinate drawers.
     * If the drawer is not determinate or horizontal_determinate it's ignored.
     *
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.  */
    var frontColor: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                listener.get()?.onOptionsUpdated(this)
        }

    /** Back color of the progress indicator, used by determinate drawers.
     * If the drawer is not determinate or horizontal_determinate it's ignored.
     *
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.  */
    var backColor: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                listener.get()?.onOptionsUpdated(this)
        }

    /** Color of the progress indicator, used by indeterminate drawers.
     * If the drawer is not indeterminate or horizontal_indeterminate it's ignored.
     *
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.  */
    var indeterminateColor: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                listener.get()?.onOptionsUpdated(this)
        }

    /** If should show a wedge, used by circular determinate drawer. If the drawer is not determinate it's ignored.  */
    var drawWedge: Boolean = false
        set(value) {
            field = value
            if (isInitialized)
                listener.get()?.onOptionsUpdated(this)
        }

    /** If should show a shadow  */
    var shadowEnabled: Boolean = false
        set(value) {
            field = value
            if (isInitialized)
                calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
        }

    /** Shadow color of the progress indicator.
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details. */
    var shadowColor: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                listener.get()?.onOptionsUpdated(this)
        }

    /** Padding of the progress indicator relative to its shadow. If it's lower than 0, it is ignored.
     * If you want the real shadow padding, check [calculatedShadowPadding] */
    var shadowPadding: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
        }

    /** Padding of the progress indicator relative to its shadow, as a percentage of the whole shadow.
     * If you want the real shadow padding, check [calculatedShadowPadding] */
    var shadowPaddingPercent: Float = 0f

    /** Width of the progress indicator shadow border.
     * If you want to use dp, use
     *  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, resources.displayMetrics)
     * If you want the real shadow border width, check [calculatedShadowBorderWidth] */
    var shadowBorderWidth: Float = 0f
        set(value) {
            field = value
            if (isInitialized)
                calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
        }

    /** Width of the progress indicator shadow border after calculations  */
    var calculatedShadowBorderWidth: Float = 0f

    /** Color of the progress indicator shadow border.
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.  */
    var shadowBorderColor: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                listener.get()?.onOptionsUpdated(this)
        }

    // Variables used to calculate bounds

    /** Size of the progress indicator. If you want the real progress indicator size, check [calculatedSize] */
    var mSize: Int = 0

    /**
     * Padding of the progress indicator.
     * If you want to use dp, use
     *  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, resources.displayMetrics) */
    var padding: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
        }

    /** Size of the progress indicator, as a percentage of the whole View.
     * If you want the real progress indicator size, check [calculatedSize] */
    var sizePercent: Float = 0f

    /**
     * Gravity of the progress indicator. It will follow the right to left layout (on api 17+), if not disabled */
    var gravity: PivProgressGravity? = null
        set(value) {
            field = value
            if (isInitialized)
                calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
        }

    /** Whether the view should use right to left layout (used for gravity option) */
    var isProgressReversed: Boolean = false
        set(value) {
            field = value
            listener.get()?.onOptionsUpdated(this)
        }
        get() = (isRtl && !isRtlDisabled) != field

    /** Whether the progress should be reset on drawable change  */
    var isRemovedOnChange: Boolean = false

    /** Whether the view is using right to left layout (used for gravity option and progress direction)  */
    var isRtl: Boolean = false

    /** Whether the view should use or ignore right to left layout (used for gravity option and progress direction)  */
    var isRtlDisabled: Boolean = false
        set(value) {
            field = value
            if (isInitialized)
                calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
        }

    /** Whether the progress indicator is indeterminate or not  */
    var isIndeterminate: Boolean = false
        private set

    // ************** Calculated fields *****************

    /** Calculated size of the indicator, base on mSize, sizePercent and View size. It's the real value used by the
     * progress indicator  */
    var calculatedSize: Float = 0f

    /** Calculated padding of the indicator shadow. It's the real value used by the progress indicator  */
    var calculatedShadowPadding: Int = 0

    /** Border width of the progress indicator, after calculations. It's the real value used by the progress
     * indicator */
    var calculatedBorderWidth: Int = 0

    // Bounds of the progress and shadow indicator
    /** Progress bounds calculated after [calculateSizeAndBounds]. DON'T change directly its values! */
    var rect = RectF(0f, 0f, 0f, 0f)

    /** Shadow bounds calculated after [calculateSizeAndBounds]. DON'T change directly its values! */
    var shadowRect = RectF(0f, 0f, 0f, 0f)

    /** Shadow border bounds calculated after [calculateSizeAndBounds]. DON'T change directly its values! */
    var shadowBorderRect = RectF(0f, 0f, 0f, 0f)

    // Last calculated width and height
    /** Last width calculated. Used when changing programmatically the options, so bounds can be recalculated */
    private var mCalculatedLastW: Int = 0

    /** Last height calculated. Used when changing programmatically the options, so bounds can be recalculated */
    private var mCalculatedLastH: Int = 0

    /** Last progress mode used. Used when changing programmatically the options, so bounds can be recalculated */
    private var mCalculatedLastMode = PivProgressMode.NONE

    /** Listener that will update the progress drawers on changes, with a weak reference to not leak memory */
    private var listener = WeakReference<ProgressOptionsListener>(null)

    /** Flag to check if the object's constructor was called */
    private var isInitialized = false

    /**
     * Creates the object that will be used by progress drawers:
     *
     * @param determinateAnimationEnabled If the determinate drawer should update its progress with an animation
     * @param borderWidth Width of the progress indicator. If it's 0 or more, it applies and overrides
     *  "borderWidthPercent" parameter
     * @param borderWidthPercent Width of the progress indicator as a percentage of the progress indicator size
     * @param size Size of the progress indicator. If it's 0 or more, it applies and overrides
     *  "sizePercent" parameter
     * @param padding Padding of the progress indicator
     * @param sizePercent Size of the progress indicator as a percentage of the whole View
     * @param valuePercent Percentage value of the progress indicator, used by determinate drawers
     * @param frontColor Front color of the indicator, used by determinate drawers
     * @param backColor Back color of the indicator, used by determinate drawers
     * @param indeterminateColor Color of the indicator, used by indeterminate drawers
     * @param gravity Gravity of the indicator
     * @param rtl Whether the view should use right to left layout (used for gravity option)
     * @param disableRtlSupport If true, rtl attribute will be ignored (start will always be treated as left)
     * @param isIndeterminate If true, indeterminate progress is drawn
     * @param drawWedge If should show a wedge, used by circular determinate drawer
     * @param shadowEnabled If should show a shadow under progress indicator
     * @param shadowColor Color of the shadow
     * @param shadowPadding Padding of the progress indicator, relative to its shadow.
     *  If it's 0 or more, it applies and overrides "shadowPaddingPercent" parameter
     * @param shadowPaddingPercent Padding of the progress indicator, relative to its shadow,
     *  as a percentage of the shadow
     * @param shadowBorderWidth Width of the progress indicator shadow border
     * @param shadowBorderColor Color of the progress indicator shadow border
     * @param isProgressReversed Whether the progress should be reversed
     */
    @Suppress("LongParameterList")
    constructor(
        determinateAnimationEnabled: Boolean,
        animationDuration: Int,
        borderWidth: Int,
        borderWidthPercent: Float,
        size: Int,
        sizePercent: Float,
        padding: Int,
        valuePercent: Float,
        frontColor: Int,
        backColor: Int,
        indeterminateColor: Int,
        gravity: Int,
        rtl: Boolean,
        disableRtlSupport: Boolean,
        isIndeterminate: Boolean,
        drawWedge: Boolean,
        shadowEnabled: Boolean,
        shadowColor: Int,
        shadowPadding: Int,
        shadowPaddingPercent: Float,
        shadowBorderWidth: Float,
        shadowBorderColor: Int,
        isProgressReversed: Boolean,
        isRemovedOnChange: Boolean
    ) : this() {
        this.determinateAnimationEnabled = determinateAnimationEnabled
        this.animationDuration = animationDuration
        this.borderWidth = borderWidth
        this.borderWidthPercent = borderWidthPercent
        if (this.borderWidthPercent > 100)
            this.borderWidthPercent = this.borderWidthPercent % 100
        this.mSize = size
        this.padding = padding
        this.sizePercent = sizePercent
        this.valuePercent = valuePercent
        this.frontColor = frontColor
        this.backColor = backColor
        this.indeterminateColor = indeterminateColor
        this.gravity = PivProgressGravity.fromValue(gravity)
        this.isRtl = rtl
        this.isRtlDisabled = disableRtlSupport
        this.isIndeterminate = isIndeterminate
        this.drawWedge = drawWedge
        this.shadowEnabled = shadowEnabled
        this.shadowColor = shadowColor
        this.shadowPadding = shadowPadding
        this.shadowPaddingPercent = shadowPaddingPercent
        this.shadowBorderWidth = shadowBorderWidth
        this.shadowBorderColor = shadowBorderColor
        this.isProgressReversed = isProgressReversed
        this.isRemovedOnChange = isRemovedOnChange
        this.isInitialized = true
    }

    /** Updates the values of the current options, copying the passed values  */
    fun setOptions(other: ProgressOptions) {
        this.determinateAnimationEnabled = other.determinateAnimationEnabled
        this.borderWidth = other.borderWidth
        this.borderWidthPercent = other.borderWidthPercent
        this.valuePercent = other.valuePercent
        this.frontColor = other.frontColor
        this.backColor = other.backColor
        this.indeterminateColor = other.indeterminateColor
        this.drawWedge = other.drawWedge
        this.shadowEnabled = other.shadowEnabled
        this.shadowColor = other.shadowColor
        this.shadowPadding = other.shadowPadding
        this.shadowPaddingPercent = other.shadowPaddingPercent
        this.shadowBorderWidth = other.shadowBorderWidth
        this.calculatedShadowBorderWidth = other.calculatedShadowBorderWidth
        this.shadowBorderColor = other.shadowBorderColor
        this.mSize = other.mSize
        this.padding = other.padding
        this.sizePercent = other.sizePercent
        this.gravity = other.gravity
        this.isRtl = other.isRtl
        this.isRtlDisabled = other.isRtlDisabled
        this.isIndeterminate = other.isIndeterminate
        this.calculatedSize = other.calculatedSize
        this.calculatedShadowPadding = other.calculatedShadowPadding
        this.calculatedBorderWidth = other.calculatedBorderWidth
        this.rect.set(other.rect)
        this.shadowRect.set(shadowRect)
        this.shadowBorderRect.set(shadowBorderRect)
        this.mCalculatedLastW = other.mCalculatedLastW
        this.mCalculatedLastH = other.mCalculatedLastH
        this.mCalculatedLastMode = other.mCalculatedLastMode
        this.isProgressReversed = other.isProgressReversed
        this.listener = other.listener
        this.isInitialized = true
    }

    /**
     * Forces recalculation of the bounds of the progress indicator, based on progress options and mode.
     * Calculated bounds are accessible after this call through getLeft(), getTop(), getRight() and getBottom() methods.
     *
     * @param mode Mode of the progress indicator
     */
    fun recalculateBounds(mode: PivProgressMode) {
        calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mode)
    }

    /**
     * Calculates the bounds of the progress indicator, based on progress options and mode.
     * Calculated bounds are accessible after this call through getLeft(), getTop(), getRight() and getBottom() methods.
     *
     * Do not use this method directly! If you want the size to be calculated again, call requestLayout()!
     *
     * @param w Width of the View
     * @param h Height of the View
     * @param mode Mode of the progress indicator
     */
    fun calculateSizeAndBounds(w: Int, h: Int, mode: PivProgressMode) {

        // Saving last width and height, so i can later call this function from this class
        mCalculatedLastW = w
        mCalculatedLastH = h
        mCalculatedLastMode = mode

        // If there's no shadow, no border of the shadow should be considered
        calculatedShadowBorderWidth = if (shadowEnabled) shadowBorderWidth else 0f

        if (mode == PivProgressMode.NONE) {
            this.rect.set(0f, 0f, 0f, 0f)
            this.shadowRect.set(0f, 0f, 0f, 0f)
            this.shadowBorderRect.set(0f, 0f, 0f, 0f)
            listener.get()?.onSizeUpdated(this)
            return
        }

        // Calculate the maximum possible size of the progress indicator
        var maxSize = if (w < h) w else h

        when (mode) {
            // Calculation of circular bounds
            PivProgressMode.CIRCULAR -> maxSize = if (w < h) w else h
            PivProgressMode.HORIZONTAL -> maxSize = w
            PivProgressMode.NONE -> mSize = 0
        }

        // ********** SIZE ***********
        calculatedSize = maxSize * sizePercent / 100
        // If mSize is 0 or more, it overrides sizePercent parameter
        if (mSize >= 0) calculatedSize = mSize.toFloat()

        // The progress indicator cannot be bigger than the view (minus padding)
        calculatedSize = calculatedSize.coerceAtMost((maxSize - padding - padding).toFloat())

        // ********** SHADOW PADDING ***********
        calculatedShadowPadding =
            ((calculatedSize - calculatedShadowBorderWidth * 2) * shadowPaddingPercent / 100).toInt()
        // If shadowPadding is 0 or more, it overrides shadowPaddingPercent parameter
        if (shadowPadding >= 0) calculatedShadowPadding = shadowPadding

        // If shadow is not enabled, shadow padding is set to 0
        if (!shadowEnabled) calculatedShadowPadding = 0

        // ********** BORDER WIDTH ***********
        calculatedBorderWidth =
            ((calculatedSize - calculatedShadowBorderWidth * 2) * borderWidthPercent / 100).roundToInt()
        // If borderWidth is 0 or more, it overrides borderWidthPercent paramenter
        if (borderWidth >= 0) calculatedBorderWidth = borderWidth

        // Width of the border should be at least 1 px
        calculatedBorderWidth = calculatedBorderWidth.coerceAtLeast(1)

        // ********** BOUNDS ***********
        calculateBounds(mode, w, h)

        listener.get()?.onSizeUpdated(this)
    }

    private fun calculateBounds(mode: PivProgressMode, w: Int, h: Int) {
        val considerRtl = isRtl && !isRtlDisabled
        // Horizontal gravity
        val left: Float = when {
            gravity?.isGravityLeft(considerRtl) == true -> padding.toFloat()
            gravity?.isGravityRight(considerRtl) == true -> w - calculatedSize - padding.toFloat()
            else -> (w - calculatedSize) / 2
        }
        val top: Float

        when (mode) {

            // Calculation of circular bounds
            PivProgressMode.CIRCULAR -> {

                // Vertical gravity
                top = when {
                    gravity?.isGravityTop() == true -> padding.toFloat()
                    gravity?.isGravityBottom() == true -> h.toFloat() - calculatedSize - padding.toFloat()
                    else -> (h - calculatedSize) / 2
                }

                this.shadowBorderRect.set(
                    left + (calculatedShadowBorderWidth / 2),
                    top + (calculatedShadowBorderWidth / 2),
                    left + calculatedSize - calculatedShadowBorderWidth,
                    top + calculatedSize - calculatedShadowBorderWidth
                )

                this.shadowRect.set(shadowBorderRect)
                this.shadowRect.inset((calculatedShadowBorderWidth / 2), (calculatedShadowBorderWidth / 2))

                this.rect.set(shadowRect)
                this.rect.inset(
                    (calculatedShadowPadding + calculatedBorderWidth / 2).toFloat(),
                    (calculatedShadowPadding + calculatedBorderWidth / 2).toFloat()
                )
            }

            // Calculation of horizontal bounds
            PivProgressMode.HORIZONTAL -> {

                // Vertical gravity
                top = when {
                    gravity?.isGravityTop() == true -> padding.toFloat()
                    gravity?.isGravityBottom() == true -> (h - calculatedBorderWidth - padding).toFloat()
                    else -> ((h - calculatedBorderWidth - padding) / 2).toFloat()
                }

                this.shadowBorderRect.set(
                    left + (calculatedShadowBorderWidth / 2),
                    top + (calculatedShadowBorderWidth / 2),
                    left + calculatedSize - calculatedShadowBorderWidth,
                    top + calculatedBorderWidth - (calculatedShadowBorderWidth / 2)
                )

                this.shadowRect.set(shadowBorderRect)
                this.shadowRect.inset((calculatedShadowBorderWidth / 2), (calculatedShadowBorderWidth / 2))

                this.rect.set(shadowRect)
                this.rect.inset(calculatedShadowPadding.toFloat(), calculatedShadowPadding.toFloat())
            }

            // If everything goes right, it should never come here. Just a precaution
            PivProgressMode.NONE -> {
                this.rect.set(0f, 0f, 0f, 0f)
                this.shadowRect.set(0f, 0f, 0f, 0f)
                this.shadowBorderRect.set(0f, 0f, 0f, 0f)
            }
        }
    }

    /**
     * Width of the progress indicator as percentage of the progress indicator size.
     * It's used only if borderWidth is less than 0.
     * If the percentage is higher than 100, it is treated as (value % 100).
     *
     * @param borderWidthPercent Percentage of the progress indicator size, as a float from 0 to 100
     */
    fun setBorderWidth(borderWidthPercent: Float) {
        var borderWidthPercent = borderWidthPercent
        this.borderWidth = -1
        if (borderWidthPercent > 100)
            borderWidthPercent %= 100
        this.borderWidthPercent = borderWidthPercent
        calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
    }

    /**
     * Percentage value of the progress indicator, used by determinate drawers.
     * If the drawer is indeterminate, it will change its state and make it determinate.
     * If the percentage is higher than 100, it is treated as (value % 100).
     * If the percentage is lower than 0, it is treated as 0.
     * If the drawer is not determinate or horizontal_determinate it's ignored.
     * Note: multiplies of 100 (e.g. 200, 300, ...) will be treated as 0!
     *
     * @param valuePercent Percentage of the progress indicator, as a float from 0 to 100
     */
    fun setValue(valuePercent: Float) {
        var valuePercent = valuePercent
        if (valuePercent > 100)
            valuePercent %= 100
        if (valuePercent < 0)
            valuePercent = 0f
        this.valuePercent = valuePercent

        // If it's indeterminate, I change it to determinate changing the mode, otherwise I just update current drawer
        val modeChanged = isIndeterminate

        this.isIndeterminate = false

        if (modeChanged)
            listener.get()?.onModeUpdated(this)
        else
            listener.get()?.onOptionsUpdated(this)
    }

    /**
     * Size of the progress indicator.
     *
     * Note that it may be different from the actual size used to draw the progress, since it is
     * calculated based on the View size and on the padding option.
     * If you want to use dp, use
     *  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, resources.displayMetrics)
     *
     * @param size Size of the progress indicator
     */
    fun setSize(size: Int) {
        this.mSize = size
        calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
    }

    /**
     * Set the size of the progress indicator.
     *
     * If the percentage is higher than 100, it is treated as (value % 100).
     *
     * @param sizePercent Progress indicator size as a percentage of the whole View, as a float from 0 to 100
     */
    fun setSize(sizePercent: Float) {
        var sizePercent = sizePercent
        this.mSize = -1
        if (sizePercent > 100)
            sizePercent %= 100
        this.sizePercent = sizePercent
        calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
    }

    /**
     * Set whether the view should use right to left layout (used for gravity option)
     *
     * @param isIndeterminate If true, indeterminate progress is drawn.
     * If false, determinate is drawn.
     */
    fun setIsIndeterminate(isIndeterminate: Boolean) {
        // If it's indeterminate, I change it to determinate, changing the mode, otherwise I just update current drawer
        val modeChanged = this.isIndeterminate != isIndeterminate
        this.isIndeterminate = isIndeterminate

        if (modeChanged)
            listener.get()?.onModeUpdated(this)
        else
            listener.get()?.onOptionsUpdated(this)
    }

    /**
     * Set the padding of the progress indicator relative to its shadow.
     * If the percentage is higher than 100, it is treated as (value % 100).
     *
     * @param paddingPercent Progress indicator shadow padding as a percentage of the whole shadow, as a float
     *  from 0 to 100
     */
    fun setShadowPadding(paddingPercent: Float) {
        this.shadowPadding = -1
        if (paddingPercent > 100)
            shadowPaddingPercent = paddingPercent % 100
        calculateSizeAndBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode)
    }

    // *************** Fields used by drawers ****************

    /**
     * Set the listener that will update the progress drawers on changes
     *
     * Do not use this method, as it is intended for internal reasons!
     *
     * @param listener Listener that will update the progress drawers on changes
     */
    fun setListener(listener: ProgressOptionsListener) { this.listener = WeakReference(listener) }

    interface ProgressOptionsListener {
        fun onOptionsUpdated(options: ProgressOptions)
        fun onSizeUpdated(options: ProgressOptions)
        fun onModeUpdated(options: ProgressOptions)
    }
}
