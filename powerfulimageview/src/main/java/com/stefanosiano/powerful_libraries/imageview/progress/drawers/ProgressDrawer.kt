package com.stefanosiano.powerful_libraries.imageview.progress.drawers

import android.graphics.Canvas
import android.graphics.RectF
import com.stefanosiano.powerful_libraries.imageview.progress.ProgressOptions

/** Interface that handles options, drawing and updating of the progress indicators on the View. */
internal interface ProgressDrawer {

    /** Initialize or updates all the variables needed to work, reading values from [progressOptions]. */
    fun setup(progressOptions: ProgressOptions)

    /**
     * Draws the progress indicator, using [canvas] and [progressBounds].
     * No operation should be performed here, except drawing, for efficiency.
     * No object creation, no allocation, no calculation. Just draw.
     */
    fun draw(canvas: Canvas, progressBounds: RectF)

    /**
     * Starts the animations (for indeterminate drawers).
     * Calling it multiple times will cause the animation to restart!
     */
    fun startIndeterminateAnimation()

    /** Stops the animations (valid for indeterminate drawers). */
    fun stopIndeterminateAnimation()

    /**
     * Set the [progressPercent] as percentage of the progress (for determinate drawers).
     * The transition from the current value to the new one is done through animation, if enabled in options.
     */
    fun setProgressPercent(progressPercent: Float)

    /** Set if the animation is [enabled] (for determinate drawers). */
    fun setAnimationEnabled(enabled: Boolean)

    /**
     * Set the duration of the animation in [millis] milliseconds (valid for all drawers).
     * On determinate drawers it sets the duration of transition from old percentage value to new one.
     * On indeterminate drawers it sets the duration of the expanding/shrinking animation.
     */
    fun setAnimationDuration(millis: Long)

    /** Set the [listener] to handle things coming from the drawer. */
    fun setListener(listener: ProgressDrawerManager.ProgressDrawerListener)
}

/**
 * Dummy progress drawer that doesn't do anything.
 * Used when progress is disabled, so functions can be called without checks with no problem.
 */
internal class DummyProgressDrawer : ProgressDrawer {
    override fun setup(progressOptions: ProgressOptions) {}
    override fun draw(canvas: Canvas, progressBounds: RectF) {}
    override fun startIndeterminateAnimation() {}
    override fun stopIndeterminateAnimation() {}
    override fun setProgressPercent(progressPercent: Float) {}
    override fun setAnimationEnabled(enabled: Boolean) {}
    override fun setAnimationDuration(millis: Long) {}
    override fun setListener(listener: ProgressDrawerManager.ProgressDrawerListener) {}
}
