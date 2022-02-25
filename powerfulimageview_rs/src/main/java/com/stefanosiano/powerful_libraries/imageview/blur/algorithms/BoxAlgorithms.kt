package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicConvolve3x3
import androidx.renderscript.ScriptIntrinsicConvolve5x5

/**
 * Class that performs the box blur with 3x3 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */
internal class Box3x3BlurAlgorithm : BaseConvolveBlurAlgorithm() {
    @Suppress("MagicNumber")
    override fun getFilter() = floatArrayOf(1 / 3f, 1 / 3f, 1 / 3f)
}

/**
 * Class that performs the box blur with 5x5 coefficient matrix.
 * Changing radius will repeat the process radius times.
 */
internal class Box5x5BlurAlgorithm : BaseConvolveBlurAlgorithm() {
    @Suppress("MagicNumber")
    override fun getFilter() = floatArrayOf(0.2f, 0.2f, 0.2f, 0.2f, 0.2f)
}

// RENDERSCRIPT ALGORITHMS

/**
 * Class that performs the box blur with 3x3 coefficient matrix using renderscript.
 * Changing radius will repeat the process radius times.
 */

internal class Box3x3RenderscriptBlurAlgorithm : BaseConvolveRenderscriptBlurAlgorithm() {

    @Suppress("MagicNumber")
    private val coefficientMatrix = floatArrayOf(1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f)

    override fun runScript(radius: Int, rs: RenderScript, original: Bitmap): Allocation {
        var input = Allocation.createFromBitmap(rs, original)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
        script.setCoefficients(coefficientMatrix)
        (0 until radius).forEach { _ ->
            script.setInput(input)
            script.forEach(output)
            if (input != output) input.destroy()
            input = output
        }
        return output
    }
}

/**
 * Class that performs the box blur with 5x5 coefficient matrix using renderscript.
 * Changing radius will repeat the process radius times.
 */
internal class Box5x5RenderscriptBlurAlgorithm : BaseConvolveRenderscriptBlurAlgorithm() {

    @Suppress("MagicNumber", "MaxLineLength")
    private val coefficientMatrix =
        floatArrayOf(0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f)

    override fun runScript(radius: Int, rs: RenderScript, original: Bitmap): Allocation {
        var input = Allocation.createFromBitmap(rs, original)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicConvolve5x5.create(rs, Element.U8_4(rs))
        script.setCoefficients(coefficientMatrix)
        (0 until radius).forEach { _ ->
            script.setInput(input)
            script.forEach(output)
            if (input != output) input.destroy()
            input = output
        }
        return output
    }
}
