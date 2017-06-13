package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;
import android.support.v8.renderscript.RenderScript;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;

/**
 * Created by stefano on 26/05/17.
 */

public interface BlurAlgorithm {
    void setRenderscript(RenderScript renderscript);
    Bitmap blur(Bitmap original, int radius, BlurOptions options);
}
