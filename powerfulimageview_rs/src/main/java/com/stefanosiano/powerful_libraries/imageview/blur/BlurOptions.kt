package com.stefanosiano.powerful_libraries.imageview.blur

import java.lang.ref.WeakReference

/** Class that helps managing the options that will be used to blur the image. */

class BlurOptions() {

    /**
     * Rate to downSample the image width and height, so that bitmap is no more than the view size divided by this rate.
     * If a value less than 1 is passed, downSampling rate 1 is used.
     */
    var downSamplingRate: Float = 0f
        set(value) {
            field = value.coerceAtLeast(1f)
            if (isInitialized) {
                this.listener?.get()?.onDownSamplingRateChanged()
            }
        }

    /**
     * Whether the original bitmap should be blurred only once. If true, optimizations occur.
     * If false, trying to blur a second time won't have effect.
     */
    var isStaticBlur: Boolean = false
        set(value) {
            field = value
            if (isInitialized) {
                listener?.get()?.onStaticBlurChanged()
            }
        }

    /**
     * Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs.
     * Used only if a renderscript mode is selected.
     */
    var useRsFallback: Boolean = false

    /** Number of threads to use to blur the image. If 0 or negative, available cores number will be used. */
    var numThreads: Int = 0

    /** Listener that will update the blur manager on changes, with a weak reference to be sure to not leak memory. */
    private var listener: WeakReference<BlurOptionsListener>? = null

    /** Flag to check if the object's constructor was called. */
    private var isInitialized = false

    /**
     * Creates the object that will manage the blur options.
     *
     * [downSamplingRate] Rate to downSample the image width and height, based on the view size. Cannot be lower than 1.
     * [isStaticBlur] Whether the original bitmap should be kept in memory.
     *  If false, trying to blur a second time won't have effect.
     * [useRsFallback] If the image should be blurred with a java equivalent of the renderscript algorithm on error.
     */
    constructor(downSamplingRate: Float, isStaticBlur: Boolean, useRsFallback: Boolean, numThreads: Int) : this() {
        this.downSamplingRate = downSamplingRate.coerceAtLeast(1f)
        this.isStaticBlur = isStaticBlur
        this.useRsFallback = useRsFallback
        this.numThreads = numThreads
        this.isInitialized = true
    }

    internal fun setListener(listener: BlurOptionsListener) { this.listener = WeakReference(listener) }

    internal interface BlurOptionsListener {
        fun onStaticBlurChanged()
        fun onDownSamplingRateChanged()
    }
}
