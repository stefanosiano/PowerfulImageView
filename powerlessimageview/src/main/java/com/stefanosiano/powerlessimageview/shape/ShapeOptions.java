package com.stefanosiano.powerlessimageview.shape;

import java.lang.ref.WeakReference;

/**
 * Created by stefano on 05/04/17.
 */

public class ShapeOptions {

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
    public void calculateBounds(int w, int h, PivShapeMode mode){

    }

    public void setListener(ShapeOptionsListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    public interface ShapeOptionsListener{
        void onOptionsUpdated(ShapeOptions options);
        void onSizeUpdated(ShapeOptions options);
        void onModeUpdated(ShapeOptions options);
    }

}
