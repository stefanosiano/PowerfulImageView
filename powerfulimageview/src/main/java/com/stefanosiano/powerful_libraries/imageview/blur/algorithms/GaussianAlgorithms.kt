package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap
import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions

/**
 * Class that performs the gaussian blur with 3x3 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */
internal class Gaussian3x3BlurAlgorithm : BaseConvolveBlurAlgorithm() {
    @Suppress("MagicNumber")
    override fun getFilter() = floatArrayOf(0.1968f, 0.6064f, 0.1968f)
}

/**
 * Class that performs the gaussian blur with 5x5 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */
internal class Gaussian5x5BlurAlgorithm : BaseConvolveBlurAlgorithm() {
    @Suppress("MagicNumber")
    override fun getFilter() = floatArrayOf(0.0545f, 0.2442f, 0.4026f, 0.2442f, 0.0545f)
}

/**
 * Class that performs the gaussian blur with any kind of radius.
 * Increasing radius will change the coefficients used and increase the radius of the blur,
 * resulting in the image more blurry, but slower.
 */

internal class GaussianBlurAlgorithm : BaseConvolveBlurAlgorithm() {

    private var radius: Int = 0

    override fun getFilter(): FloatArray {
        val filter = FloatArray(radius * 2 + 1)

        val sigma = (radius * 2 + 2) / 6.toFloat()
        val coeff = 1 / Math.sqrt(2.0 * Math.PI * sigma.toDouble() * sigma.toDouble())
        val exponent = -1 / (2f * sigma * sigma).toDouble()

        var sum = 0f
        for (i in filter.indices) {
            val x = (i - radius).toDouble()
            val value = (coeff * Math.exp(exponent * x * x)).toFloat()

            filter[i] = value
            sum += value
        }

        if (sum != 0f) sum = 1 / sum

        return filter.map { it * sum }.toFloatArray()
    }

    init {
        radius = 0
    }

    @Throws(RenderscriptException::class)
    override fun blur(original: Bitmap, radius: Int, options: BlurOptions): Bitmap? {
        if (radius == 0)
            return original
        this.radius = radius
        return super.blur(original, 1, options)
    }
}
