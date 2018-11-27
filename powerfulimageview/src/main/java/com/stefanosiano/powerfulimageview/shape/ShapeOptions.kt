package com.stefanosiano.powerfulimageview.shape

import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import java.lang.ref.WeakReference


/**
 * Class that helps managing the options that will be used by the shape drawers.
 */

class ShapeOptions() : Parcelable {

    //Options used directly by drawers

    /** Background color of the shape  */
    internal var backgroundColor: Int = 0

    /** Foreground color of the shape  */
    internal var foregroundColor: Int = 0

    /** Inner padding of the image relative to the shape  */
    internal var mInnerPadding: Int = 0

    /** Inner padding of the image relative to the shape, as a percentage  */
    internal var mInnerPaddingPercent: Float = 0f

    /** Whether the border should be drawn over the image or not  */
    internal var borderOverlay: Boolean = false

    /** Color of the shape border  */
    internal var borderColor: Int = 0

    /** Width of the shape border  */
    internal var borderWidth: Int = 0
    
    /** Ratio of the shape  */
    internal var ratio: Float = 0f

    /** X radius of the rounded rectangles  */
    internal var radiusX: Float = 0f

    /** Y radius of the rounded rectangles  */
    internal var radiusY: Float = 0f

    /** Color used by solid shapes  */
    internal var solidColor: Int = 0

    /** Foreground drawable used by shapes  */
    internal var foregroundDrawable: Drawable? = null

    /** Background drawable used by shapes  */
    internal var backgroundDrawable: Drawable? = null

    // ************** Calculated fields *****************

    //bounds of the shape
    /** Bounds of the shape, calculated using [calculateBounds]. DON'T edit its values directly! */
    internal var shapeBounds = RectF(0f, 0f, 0f, 0f)

    /** Bounds of the image, calculated using [calculateBounds]. DON'T edit its values directly! */
    internal var imageBounds = RectF(0f, 0f, 0f, 0f)

    /** Bounds of the border, calculated using [calculateBounds]. DON'T edit its values directly! */
    internal var borderBounds = RectF(0f, 0f, 0f, 0f)

    /** Bounds of the view, without padding, calculated using [calculateBounds]. DON'T edit its values directly! */
    internal var viewBounds = RectF(0f, 0f, 0f, 0f)

    /** Calculated padding of the indicator shadow  */
    private var mCalculatedInnerPadding: Float = 0f

    //last calculated width and height
    /** Last left padding calculated. Used when changing programmatically the options, so bounds can be calculated directly  */
    private var mCalculatedLastPaddingLeft: Int = 0

    /** Last top padding calculated. Used when changing programmatically the options, so bounds can be calculated directly  */
    private var mCalculatedLastPaddingTop: Int = 0

    /** Last right padding calculated. Used when changing programmatically the options, so bounds can be calculated directly  */
    private var mCalculatedLastPaddingRight: Int = 0

    /** Last bottom padding calculated. Used when changing programmatically the options, so bounds can be calculated directly  */
    private var mCalculatedLastPaddingBottom: Int = 0

    /** Last width calculated. Used when changing programmatically the options, so bounds can be calculated directly  */
    private var mCalculatedLastW: Int = 0

    /** Last height calculated. Used when changing programmatically the options, so bounds can be calculated directly  */
    private var mCalculatedLastH: Int = 0

    /** Last progress mode used. Used when changing programmatically the options, so bounds can be calculated directly  */
    private var mCalculatedLastMode = PivShapeMode.NORMAL

    /** Listener that will update the shape drawers on changes, with a weak reference to be sure to not leak memory  */
    private var listener = WeakReference<ShapeOptionsListener>(null)


    /**
     * Creates the object that will be used by shape drawers:
     *
     * @param backgroundColor Background color of the shape
     * @param foregroundColor Foreground color of the shape
     * @param innerPadding Inner padding of the image relative to the shape. If it's 0 or more, it applies and overrides "innerPaddingPercent" parameter
     * @param innerPaddingPercent Inner padding of the image relative to the shape, as a percentage
     * @param borderOverlay Whether the border should be drawn over the image or not
     * @param borderColor Color of the shape border
     * @param borderWidth Width of the shape border
     * @param ratio Ratio of the shape. Width will be equal to (height * ratio). It's ignored in square and circle shapes
     */
    constructor(backgroundColor: Int, foregroundColor: Int, innerPadding: Int, innerPaddingPercent: Float, borderOverlay: Boolean,
                     borderColor: Int, borderWidth: Int, ratio: Float, radiusX: Float, radiusY: Float, solidColor: Int, backgroundDrawable: Drawable, foregroundDrawable: Drawable): this() {
        this.backgroundColor = backgroundColor
        this.foregroundColor = foregroundColor
        this.mInnerPadding = innerPadding
        this.mInnerPaddingPercent = innerPaddingPercent
        this.borderOverlay = borderOverlay
        this.borderColor = borderColor
        this.borderWidth = borderWidth
        this.ratio = ratio
        this.radiusX = radiusX
        this.radiusY = radiusY
        this.solidColor = solidColor
        this.backgroundDrawable = backgroundDrawable
        this.foregroundDrawable = foregroundDrawable
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
        this.borderColor = other.borderColor
        this.borderWidth = other.borderWidth
        this.ratio = other.ratio
        this.radiusX = other.radiusX
        this.radiusY = other.radiusY
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
    }

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
    fun calculateBounds(w: Int, h: Int, paddingLeft: Int, paddingTop: Int, paddingRight: Int, paddingBottom: Int, mode: PivShapeMode) {

        //saving last width and height, so i can later call this function from this class
        mCalculatedLastW = w
        mCalculatedLastH = h
        mCalculatedLastMode = mode
        mCalculatedLastPaddingLeft = paddingLeft
        mCalculatedLastPaddingTop = paddingTop
        mCalculatedLastPaddingRight = paddingRight
        mCalculatedLastPaddingBottom = paddingBottom

        viewBounds.set(0f, 0f, w.toFloat(), h.toFloat())

        //smallest size (used for padding and square/circle shapes)
        val smallSize: Float
        //if no ratio was set, i use the view ratio
        val usedRatio = if (ratio <= 0) w / h.toFloat() else ratio

        when (mode) {

            PivShapeMode.CIRCLE, PivShapeMode.SQUARE, PivShapeMode.SOLID_CIRCLE -> {
                smallSize = Math.min(h, w).toFloat()
                shapeBounds.set((w - smallSize) / 2, (h - smallSize) / 2, (w + smallSize) / 2, (h + smallSize) / 2)
            }

            PivShapeMode.RECTANGLE, PivShapeMode.ROUNDED_RECTANGLE, PivShapeMode.SOLID_ROUNDED_RECTANGLE, PivShapeMode.OVAL, PivShapeMode.SOLID_OVAL -> {
                //Min between current size and calculated size (may be different sizes are set exactly, eg. 120dp, 80dp)
                //In this case I center the shape into the view
                val smallX = Math.min(w.toFloat(), h * usedRatio).toInt().toFloat()
                val smallY = Math.min(h.toFloat(), w / usedRatio).toInt().toFloat()
                smallSize = Math.min(smallX, smallY).toInt().toFloat()
                shapeBounds.set((w - smallX) / 2, (h - smallY) / 2, (w + smallX) / 2, (h + smallY) / 2)
            }

            PivShapeMode.NORMAL -> {
                shapeBounds.set(0f, 0f, w.toFloat(), h.toFloat())
                smallSize = Math.min(w, h).toFloat()
            }

            else -> {
                shapeBounds.set(0f, 0f, w.toFloat(), h.toFloat())
                smallSize = Math.min(w, h).toFloat()
            }
        }

        shapeBounds.set(shapeBounds.left + paddingLeft,
                shapeBounds.top + paddingTop,
                shapeBounds.right - paddingRight,
                shapeBounds.bottom - paddingBottom)

        //Border cannot be bigger than the shape!
        borderWidth = borderWidth.coerceAtMost((shapeBounds.width() / 2).toInt())

        borderBounds.set(shapeBounds)
        borderBounds.inset((borderWidth / 2).toFloat(), (borderWidth / 2).toFloat())

        //If border does not overlay, i shrink shape and image bounds
        if (!borderOverlay)
            shapeBounds.inset(borderWidth.toFloat(), borderWidth.toFloat())

        mCalculatedInnerPadding = smallSize * mInnerPaddingPercent / 100
        //if mInnerPadding is 0 or more, it overrides mInnerPaddingPercent parameter
        if (mInnerPadding >= 0) mCalculatedInnerPadding = mInnerPadding.toFloat()

        mCalculatedInnerPadding = mCalculatedInnerPadding.coerceAtMost(smallSize / 2 - 1)

        imageBounds.set(shapeBounds)
        imageBounds.inset(mCalculatedInnerPadding, mCalculatedInnerPadding)
    }

    /**
     * Set the listener that will update the shape drawers on changes
     *
     * Do not use this method, as it is intended for internal reasons!
     *
     * @param listener Listener that will update the shape drawers on changes
     */
    fun setListener(listener: ShapeOptionsListener) { this.listener = WeakReference(listener) }

    /** Set the background color of the image, using the shape.
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.  */
    fun setBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
        listener.get()?.onOptionsUpdated(this)
    }

    /** Set the background color of the image, using the shape.
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.  */
    fun setForegroundColor(foregroundColor: Int) {
        this.foregroundColor = foregroundColor
        listener.get()?.onOptionsUpdated(this)
    }

    /** Set the foreground drawable to be drawn over the image, using the shape.
     * Note: Does not work on rounded shapes!  */
    fun setForegroundDrawable(foregroundDrawable: Drawable) {
        this.foregroundDrawable = foregroundDrawable
        listener.get()?.onOptionsUpdated(this)
    }

    /** Set the background drawable to be drawn under the image, using the shape.
     * Note: Does not work on rounded shapes!  */
    fun setBackgroundDrawable(backgroundDrawable: Drawable) {
        this.backgroundDrawable = backgroundDrawable
        listener.get()?.onOptionsUpdated(this)
    }

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

    /** Set the border color of the shape.
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.  */
    fun setBorderColor(borderColor: Int) {
        this.borderColor = borderColor
        listener.get()?.onOptionsUpdated(this)
    }

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
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode)
        listener.get()?.onSizeUpdated(this)
    }

    /**
     * Set the inner padding of the image relative to the shape, as a percentage of the shape size.
     * If the percentage is higher than 100, it is treated as (value % 100).
     *
     * @param innerPaddingPercent Inner padding of the image relative to the shape, as a percentage of the shape size, as a float from 0 to 100
     */
    fun setInnerPadding(innerPaddingPercent: Float) {
        this.mInnerPadding = -1
        this.mInnerPaddingPercent = innerPaddingPercent
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode)
        listener.get()?.onSizeUpdated(this)
    }

    /**
     * Set whether border should be drawn over the shape or not.
     *
     * @param borderOverlay If true, the border is drawn over the shape, otherwise the shape is shrinked
     */
    fun setBorderOverlay(borderOverlay: Boolean) {
        this.borderOverlay = borderOverlay
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode)
        listener.get()?.onSizeUpdated(this)
    }

    /**
     * Set the width of the shape border
     * If you want to use dp, set value using TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, getResources().getDisplayMetrics())
     *
     * @param borderWidth Width of the shape border.
     */
    fun setBorderWidth(borderWidth: Int) {
        this.borderWidth = borderWidth
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode)
        listener.get()?.onSizeUpdated(this)
    }

    /**
     * Set the ratio of the shape
     * It's ignored in Circle and Square shapes.
     * Width will be calculated as height * ratio
     *
     * @param ratio Ratio of the shape
     */
    fun setRatio(ratio: Float) {
        this.ratio = ratio
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode)
        listener.get()?.onRequestMeasure(this)
    }

    /** Set the x radius of the shape. Used by rounded rectangles  */
    fun setRadiusX(radiusX: Float) {
        this.radiusX = radiusX
        listener.get()?.onOptionsUpdated(this)
    }

    /** Set the y radius of the shape. Used by rounded rectangles  */
    fun setRadiusY(radiusY: Float) {
        this.radiusY = radiusY
        listener.get()?.onOptionsUpdated(this)
    }

    /** Set the solid color of the solid shapes.
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.  */
    fun setSolidColor(solidColor: Int) {
        this.solidColor = solidColor
        listener.get()?.onOptionsUpdated(this)
    }


    interface ShapeOptionsListener {
        fun onOptionsUpdated(options: ShapeOptions)
        fun onSizeUpdated(options: ShapeOptions)
        fun onRequestMeasure(options: ShapeOptions)
    }


    //PARCELABLE STUFF

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(backgroundColor)
        dest.writeInt(foregroundColor)
        dest.writeInt(mInnerPadding)
        dest.writeFloat(mInnerPaddingPercent)
        dest.writeByte((if (borderOverlay) 1 else 0).toByte())
        dest.writeInt(borderColor)
        dest.writeInt(borderWidth)
        dest.writeFloat(ratio)
        dest.writeFloat(radiusX)
        dest.writeFloat(radiusY)
        dest.writeInt(solidColor)
        dest.writeParcelable(shapeBounds, flags)
        dest.writeParcelable(imageBounds, flags)
        dest.writeParcelable(borderBounds, flags)
        dest.writeParcelable(viewBounds, flags)
        dest.writeFloat(mCalculatedInnerPadding)
        dest.writeInt(mCalculatedLastPaddingLeft)
        dest.writeInt(mCalculatedLastPaddingTop)
        dest.writeInt(mCalculatedLastPaddingRight)
        dest.writeInt(mCalculatedLastPaddingBottom)
        dest.writeInt(mCalculatedLastW)
        dest.writeInt(mCalculatedLastH)
    }


    private constructor(`in`: Parcel): this() {
        backgroundColor = `in`.readInt()
        foregroundColor = `in`.readInt()
        mInnerPadding = `in`.readInt()
        mInnerPaddingPercent = `in`.readFloat()
        borderOverlay = `in`.readByte().toInt() != 0
        borderColor = `in`.readInt()
        borderWidth = `in`.readInt()
        ratio = `in`.readFloat()
        radiusX = `in`.readFloat()
        radiusY = `in`.readFloat()
        solidColor = `in`.readInt()
        shapeBounds = `in`.readParcelable(RectF::class.java.classLoader) ?: RectF(0f, 0f, 0f, 0f)
        imageBounds = `in`.readParcelable(RectF::class.java.classLoader) ?: RectF(0f, 0f, 0f, 0f)
        borderBounds = `in`.readParcelable(RectF::class.java.classLoader) ?: RectF(0f, 0f, 0f, 0f)
        viewBounds = `in`.readParcelable(RectF::class.java.classLoader) ?: RectF(0f, 0f, 0f, 0f)
        mCalculatedInnerPadding = `in`.readFloat()
        mCalculatedLastPaddingLeft = `in`.readInt()
        mCalculatedLastPaddingTop = `in`.readInt()
        mCalculatedLastPaddingRight = `in`.readInt()
        mCalculatedLastPaddingBottom = `in`.readInt()
        mCalculatedLastW = `in`.readInt()
        mCalculatedLastH = `in`.readInt()
    }
    
    companion object CREATOR : Parcelable.Creator<ShapeOptions> {
        override fun createFromParcel(parcel: Parcel): ShapeOptions = ShapeOptions(parcel)
        override fun newArray(size: Int): Array<ShapeOptions?> = arrayOfNulls(size)
    }
}