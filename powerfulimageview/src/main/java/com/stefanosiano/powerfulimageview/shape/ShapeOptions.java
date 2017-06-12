package com.stefanosiano.powerfulimageview.shape;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.ref.WeakReference;

/**
 * Class that helps managing the options that will be used by the shape drawers.
 */

public final class ShapeOptions implements Parcelable {

    //Options used directly by drawers

    /** Background color of the shape */
    private int mBackgroundColor;

    /** Frontground color of the shape */
    private int mFrontgroundColor;

    /** Inner padding of the image relative to the shape */
    private int mInnerPadding;

    /** Inner padding of the image relative to the shape, as a percentage */
    private float mInnerPaddingPercent;

    /** Whether the border should be drawn over the image or not */
    private boolean mBorderOverlay;

    /** Color of the shape border */
    private int mBorderColor;

    /** Width of the shape border */
    private int mBorderWidth;

    /** Ratio of the shape */
    private float mRatio;

    /** X radius of the rounded rectangles */
    private float mRadiusX;

    /** Y radius of the rounded rectangles */
    private float mRadiusY;

    /** Color used by solid shapes */
    private int mSolidColor;

    // ************** Calculated fields *****************

    //bounds of the shape
    /** Calculated shape bounds */
    private final RectF mShapeBounds;

    /** Calculated image bounds */
    private final RectF mImageBounds;

    /** Calculated border bounds */
    private final RectF mBorderBounds;

    /** Calculated border bounds */
    private final RectF mViewBounds;

    /** Calculated padding of the indicator shadow */
    private float mCalculatedInnerPadding;

    //last calculated width and height
    /** Last left padding calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastPaddingLeft;

    /** Last top padding calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastPaddingTop;

    /** Last right padding calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastPaddingRight;

    /** Last bottom padding calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastPaddingBottom;

    /** Last width calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastW;

    /** Last height calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastH;

    /** Last progress mode used. Used when changing programmatically the options, so bounds can be calculated directly */
    private PivShapeMode mCalculatedLastMode;

    /** Listener that will update the shape drawers on changes, with a weak reference to be sure to not leak memory */
    private WeakReference<ShapeOptionsListener> listener;


    /**
     * Creates the object that will be used by shape drawers:
     *
     * @param backgroundColor Background color of the shape
     * @param frontgroundColor Frontground color of the shape
     * @param innerPadding Inner padding of the image relative to the shape. If it's 0 or more, it applies and overrides "innerPaddingPercent" parameter
     * @param innerPaddingPercent Inner padding of the image relative to the shape, as a percentage
     * @param borderOverlay Whether the border should be drawn over the image or not
     * @param borderColor Color of the shape border
     * @param borderWidth Width of the shape border
     * @param ratio Ratio of the shape. Width will be equal to (height * ratio). It's ignored in square and circle shapes
     */
    public ShapeOptions(int backgroundColor, int frontgroundColor, int innerPadding, float innerPaddingPercent, boolean borderOverlay,
                        int borderColor, int borderWidth, float ratio, float radiusX, float radiusY, int solidColor) {
        this.mBackgroundColor = backgroundColor;
        this.mFrontgroundColor = frontgroundColor;
        this.mInnerPadding = innerPadding;
        this.mInnerPaddingPercent = innerPaddingPercent;
        this.mBorderOverlay = borderOverlay;
        this.mBorderColor = borderColor;
        this.mBorderWidth = borderWidth;
        this.mRatio = ratio;
        this.mRadiusX = radiusX;
        this.mRadiusY = radiusY;
        this.mSolidColor = solidColor;
        this.mShapeBounds = new RectF(0, 0, 0, 0);
        this.mBorderBounds = new RectF(0, 0, 0, 0);
        this.mViewBounds = new RectF(0, 0, 0, 0);
        this.mImageBounds = new RectF(0, 0, 0, 0);

        this.mCalculatedInnerPadding = 0;
        this.mCalculatedLastW = 0;
        this.mCalculatedLastH = 0;
        this.mCalculatedLastPaddingLeft = 0;
        this.mCalculatedLastPaddingTop = 0;
        this.mCalculatedLastPaddingRight = 0;
        this.mCalculatedLastPaddingBottom = 0;
        this.mCalculatedLastMode = PivShapeMode.NORMAL;
        this.listener = new WeakReference<>(null);
    }

    /** Updates the values of the current options, copying the passed values */
    public void setOptions(ShapeOptions other) {
        this.mBackgroundColor = other.mBackgroundColor;
        this.mFrontgroundColor = other.mFrontgroundColor;
        this.mInnerPadding = other.mInnerPadding;
        this.mInnerPaddingPercent = other.mInnerPaddingPercent;
        this.mBorderOverlay = other.mBorderOverlay;
        this.mBorderColor = other.mBorderColor;
        this.mBorderWidth = other.mBorderWidth;
        this.mRatio = other.mRatio;
        this.mRadiusX = other.mRadiusX;
        this.mRadiusY = other.mRadiusY;
        this.mSolidColor = other.mSolidColor;
        this.mShapeBounds.set(other.mShapeBounds);
        this.mImageBounds.set(other.mImageBounds);
        this.mBorderBounds.set(other.mBorderBounds);
        this.mViewBounds.set(other.mViewBounds);
        this.mCalculatedInnerPadding = other.mCalculatedInnerPadding;
        this.mCalculatedLastW = other.mCalculatedLastW;
        this.mCalculatedLastH = other.mCalculatedLastH;
        this.mCalculatedLastMode = other.mCalculatedLastMode;
        this.mCalculatedLastPaddingLeft = other.mCalculatedLastPaddingLeft;
        this.mCalculatedLastPaddingTop = other.mCalculatedLastPaddingTop;
        this.mCalculatedLastPaddingRight = other.mCalculatedLastPaddingRight;
        this.mCalculatedLastPaddingBottom = other.mCalculatedLastPaddingBottom;
        this.listener = other.listener;
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
    public void calculateBounds(int w, int h, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom, PivShapeMode mode){

        //saving last width and height, so i can later call this function from this class
        mCalculatedLastW = w;
        mCalculatedLastH = h;
        mCalculatedLastMode = mode;
        mCalculatedLastPaddingLeft = paddingLeft;
        mCalculatedLastPaddingTop = paddingTop;
        mCalculatedLastPaddingRight = paddingRight;
        mCalculatedLastPaddingBottom = paddingBottom;

        mViewBounds.set(0, 0, w, h);

        //smallest size (used for padding and square/circle shapes)
        float smallSize;
        //if no ratio was set, i use the view ratio
        float usedRatio = mRatio <= 0 ? w / (float) h : mRatio;

        switch(mode){

            case CIRCLE:
            case SQUARE:
            case SOLID_CIRCLE:
                smallSize = Math.min(h, w);
                mShapeBounds.set((w - smallSize) /2,
                        (h - smallSize) /2,
                        (w + smallSize) /2,
                        (h + smallSize) /2);
                break;

            case RECTANGLE:
            case ROUNDED_RECTANGLE:
            case SOLID_ROUNDED_RECTANGLE:
            case OVAL:
            case SOLID_OVAL:
                //Min between current size and calculated size (may be different sizes are set exactly, eg. 120dp, 80dp)
                //In this case I center the shape into the view
                float smallX = (int) Math.min(w, h * usedRatio);
                float smallY = (int) Math.min(h, w / usedRatio);
                smallSize = (int) Math.min(smallX, smallY);
                mShapeBounds.set((w - smallX) /2,
                        (h - smallY) /2,
                        (w + smallX) /2,
                        (h + smallY) /2);
                break;

            default:
            case NORMAL:
                mShapeBounds.set(0, 0, w, h);
                smallSize = Math.min(w, h);
                break;
        }

        mShapeBounds.set(mShapeBounds.left + paddingLeft,
                mShapeBounds.top + paddingTop,
                mShapeBounds.right - paddingRight,
                mShapeBounds.bottom - paddingBottom);

        //Border cannot be bigger than the shape!
        if(mBorderWidth > mShapeBounds.width()/2) mBorderWidth = (int) mShapeBounds.width()/2;

        mBorderBounds.set(mShapeBounds);
        mBorderBounds.inset(mBorderWidth/2, mBorderWidth/2);

        //If border does not overlay, i shrink shape and image bounds
        if(!mBorderOverlay)
            mShapeBounds.inset(mBorderWidth, mBorderWidth);

        mCalculatedInnerPadding = smallSize * mInnerPaddingPercent / 100;
        //if mInnerPadding is 0 or more, it overrides mInnerPaddingPercent parameter
        if(mInnerPadding >= 0) mCalculatedInnerPadding = mInnerPadding;

        if(mCalculatedInnerPadding >= smallSize / 2)
            mCalculatedInnerPadding = smallSize / 2 - 1;

        mImageBounds.set(mShapeBounds);
        mImageBounds.inset(mCalculatedInnerPadding, mCalculatedInnerPadding);
    }

    /**
     * Set the listener that will update the shape drawers on changes
     *
     * Do not use this method, as it is intended for internal reasons!
     *
     * @param listener Listener that will update the shape drawers on changes
     */
    public void setListener(ShapeOptionsListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    /** Returns the shape calculated bounds. Be sure to call calculateBounds() before this!
     * Don't change directly its values! If you want to change them, create a copy! */
    public RectF getShapeBounds() {
        return mShapeBounds;
    }

    /** Returns the border calculated bounds. Be sure to call calculateBounds() before this!
     * Don't change directly its values! If you want to change them, create a copy! */
    public RectF getBorderBounds() {
        return mBorderBounds;
    }

    /** Returns the view calculated bounds, without padding. Be sure to call calculateBounds() before this!
     * Don't change directly its values! If you want to change them, create a copy! */
    public RectF getViewBounds() {
        return mViewBounds;
    }

    /** Returns the image calculated bounds. Be sure to call calculateBounds() before this!
     * Don't change directly its values! If you want to change them, create a copy! */
    public RectF getImageBounds() {
        return mImageBounds;
    }

    /**
     * @return Width of the border.
     */
    public int getBorderWidth() {
        return mBorderWidth;
    }

    /** Set the background color of the image, using the shape.
    Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
    premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
    See the Color class for more details. */
    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /** Set the background color of the image, using the shape.
     Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     See the Color class for more details. */
    public void setFrontgroundColor(int frontgroundColor) {
        this.mFrontgroundColor = frontgroundColor;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /**
     *
     * @return Background color of the shape
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * @return Frontground color of the shape
     */
    public int getFrontgroundColor() {
        return mFrontgroundColor;
    }

    /**
     * @return Border color of the shape
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * @return Ratio of the shape
     */
    public float getRatio() {
        return mRatio;
    }

    /**
     * @return X radius of the shape, used by rounded rectangles
     */
    public float getRadiusX() {
        return mRadiusX;
    }

    /**
     * @return Y radius of the shape, used by rounded rectangles
     */
    public float getRadiusY() {
        return mRadiusY;
    }

    /**
     * Returns the inner padding of the image, relative to the shape
     * If you want to get the real inner padding used to show the image, call getCalculatedInnerPadding().
     *
     * @return Inner padding of the image, relative to the shape
     */
    public int getInnerPadding() {
        return mInnerPadding;
    }

    /**
     * Returns the inner padding of the image, relative to the shape, as a percentage value.
     * If you want to get the real inner padding used to show the image, call getCalculatedInnerPadding().
     *
     * @return Inner padding of the image, relative to the shape, as a percentage value.
     */
    public float getInnerPaddingPercent() {
        return mInnerPaddingPercent;
    }

    /**
     * If the border should be drawn over the shape or not.
     *
     * @return true if it's drown over the shape, false if the shape is shrinked
     */
    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    /** Set the border color of the shape.
     Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     See the Color class for more details. */
    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /**
     * Inner padding of the image relative to the shape, after calculations.
     * This will return the real value used by the shape drawer.
     *
     * @return Inner padding of the image relative to the shape, after calculations
     */
    public float getCalculatedInnerPadding() {
        return mCalculatedInnerPadding;
    }


    /**
     * @return Solid color used by solid shapes
     */
    public int getSolidColor() {
        return mSolidColor;
    }

    /**
     * Set the inner padding of the image relative to the shape.
     * If it's lower than 0, it is ignored.
     * Overrides inner padding set through setInnerPaddingPercent().
     *
     * @param innerPadding Inner padding of the image relative to the shape
     */
    public void setInnerPadding(int innerPadding) {
        this.mInnerPadding = innerPadding;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set the inner padding of the image relative to the shape, as a percentage of the shape size.
     * It's used only if innerPadding is less than 0.
     * If the percentage is higher than 100, it is treated as (value % 100).
     *
     * @param innerPaddingPercent Inner padding of the image relative to the shape, as a percentage of the shape size, as a float from 0 to 100
     */
    public void setInnerPaddingPercent(float innerPaddingPercent) {
        this.mInnerPaddingPercent = innerPaddingPercent;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set whether border should be drawn over the shape or not.
     *
     * @param borderOverlay If true, the border is drawn over the shape, otherwise the shape is shrinked
     */
    public void setBorderOverlay(boolean borderOverlay) {
        this.mBorderOverlay = borderOverlay;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set the width of the shape border
     * If you want to use dp, set value using TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, getResources().getDisplayMetrics())
     *
     * @param borderWidth Width of the shape border.
     */
    public void setBorderWidth(int borderWidth) {
        this.mBorderWidth = borderWidth;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set the ratio of the shape
     * It's ignored in Circle and Square shapes.
     * Width will be calculated as height * ratio
     *
     * @param ratio Ratio of the shape
     */
    public void setRatio(float ratio) {
        this.mRatio = ratio;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastPaddingLeft, mCalculatedLastPaddingTop, mCalculatedLastPaddingRight, mCalculatedLastPaddingBottom, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onRequestMeasure(this);
    }

    /** Set the x radius of the shape. Used by rounded rectangles */
    public void setRadiusX(float radiusX) {
        this.mRadiusX = radiusX;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /** Set the y radius of the shape. Used by rounded rectangles */
    public void setRadiusY(float radiusY) {
        this.mRadiusY = radiusY;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /** Set the solid color of the solid shapes.
     Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     See the Color class for more details. */
    public void setSolidColor(int solidColor) {
        this.mSolidColor = solidColor;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }







    public interface ShapeOptionsListener{
        void onOptionsUpdated(ShapeOptions options);
        void onSizeUpdated(ShapeOptions options);
        void onRequestMeasure(ShapeOptions options);
    }





    //PARCELABLE STUFF

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mBackgroundColor);
        dest.writeInt(mFrontgroundColor);
        dest.writeInt(mInnerPadding);
        dest.writeFloat(mInnerPaddingPercent);
        dest.writeByte((byte) (mBorderOverlay ? 1 : 0));
        dest.writeInt(mBorderColor);
        dest.writeInt(mBorderWidth);
        dest.writeFloat(mRatio);
        dest.writeFloat(mRadiusX);
        dest.writeFloat(mRadiusY);
        dest.writeInt(mSolidColor);
        dest.writeParcelable(mShapeBounds, flags);
        dest.writeParcelable(mImageBounds, flags);
        dest.writeParcelable(mBorderBounds, flags);
        dest.writeParcelable(mViewBounds, flags);
        dest.writeFloat(mCalculatedInnerPadding);
        dest.writeInt(mCalculatedLastPaddingLeft);
        dest.writeInt(mCalculatedLastPaddingTop);
        dest.writeInt(mCalculatedLastPaddingRight);
        dest.writeInt(mCalculatedLastPaddingBottom);
        dest.writeInt(mCalculatedLastW);
        dest.writeInt(mCalculatedLastH);
    }



    private ShapeOptions(Parcel in) {
        mBackgroundColor = in.readInt();
        mFrontgroundColor = in.readInt();
        mInnerPadding = in.readInt();
        mInnerPaddingPercent = in.readFloat();
        mBorderOverlay = in.readByte() != 0;
        mBorderColor = in.readInt();
        mBorderWidth = in.readInt();
        mRatio = in.readFloat();
        mRadiusX = in.readFloat();
        mRadiusY = in.readFloat();
        mSolidColor = in.readInt();
        mShapeBounds = in.readParcelable(RectF.class.getClassLoader());
        mImageBounds = in.readParcelable(RectF.class.getClassLoader());
        mBorderBounds = in.readParcelable(RectF.class.getClassLoader());
        mViewBounds = in.readParcelable(RectF.class.getClassLoader());
        mCalculatedInnerPadding = in.readFloat();
        mCalculatedLastPaddingLeft = in.readInt();
        mCalculatedLastPaddingTop = in.readInt();
        mCalculatedLastPaddingRight = in.readInt();
        mCalculatedLastPaddingBottom = in.readInt();
        mCalculatedLastW = in.readInt();
        mCalculatedLastH = in.readInt();
    }

    public static final Creator<ShapeOptions> CREATOR = new Creator<ShapeOptions>() {
        @Override
        public ShapeOptions createFromParcel(Parcel in) {
            return new ShapeOptions(in);
        }

        @Override
        public ShapeOptions[] newArray(int size) {
            return new ShapeOptions[size];
        }
    };

}
