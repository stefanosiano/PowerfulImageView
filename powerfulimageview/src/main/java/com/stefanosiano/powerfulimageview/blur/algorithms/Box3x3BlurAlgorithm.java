package com.stefanosiano.powerfulimageview.blur.algorithms;

/**
 * Class that performs the box blur with 3x3 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */

final class Box3x3BlurAlgorithm extends ConvolveBaseBlurAlgorithm {

    @Override
    protected float[] getFilter(){
        return new float[] {1/3f, 1/3f, 1/3f};
    }
}