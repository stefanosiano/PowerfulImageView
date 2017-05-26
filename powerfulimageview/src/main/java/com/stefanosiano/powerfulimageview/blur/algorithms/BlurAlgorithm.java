package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;

/**
 * Created by stefano on 26/05/17.
 */

public interface BlurAlgorithm {

    void setup(BlurOptions options);
    Bitmap blur(Bitmap original);
}
