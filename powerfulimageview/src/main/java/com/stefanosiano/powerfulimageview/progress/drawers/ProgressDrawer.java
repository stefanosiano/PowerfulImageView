package com.stefanosiano.powerfulimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.stefanosiano.powerfulimageview.progress.ProgressOptions;

/**
 * Interface that handles options, drawing and updating of the progress indicators on the View.
 */
interface ProgressDrawer {

    /**
     * Initialize or updates all the variables needed to work.
     *
     * @param progressOptions Options to take values from
     */
    void setup(ProgressOptions progressOptions);

    /**
     * Draws the progress indicator.
     * No operation should be performed here, except drawing, for efficiency.
     * No object creation, no allocation, no calculation. Just draw.
     *
     * @param canvas Canvas of the View
     * @param progressBounds Bounds of the progress indicator
     */
    void draw(Canvas canvas, RectF progressBounds);

    /** Starts the animations (valid for indeterminate drawers)
     * Calling it multiple times will cause the animation to restart! */
    void startIndeterminateAnimation();

    /** Stops the animations (valid for indeterminate drawers) */
    void stopIndeterminateAnimation();

    /**
     * Sets the percentage of the progress (valid for determinate drawers).
     * The transition from the current value to the new one is done through animation, if enabled in options
     *
     * @param progressPercent Percentage of the progress
     */
    void setProgressPercent(float progressPercent);

    /**
     * Enabled the animation (valid for determinate drawers)
     *
     * @param enabled True to use animation, false otherwise
     */
    void setAnimationEnabled(boolean enabled);

    /**
     * Sets the duration of the animation (valid for all drawers).
     * On determinate drawers it sets the duration of transition from old percentage value to new one.
     * On indeterminate drawers it sets the duration of the expanding/shrinking animation.
     *
     * @param millis Duration of the animation in milliseconds
     */
    void setAnimationDuration(long millis);

    /**
     * Set the listener to handle things coming from the drawer
     * @param listener The listener
     */
    void setListener(ProgressDrawerManager.ProgressDrawerListener listener);
}
