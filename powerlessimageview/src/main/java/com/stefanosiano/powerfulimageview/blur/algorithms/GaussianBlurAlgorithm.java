package com.stefanosiano.powerfullibraries.imageview.blur.algorithms;

import android.graphics.Bitmap;

import com.stefanosiano.powerfullibraries.imageview.blur.BlurOptions;

/**
 * Class that performs the gaussian blur with any kind of radius.
 * Increasing radius will change the coefficients used and increase the radius of the blur,
 * resulting in the image more blurry, but slower.
 */

final class GaussianBlurAlgorithm extends ConvolveBaseBlurAlgorithm {
    private int radius;

    GaussianBlurAlgorithm() {
        super();
        radius = 0;
    }

    @Override
    float[] getFilter() {
        float[] filter = new float[radius * 2 + 1];

        float sigma = (radius * 2 + 2) / (float) 6;
        double coeff = 1/Math.sqrt(2 * Math.PI * sigma * sigma);
        double exponent = -1/(double) (2 * sigma * sigma);


        float sum = 0;
        for(int i = 0; i < filter.length; i++){

            double x = i - radius;
            float value = (float) (coeff * Math.exp(exponent * x * x));

            filter[i] = value;
            sum += value;
        }

        if(sum != 0)
            sum = 1/sum;

        for(int i = 0; i < filter.length; i++){
            filter[i] = filter[i] * sum;
        }
        return filter;
        //return new float[] {0.0545f, 0.2442f, 0.4026f, 0.2442f, 0.0545f};
    }

    @Override
    public Bitmap blur(Bitmap original, int radius, BlurOptions options) throws RenderscriptException {
        this.radius = radius;

        return super.blur(original, 1, options);
    }
}
