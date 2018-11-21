package com.stefanosiano.powerfulimageview.blur.algorithms

import android.graphics.Bitmap
import androidx.renderscript.*
import com.stefanosiano.powerfulimageview.blur.BlurOptions
import java.lang.ref.WeakReference

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


/**
 * Class that performs the box blur with 3x3 coefficient matrix using renderscript.
 * Changing radius will repeat the process radius times.
 */

internal class Box3x3RenderscriptBlurAlgorithm : BaseConvolveRenderscriptBlurAlgorithm() {

    private val coefficientMatrix = floatArrayOf(1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f)

    override fun runScript(radius: Int, rs: RenderScript, original: Bitmap): Allocation {
        var input = Allocation.createFromBitmap(rs, original)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
        script.setCoefficients(coefficientMatrix)
        for (i in 0 until radius) {
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

    private val coefficientMatrix = floatArrayOf(0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f, 0.04f)

    override fun runScript(radius: Int, rs: RenderScript, original: Bitmap): Allocation {
        var input = Allocation.createFromBitmap(rs, original)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicConvolve5x5.create(rs, Element.U8_4(rs))
        script.setCoefficients(coefficientMatrix)
        for (i in 0 until radius) {
            script.setInput(input)
            script.forEach(output)
            if (input != output) input.destroy()
            input = output
        }
        return output
    }
}