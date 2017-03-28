package com.stefanosiano.powerfulimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.stefanosiano.powerfulimageview.progress.ProgressOptions;

/**
 * Interface that handles options, drawing and updating of the cancel progress indicators on the View.
 */

interface CancelDrawer {

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
     * @param cancelBounds Bounds of the cancel progress background indicator
     * @param cancelIconBounds Bounds of the cancel progress icon indicator
     */
    void draw(Canvas canvas, RectF cancelBounds, RectF cancelIconBounds);
}
