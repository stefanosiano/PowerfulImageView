package com.stefanosiano.powerfullibraries.imageview.blur.algorithms;

import android.graphics.Bitmap;
import android.renderscript.RenderScript;

import com.stefanosiano.powerfullibraries.imageview.blur.BlurOptions;

/**
 * Dummy algorithm that doesn't do anything
 */

final class DummyBlurAlgorithm implements BlurAlgorithm {

    @Override
    public void setRenderscript(RenderScript renderscript) {

    }

    @Override
    public Bitmap blur(Bitmap original, int radius, BlurOptions options) {

        return original;
    }
}
