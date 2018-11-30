package com.stefanosiano.powerfullibraries.imageview.blur.algorithms;

/**
 * Class that performs the box blur with 5x5 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */

final class Box5x5BlurAlgorithm extends ConvolveBaseBlurAlgorithm {

    @Override
    protected float[] getFilter(){
        return new float[] {0.2f, 0.2f, 0.2f, 0.2f, 0.2f};
    }
}