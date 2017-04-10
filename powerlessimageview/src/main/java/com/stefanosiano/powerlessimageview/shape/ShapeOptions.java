package com.stefanosiano.powerlessimageview.shape;

import java.lang.ref.WeakReference;

/**
 * Created by stefano on 05/04/17.
 */

public class ShapeOptions {

    /** Listener that will update the shape drawers on changes, with a weak reference to be sure to not leak memory */
    private WeakReference<ShapeOptionsListener> listener;


    public void calculateBounds(int width, int height, PivShapeMode shapeMode){

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
