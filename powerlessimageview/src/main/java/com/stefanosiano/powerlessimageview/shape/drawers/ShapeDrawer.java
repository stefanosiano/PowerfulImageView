package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.stefanosiano.powerlessimageview.shape.ShapeOptions;

/**
 * Interface that handles options, drawing and updating of the image shapes on the View.
 */
interface ShapeDrawer {

    /**
     * Method that updates the drawable and bitmap to show
     *
     * @param drawable drawable to show on normal, square and rectangle shapes
     * @param bitmap bitmap to show on rounded, circle and oval shapes
     */
    void changeBitmap(Drawable drawable, Bitmap bitmap);

    /**
     * Sets the matrix to be used in the shape, and the current scale type
     *
     * @param scaleType ScaleType selected for the shape
     * @param matrix Matrix to be applied for the shape
     */
    void setMatrix(ImageView.ScaleType scaleType, Matrix matrix);

    /**
     * Initialize or updates all the variables needed to work.
     *
     * @param shapeOptions Options to take values from
     */
    void setup(ShapeOptions shapeOptions);

    /**
     * Draws the progress indicator.
     * No operation should be performed here, except drawing, for efficiency.
     * No object creation, no allocation, no calculation. Just draw.
     *
     * @param canvas Canvas of the View
     * @param shapeBounds Bounds of the shape
     * @param imageBounds Bounds of the image (drawable)
     */
    void draw(Canvas canvas, RectF shapeBounds, RectF imageBounds);
}
