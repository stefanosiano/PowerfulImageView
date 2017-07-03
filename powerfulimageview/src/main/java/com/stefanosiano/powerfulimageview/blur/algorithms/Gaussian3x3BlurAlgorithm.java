package com.stefanosiano.powerfulimageview.blur.algorithms;

/**
 * Class that performs the gaussian blur with 3x3 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */

final class Gaussian3x3BlurAlgorithm extends GaussianBaseBlurAlgorithm {

    @Override
    protected float[] getFilter(){
        return new float[] {0.1968f, 0.6064f, 0.1968f};
    }
}