package com.stefanosiano.powerlessimageview.shape;

import android.graphics.RectF;

import java.lang.ref.WeakReference;

/**
 * Created by stefano on 05/04/17.
 */

public class ShapeOptions {


    // ************** Calculated fields *****************

    //bounds of the shape
    /** Calculated shape bounds calculated */
    private final RectF mRect;

    //last calculated width and height
    /** Last width calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastW;

    /** Last height calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastH;

    /** Last progress mode used. Used when changing programmatically the options, so bounds can be calculated directly */
    private PivShapeMode mCalculatedLastMode;

    /** Listener that will update the shape drawers on changes, with a weak reference to be sure to not leak memory */
    private WeakReference<ShapeOptionsListener> listener;


    public ShapeOptions() {
        this.mRect = new RectF(0, 0, 0, 0);
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
                mRect.set((w - smallSize) /2,
                        (h - smallSize) /2,
                        (w + smallSize) /2,
                        (h + smallSize) /2);
                break;

            default:
            case NORMAL:
                mRect.set(0, 0, w, h);
                break;
        }

        mRect.set(mRect.left + paddingLeft,
                mRect.top + paddingTop,
                mRect.right + paddingRight,
                mRect.bottom + paddingBottom);
    }

    public void setListener(ShapeOptionsListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    /** Returns the shape calculated bounds. Be sure to call calculateBounds() before this!
     * Don't change directly its values! If you want to change them, create a copy! */
    public RectF getRect() {
        return mRect;
    }





    public interface ShapeOptionsListener{
        void onOptionsUpdated(ShapeOptions options);
        void onSizeUpdated(ShapeOptions options);
        void onModeUpdated(ShapeOptions options);
    }

}
