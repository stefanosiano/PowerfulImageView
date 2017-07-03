package com.stefanosiano.powerfulimageview.blur.algorithms;

/**
 * Class that performs the gaussian blur with 5x5 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */

final class Gaussian5x5BlurAlgorithm extends GaussianBaseBlurAlgorithm {

    @Override
    protected float[] getFilter(){
        return new float[] {0.0545f, 0.2442f, 0.4026f, 0.2442f, 0.0545f};
    }
}