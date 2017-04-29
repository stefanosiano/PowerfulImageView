package com.stefanosiano.powerlessimageview.shape;

import android.graphics.RectF;

import java.lang.ref.WeakReference;

/**
 * Created by stefano on 05/04/17.
 */

public class ShapeOptions {


    // ************** Calculated fields *****************

    //bounds of the shape
    /** Calculated shape bounds */
    private final RectF mShapeBounds;

    /** Calculated image bounds */
    private final RectF mImageBounds;

    /** Calculated border bounds */
    private final RectF mBorderBounds;

    //last calculated width and height
    /** Last width calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastW;

    /** Last height calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastH;

    /** Last progress mode used. Used when changing programmatically the options, so bounds can be calculated directly */
    private PivShapeMode mCalculatedLastMode;

    /** Listener that will update the shape drawers on changes, with a weak reference to be sure to not leak memory */
    private WeakReference<ShapeOptionsListener> listener;

    private int mBackgroundColor;
    private int mFrontgroundColor;

    private int mInnerPadding;
    private float mInnerPaddingPercent;
    private boolean mBorderOverlay;
    private int mBorderColor;
    private int mBorderWidth;

    // ************** Calculated fields *****************

    /** Calculated padding of the indicator shadow */
    private float mCalculatedInnerPadding;

    public ShapeOptions(int backgroundColor, int frontgroundColor, int innerPadding, float innerPaddingPercent,
                        boolean borderOverlay, int borderColor, int borderWidth) {
        this.mBackgroundColor = backgroundColor;
        this.mFrontgroundColor = frontgroundColor;
        this.mInnerPadding = innerPadding;
        this.mInnerPaddingPercent = innerPaddingPercent;
        this.mBorderOverlay = borderOverlay;
        this.mBorderColor = borderColor;
        this.mBorderWidth = borderWidth;
        this.mShapeBounds = new RectF(0, 0, 0, 0);
        this.mBorderBounds = new RectF(0, 0, 0, 0);
        this.mImageBounds = new RectF(0, 0, 0, 0);
    }

    /**
     * Calculates the bounds of the image, based on shape options and mode.
     * Calculated bounds are accessible after this call through getLeft(), getTop(), getRight() and getBottom() methods.
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

        int smallSize = w < h ? w : h;

        switch(mode){
            case CIRCLE:
                mShapeBounds.set((w - smallSize) /2,
                        (h - smallSize) /2,
                        (w + smallSize) /2,
                        (h + smallSize) /2);
                break;

            default:
            case NORMAL:
                mShapeBounds.set(0, 0, w, h);
                break;
        }

        mShapeBounds.set(mShapeBounds.left + paddingLeft,
                mShapeBounds.top + paddingTop,
                mShapeBounds.right - paddingRight,
                mShapeBounds.bottom - paddingBottom);

        mBorderBounds.set(mShapeBounds);

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




    public interface ShapeOptionsListener{
        void onOptionsUpdated(ShapeOptions options);
        void onSizeUpdated(ShapeOptions options);
        void onModeUpdated(ShapeOptions options);
    }

}
