package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap
import androidx.renderscript.*
import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions


/**
 * Class that performs the gaussian blur with 3x3 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */
internal class Gaussian3x3BlurAlgorithm : BaseConvolveBlurAlgorithm() {
    override fun getFilter() = floatArrayOf(0.1968f, 0.6064f, 0.1968f)
}

/**
 * Class that performs the gaussian blur with 5x5 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */
internal class Gaussian5x5BlurAlgorithm : BaseConvolveBlurAlgorithm() {
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
        this.radius = radius
        return super.blur(original, 1, options)
    }
}


//RENDERSCRIPT ALGORITHMS

/**
 * Class that performs the gaussian blur with 3x3 coefficient matrix using renderscript.
 * Changing radius will repeat the process radius times.
 */
internal class Gaussian3x3RenderscriptBlurAlgorithm : BaseConvolveRenderscriptBlurAlgorithm() {

    private val coefficientMatrix = floatArrayOf(0.0387f, 0.1194f, 0.0387f, 0.1194f, 0.3676f, 0.1194f, 0.0387f, 0.1194f, 0.0387f)

    override fun runScript(radius: Int, rs: RenderScript, original: Bitmap): Allocation {
        var input = Allocation.createFromBitmap(rs, original)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
        script.setCoefficients(coefficientMatrix)
        for (i in 0 until radius) {
            script.setInput(input)
            script.forEach(output)
            if (input !== output) input.destroy()
            input = output
        }
        return output
    }
}

/**
 * Class that performs the gaussian blur with 5x5 coefficient matrix using renderscript.
 * Changing radius will repeat the process radius times.
 */
internal class Gaussian5x5RenderscriptBlurAlgorithm : BaseConvolveRenderscriptBlurAlgorithm() {

    private val coefficientMatrix = floatArrayOf(0.0030f, 0.0133f, 0.0219f, 0.0133f, 0.0030f, 0.0133f, 0.0596f, 0.0983f, 0.0596f, 0.0133f, 0.0219f, 0.0983f, 0.1621f, 0.0983f, 0.0219f, 0.0133f, 0.0596f, 0.0983f, 0.0596f, 0.0133f, 0.0030f, 0.0133f, 0.0219f, 0.0133f, 0.0030f)

    override fun runScript(radius: Int, rs: RenderScript, original: Bitmap): Allocation {
        var input = Allocation.createFromBitmap(rs, original)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicConvolve5x5.create(rs, Element.U8_4(rs))
        script.setCoefficients(coefficientMatrix)
        for (i in 0 until radius) {
            script.setInput(input)
            script.forEach(output)
            if (input !== output)
                input.destroy()
            input = output
        }
        return output
    }

}


/**
 * Class that performs the gaussian blur with any kind of radius using renderscript.
 * Increasing radius will change the coefficients used and increase the radius of the blur,
 * resulting in the image more blurry, but slower.
 */
internal class GaussianRenderscriptBlurAlgorithm : BaseConvolveRenderscriptBlurAlgorithm() {

    override fun runScript(radius: Int, rs: RenderScript, original: Bitmap): Allocation {
        val input = Allocation.createFromBitmap(rs, original)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(radius.toFloat())
        script.setInput(input)
        script.forEach(output)
        return output
    }
}