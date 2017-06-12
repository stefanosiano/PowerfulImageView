package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;

/**
 * Created by stefano on 26/05/17.
 */

public interface BlurAlgorithm {
    public Bitmap blur(Bitmap original, int radius, BlurOptions options);
}
