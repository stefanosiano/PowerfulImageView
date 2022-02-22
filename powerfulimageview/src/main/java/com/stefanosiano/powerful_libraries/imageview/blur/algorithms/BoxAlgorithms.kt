package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap






/**
 * Class that performs the box blur with 3x3 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */
internal class Box3x3BlurAlgorithm : BaseConvolveBlurAlgorithm() {
    override fun getFilter() = floatArrayOf(1 / 3f, 1 / 3f, 1 / 3f)
}

/**
 * Class that performs the box blur with 5x5 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */
internal class Box5x5BlurAlgorithm : BaseConvolveBlurAlgorithm() {
    override fun getFilter() = floatArrayOf(0.2f, 0.2f, 0.2f, 0.2f, 0.2f)
}


