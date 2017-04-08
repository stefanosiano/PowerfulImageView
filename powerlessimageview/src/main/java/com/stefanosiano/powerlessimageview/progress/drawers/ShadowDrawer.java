package com.stefanosiano.powerlessimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.stefanosiano.powerlessimageview.progress.ProgressOptions;

/**
 * Interface that handles options, drawing and updating of the progress indicators shadow on the View.
 */

interface ShadowDrawer {

    /**
     * Initialize or updates all the variables needed to work.
     *
     * @param progressOptions Options to take values from
     */
    void setup(ProgressOptions progressOptions);


    /**
     * Draws the cancel progress indicator.
     * No operation should be performed here, except drawing, for efficiency.
     * No object creation, no allocation, no calculation and no if/else. Just draw.
     *
     * @param canvas Canvas of the View
     * @param shadowBounds Bounds of the progress indicator shadow
     */
    void draw(Canvas canvas, RectF shadowBorderBounds, RectF shadowBounds);
}
