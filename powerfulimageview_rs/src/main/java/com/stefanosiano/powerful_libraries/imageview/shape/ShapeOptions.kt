package com.stefanosiano.powerful_libraries.imageview.shape

import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.stefanosiano.powerful_libraries.imageview.progress.PivShapeCutGravity
import java.lang.ref.WeakReference

/**
 * Class that helps managing the options that will be used by the shape drawers.
 */

class ShapeOptions() {

    // Options used directly by drawers

    /** Background color of the shape.
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not premultiplied, meaning
     * that its alpha can be any value, regardless of the values of r,g,b. See the Color class for more details */
    var backgroundColor: Int = 0
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Foreground color of the shape.
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not premultiplied, meaning
     * that its alpha can be any value, regardless of the values of r,g,b. See the Color class for more details */
    var foregroundColor: Int = 0
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Inner padding of the image relative to the shape  */
    var mInnerPadding: Int = 0

    /** Inner padding of the image relative to the shape, as a percentage  */
    var mInnerPaddingPercent: Float = 0f

    /** Whether the border should be drawn over the image or the shape should be shrinked */
    var borderOverlay: Boolean = false
        set(value) {
            field = value
            if (isInitialized)
                recalculateLastBounds()
        }

    /** Color of the shape border
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not premultiplied, meaning
     * that its alpha can be any value, regardless of the values of r,g,b. See the Color class for more details */
    var borderColor: Int = 0
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /**
     * Gravity of the progress indicator. It will follow the right to left layout (on api 17+), if not disabled */
    var cutGravity: PivShapeCutGravity = PivShapeCutGravity.BOTTOM
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Width of the shape border. If you want to use dp, set value using
     *  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, getResources().getDisplayMetrics()) */
    var borderWidth: Int = 0
        set(value) {
            field = value
            if (isInitialized)
                recalculateLastBounds()
        }

    /** Ratio of the shape. It's ignored in Circle and Square shapes. Width will be calculated as height * ratio */
    var ratio: Float = 0f
        set(value) {
            field = value
            if (isInitialized)
                recalculateLastBounds()
        }

    /** X radius of the rounded rectangles  */
    var radiusX: Float = 0f
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Y radius of the rounded rectangles  */
    var radiusY: Float = 0f
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Cut radius 1 of the cut shapes */
    var cutRadius1: Int = 0
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Cut radius 1 of the cut shapes, as a percentage */
    var cutRadius1Percent: Float = 0f
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Cut radius 2 of the cut shapes */
    var cutRadius2: Int = 0
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Cut radius 2 of the cut shapes, as a percentage */
    var cutRadius2Percent: Float = 0f
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Color used by solid shapes
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not premultiplied, meaning
     * that its alpha can be any value, regardless of the values of r,g,b. See the Color class for more details */
    var solidColor: Int = 0
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Foreground drawable to be drawn under the image, using the shape. Note: Does not work on rounded shapes! */
    var foregroundDrawable: Drawable? = null
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    /** Background drawable to be drawn under the image, using the shape. Note: Does not work on rounded shapes! */
    var backgroundDrawable: Drawable? = null
        set(value) { field = value; if (isInitialized) listener.get()?.onOptionsUpdated(this) }

    // ************** Calculated fields *****************

    // Bounds of the shape
    /** Bounds of the shape, calculated using [calculateBounds]. DON'T edit its values directly! */
    var shapeBounds = RectF(0f, 0f, 0f, 0f)

    /** Bounds of the image, calculated using [calculateBounds]. DON'T edit its values directly! */
    var imageBounds = RectF(0f, 0f, 0f, 0f)

    /** Bounds of the border, calculated using [calculateBounds]. DON'T edit its values directly! */
    var borderBounds = RectF(0f, 0f, 0f, 0f)

    /** Bounds of the view, without padding, calculated using [calculateBounds]. DON'T edit its values directly! */
    var viewBounds = RectF(0f, 0f, 0f, 0f)

    /** Calculated padding of the indicator shadow  */
    private var mCalculatedInnerPadding: Float = 0f

    // Last calculated width and height
    /** Last left padding calculated. Used when changing programmatically the options so bounds can be recalculated */
    private var mCalculatedLastPaddingLeft: Int = 0

    /** Last top padding calculated. Used when changing programmatically the options so bounds can be recalculated */
    private var mCalculatedLastPaddingTop: Int = 0

    /** Last right padding calculated. Used when changing programmatically the options so bounds can be recalculated */
    private var mCalculatedLastPaddingRight: Int = 0

    /** Last bottom padding calculated. Used when changing programmatically the options so bounds can be recalculated */
    private var mCalculatedLastPaddingBottom: Int = 0

    /** Last width calculated. Used when changing programmatically the options so bounds can be recalculated */
    private var mCalculatedLastW: Int = 0

    /** Last height calculated. Used when changing programmatically the options so bounds can be recalculated */
    private var mCalculatedLastH: Int = 0

    /** Last progress mode used. Used when changing programmatically the options so bounds can be recalculated */
    private var mCalculatedLastMode = PivShapeMode.NORMAL

    /** Listener that will update the shape drawers on changes, with a weak reference to be sure to not leak memory  */
    private var listener = WeakReference<ShapeOptionsListener>(null)

    /** Flag to check if the object's constructor was called */
    private var isInitialized = false

    /**
     * Creates the object that will be used by shape drawers:
     *
     * @param backgroundColor Background color of the shape
     * @param foregroundColor Foreground color of the shape
     * @param innerPadding Inner padding of the image relative to the shape. If it's 0 or more, it applies and overrides
     *  "innerPaddingPercent" parameter
     * @param innerPaddingPercent Inner padding of the image relative to the shape, as a percentage
     * @param borderOverlay Whether the border should be drawn over the image or not
     * @param borderColor Color of the shape border
     * @param borderWidth Width of the shape border
     * @param ratio Ratio of the shape. Width will be equal to (height * ratio). Ignored in square and circle shapes
     * @param radiusX X radius of the image. Used in rounded rectangles
     * @param radiusY Y radius of the image. Used in rounded rectangles
     * @param solidColor Solid color used by solid shapes
     * @param backgroundDrawable Background drawable to draw under the image. Does not follow rounded shapes
     * @param foregroundDrawable Fackground drawable to draw under the image. Does not follow rounded shapes
     * @param cutRadius1 Cut radius 1. Used in cut shapes. If it's 0 or more, it applies and overrides
     *  "cutRadius1Percent" parameter
     * @param cutRadius1Percent Cut radius 1 in percentage. Used in cut shapes
     * @param cutRadius2 Cut radius 2. Used in cut shapes. If it's 0 or more, it applies and overrides
     *  "cutRadius2Percent" parameter
     * @param cutRadius2Percent Cut radius 2 in percentage. Used in cut shapes
     */
    @Suppress("LongParameterList")
    constructor(
        backgroundColor: Int,
        foregroundColor: Int,
        innerPadding: Int,
        innerPaddingPercent: Float,
        borderOverlay: Boolean,
        cutGravity: Int,
        borderColor: Int,
        borderWidth: Int,
        ratio: Float,
        radiusX: Float,
        radiusY: Float,
        solidColor: Int,
        backgroundDrawable: Drawable?,
        foregroundDrawable: Drawable?,
        cutRadius1: Int,
        cutRadius1Percent: Float,
        cutRadius2: Int,
        cutRadius2Percent: Float
    ) : this() {
        this.backgroundColor = backgroundColor
        this.foregroundColor = foregroundColor
        this.mInnerPadding = innerPadding
        this.mInnerPaddingPercent = innerPaddingPercent
        this.borderOverlay = borderOverlay
        this.borderColor = borderColor
        this.cutGravity = PivShapeCutGravity.fromValue(cutGravity)
        this.borderWidth = borderWidth
        this.ratio = ratio
        this.radiusX = radiusX
        this.radiusY = radiusY
        this.cutRadius1 = cutRadius1
        this.cutRadius1Percent = if (cutRadius1Percent > 100) cutRadius1Percent % 100 else cutRadius1Percent
        this.cutRadius2 = cutRadius2
        this.cutRadius2Percent = if (cutRadius2Percent > 100) cutRadius2Percent % 100 else cutRadius2Percent
        this.solidColor = solidColor
        this.backgroundDrawable = backgroundDrawable
        this.foregroundDrawable = foregroundDrawable
        this.isInitialized = true
    }

    /** Updates the values of the current options, copying the passed values  */
    fun setOptions(other: ShapeOptions) {
        this.backgroundColor = other.backgroundColor
        this.foregroundColor = other.foregroundColor
        this.foregroundDrawable = other.foregroundDrawable
        this.backgroundDrawable = other.backgroundDrawable
        this.mInnerPadding = other.mInnerPadding
        this.mInnerPaddingPercent = other.mInnerPaddingPercent
        this.borderOverlay = other.borderOverlay
        this.cutGravity = other.cutGravity
        this.borderColor = other.borderColor
        this.borderWidth = other.borderWidth
        this.ratio = other.ratio
        this.radiusX = other.radiusX
        this.radiusY = other.radiusY
        this.cutRadius1 = other.cutRadius1
        this.cutRadius1Percent = other.cutRadius1Percent
        this.cutRadius2 = other.cutRadius2
        this.cutRadius2Percent = other.cutRadius2Percent
        this.solidColor = other.solidColor
        this.shapeBounds.set(other.shapeBounds)
        this.imageBounds.set(other.imageBounds)
        this.borderBounds.set(other.borderBounds)
        this.viewBounds.set(other.viewBounds)
        this.mCalculatedInnerPadding = other.mCalculatedInnerPadding
        this.mCalculatedLastW = other.mCalculatedLastW
        this.mCalculatedLastH = other.mCalculatedLastH
        this.mCalculatedLastMode = other.mCalculatedLastMode
        this.mCalculatedLastPaddingLeft = other.mCalculatedLastPaddingLeft
        this.mCalculatedLastPaddingTop = other.mCalculatedLastPaddingTop
        this.mCalculatedLastPaddingRight = other.mCalculatedLastPaddingRight
        this.mCalculatedLastPaddingBottom = other.mCalculatedLastPaddingBottom
        this.listener = other.listener
        this.isInitialized = true
    }

    /**
     * Recalculates the bounds of the image, based on last calculated shape options and mode.
     * Calculated bounds are accessible after this call through getLeft(), getTop(), getRight() and getBottom() methods.
     */
    private fun recalculateLastBounds() = calculateBounds(
        mCalculatedLastW,
        mCalculatedLastH,
        Rect(
            mCalculatedLastPaddingLeft,
            mCalculatedLastPaddingTop,
            mCalculatedLastPaddingRight,
            mCalculatedLastPaddingBottom
        ),
        mCalculatedLastMode
    )

    /**
     * Calculates the bounds of the image, based on shape options and mode.
     * Calculated bounds are accessible after this call through getLeft(), getTop(), getRight() and getBottom() methods.
     *
     * Do not use this method directly! If you want the size to be calculated again, call requestLayout()!
     *
     * @param w Width of the View
     * @param h Height of the View
     * @param mode Mode of the shape
     */
    fun calculateBounds(w: Int, h: Int, padding: Rect, mode: PivShapeMode) {

        // Saving last width and height, so i can later call this function from this class
        mCalculatedLastW = w
        mCalculatedLastH = h
        mCalculatedLastPaddingLeft = padding.left
        mCalculatedLastPaddingTop = padding.top
        mCalculatedLastPaddingRight = padding.right
        mCalculatedLastPaddingBottom = padding.bottom
        mCalculatedLastMode = mode

        viewBounds.set(0f, 0f, w.toFloat(), h.toFloat())

        // Smallest size (used for padding and square/circle shapes)
        val smallSize: Float
        // If no ratio was set, i use the view ratio
        val usedRatio = if (ratio <= 0) w / h.toFloat() else ratio

        when (mode) {

            PivShapeMode.CIRCLE, PivShapeMode.SQUARE, PivShapeMode.SOLID_CIRCLE -> {
                smallSize = Math.min(h, w).toFloat()
                shapeBounds.set((w - smallSize) / 2, (h - smallSize) / 2, (w + smallSize) / 2, (h + smallSize) / 2)
            }

            PivShapeMode.NORMAL -> {
                shapeBounds.set(0f, 0f, w.toFloat(), h.toFloat())
                smallSize = Math.min(w, h).toFloat()
            }

            PivShapeMode.RECTANGLE, PivShapeMode.ROUNDED_RECTANGLE, PivShapeMode.SOLID_ROUNDED_RECTANGLE,
            PivShapeMode.OVAL, PivShapeMode.SOLID_OVAL -> {
                // Min between current size and calculated size (maybe different sizes are set exactly, eg. 120dp, 80dp)
                // In this case I center the shape into the view
                val smallX = Math.min(w.toFloat(), h * usedRatio)
                val smallY = Math.min(h.toFloat(), w / usedRatio)
                smallSize = Math.min(smallX, smallY)
                shapeBounds.set((w - smallX) / 2, (h - smallY) / 2, (w + smallX) / 2, (h + smallY) / 2)
            }
        }

        shapeBounds.set(
            shapeBounds.left + padding.left,
            shapeBounds.top + padding.top,
            shapeBounds.right - padding.right,
            shapeBounds.bottom - padding.bottom
        )

        // Border cannot be bigger than the shape! ("if" needed to avoid infinite recursion)
        if (borderWidth != borderWidth.coerceAtMost((shapeBounds.width() / 2).toInt())) {
            borderWidth = borderWidth.coerceAtMost((shapeBounds.width() / 2).toInt())
            return
        }

        borderBounds.set(shapeBounds)
        borderBounds.inset((borderWidth / 2).toFloat(), (borderWidth / 2).toFloat())

        // If border does not overlay, i shrink shape and image bounds
        if (!borderOverlay)
            shapeBounds.inset(borderWidth.toFloat(), borderWidth.toFloat())

        mCalculatedInnerPadding = smallSize * mInnerPaddingPercent / 100
        // if mInnerPadding is 0 or more, it overrides mInnerPaddingPercent parameter
        if (mInnerPadding >= 0) mCalculatedInnerPadding = mInnerPadding.toFloat()

        mCalculatedInnerPadding = mCalculatedInnerPadding.coerceAtMost(smallSize / 2 - 1)

        imageBounds.set(shapeBounds)
        imageBounds.inset(mCalculatedInnerPadding, mCalculatedInnerPadding)

        listener.get()?.onSizeUpdated(this)
    }

    /**
     * Set the listener that will update the shape drawers on changes
     *
     * Do not use this method, as it is intended for internal reasons!
     *
     * @param listener Listener that will update the shape drawers on changes
     */
    fun setListener(listener: ShapeOptionsListener) { this.listener = WeakReference(listener) }

    /**
     * Returns the inner padding of the image, relative to the shape
     * If you want to get the real inner padding used to show the image, call getCalculatedInnerPadding().
     *
     * @return Inner padding of the image, relative to the shape
     */
    fun getInnerPadding(): Int = mInnerPadding

    /**
     * Returns the inner padding of the image, relative to the shape, as a percentage value.
     * If you want to get the real inner padding used to show the image, call getCalculatedInnerPadding().
     *
     * @return Inner padding of the image, relative to the shape, as a percentage value.
     */
    fun getInnerPaddingPercent(): Float = mInnerPaddingPercent

    /**
     * Inner padding of the image relative to the shape, after calculations.
     * This will return the real value used by the shape drawer.
     *
     * @return Inner padding of the image relative to the shape, after calculations
     */
    fun getCalculatedInnerPadding(): Float = mCalculatedInnerPadding

    /**
     * Set the inner padding of the image relative to the shape.
     * If it's lower than 0, it is ignored.
     *
     * @param innerPadding Inner padding of the image relative to the shape
     */
    fun setInnerPadding(innerPadding: Int) {
        this.mInnerPadding = innerPadding
        recalculateLastBounds()
    }

    /**
     * Set the inner padding of the image relative to the shape, as a percentage of the shape size.
     * If the percentage is higher than 100, it is treated as (value % 100).
     *
     * @param innerPaddingPercent Inner padding of the image relative to the shape, as a percentage of the shape size,
     *  as a float from 0 to 100
     */
    fun setInnerPadding(innerPaddingPercent: Float) {
        this.mInnerPadding = -1
        this.mInnerPaddingPercent = innerPaddingPercent
        recalculateLastBounds()
    }

    interface ShapeOptionsListener {
        fun onOptionsUpdated(options: ShapeOptions)
        fun onSizeUpdated(options: ShapeOptions)
        fun onRequestMeasure(options: ShapeOptions)
    }
}
