package com.stefanosiano.powerlessimageview.shape;

import java.lang.ref.WeakReference;

/**
 * Created by stefano on 05/04/17.
 */

public class ShapeOptions {


    // ************** Calculated fields *****************

    //bounds of the shape
    /** Left bound calculated */
    private float mCalculatedLeft;

    /** Top bound calculated */
    private float mCalculatedTop;

    /** Right bound calculated */
    private float mCalculatedRight;

    /** Bottom bound calculated */
    private float mCalculatedBottom;

    //last calculated width and height
    /** Last width calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastW;

    /** Last height calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastH;

    /** Last progress mode used. Used when changing programmatically the options, so bounds can be calculated directly */
    private PivShapeMode mCalculatedLastMode;

    /** Listener that will update the shape drawers on changes, with a weak reference to be sure to not leak memory */
    private WeakReference<ShapeOptionsListener> listener;



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
                mCalculatedLeft = (w - smallSize) /2;
                mCalculatedTop = (h - smallSize) /2;
                mCalculatedRight = (w + smallSize) /2;
                mCalculatedBottom = (h + smallSize) /2;
                break;

            default:
            case NORMAL:
                mCalculatedLeft = 0;
                mCalculatedTop = 0;
                mCalculatedRight = w;
                mCalculatedBottom = h;
                break;
        }

        mCalculatedLeft += paddingLeft;
        mCalculatedTop += paddingTop;
        mCalculatedRight -= paddingRight;
        mCalculatedBottom -= paddingBottom;
    }

    public void setListener(ShapeOptionsListener listener) {
        this.listener = new WeakReference<>(listener);
    }


    /** Returns the left bound calculated. Be sure to call calculateBounds() before this! */
    public final float getLeft() {
        return mCalculatedLeft;
    }

    /** Returns the top bound calculated. Be sure to call calculateBounds() before this! */
    public final float getTop() {
        return mCalculatedTop;
    }

    /** Returns the right bound calculated. Be sure to call calculateBounds() before this! */
    public final float getRight() {
        return mCalculatedRight;
    }

    /** Returns the bottom bound calculated. Be sure to call calculateBounds() before this! */
    public final float getBottom() {
        return mCalculatedBottom;
    }





    public interface ShapeOptionsListener{
        void onOptionsUpdated(ShapeOptions options);
        void onSizeUpdated(ShapeOptions options);
        void onModeUpdated(ShapeOptions options);
    }

}
