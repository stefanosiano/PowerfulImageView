package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap
import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions

/**
 * Algorithm to blur the image
 */

internal interface BlurAlgorithm {

    /** Sets the renderscript context to this algorithm. Pass it before blurring!  */
    fun setRenderscript(renderscript: Any?): BlurAlgorithm { return this }

    /**
     * Blurs the image
     *
     * @param original Bitmap to blur
     * @param radius Radius of the algorithm
     * @param options Options of the blurring
     * @return The blurred bitmap
     * @throws RenderscriptException If renderscript is used and something goes wrong with it
     */
    @Throws(RenderscriptException::class)
    fun blur(original: Bitmap, radius: Int, options: BlurOptions): Bitmap?
}

/** Dummy algorithm that doesn't do anything */
internal class DummyBlurAlgorithm : BlurAlgorithm {
    override fun blur(original: Bitmap, radius: Int, options: BlurOptions) = original
}

internal class RenderscriptException(message: String) : Exception(message)
