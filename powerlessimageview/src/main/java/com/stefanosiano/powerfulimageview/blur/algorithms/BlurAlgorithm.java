package com.stefanosiano.powerfullibraries.imageview.blur.algorithms;

import android.graphics.Bitmap;
import android.renderscript.RenderScript;

import com.stefanosiano.powerfullibraries.imageview.blur.BlurOptions;

/**
 * Algorithm to blur the image
 */

interface BlurAlgorithm {

    /** Sets the renderscript context to this algorithm. Pass it before blurring! */
    void setRenderscript(RenderScript renderscript);

    /**
     * Blurs the image
     *
     * @param original Bitmap to blur
     * @param radius Radius of the algorithm
     * @param options Options of the blurring
     * @return The blurred bitmap
     * @throws RenderscriptException If renderscript is used and something goes wrong with it
     */
    Bitmap blur(Bitmap original, int radius, BlurOptions options) throws RenderscriptException;
}
