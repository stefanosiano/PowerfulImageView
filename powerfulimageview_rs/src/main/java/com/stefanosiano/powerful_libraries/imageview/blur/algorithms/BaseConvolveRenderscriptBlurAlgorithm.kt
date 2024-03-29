package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap
import androidx.renderscript.Allocation
import androidx.renderscript.RenderScript
import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions
import com.stefanosiano.powerful_libraries.imageview.extensions.tryOrPrint
import java.lang.ref.WeakReference

/**
 * Class that performs the gaussian blur with 3x3 coefficient matrix using renderscript.
 * Changing radius will repeat the process radius times.
 */
internal abstract class BaseConvolveRenderscriptBlurAlgorithm : BlurAlgorithm {

    private var renderscript: WeakReference<RenderScript?>? = null

    abstract fun runScript(radius: Int, rs: RenderScript, original: Bitmap): Allocation

    override fun setRenderscript(renderscript: RenderScript?): BlurAlgorithm {
        this.renderscript = WeakReference(renderscript)
        return this
    }

    @Throws(RenderscriptException::class)
    override fun blur(original: Bitmap, radius: Int, options: BlurOptions): Bitmap {
        if (radius == 0) {
            return original
        }

        val rs = renderscript?.get() ?: throw RenderscriptException("Renderscript is null!")

        val output: Allocation = tryOrPrint { runScript(radius, rs, original) }
            ?: throw RenderscriptException("Renderscript error while blurring!")

        return if (!options.isStaticBlur) {
            val bitmap = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
            output.copyTo(bitmap)
            output.destroy()
            bitmap
        } else if (original.isMutable) {
            output.copyTo(original)
            output.destroy()
            original
        } else {
            val bitmap = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
            original.recycle()
            output.copyTo(bitmap)
            output.destroy()
            bitmap
        }
    }
}
