package com.stefanosiano.powerfulimageview.blur.algorithms

import android.graphics.Bitmap
import androidx.renderscript.*
import com.stefanosiano.powerfulimageview.blur.BlurOptions
import java.lang.ref.WeakReference


/**
 * Class that performs the gaussian blur with 3x3 coefficient matrix using renderscript.
 * Changing radius will repeat the process radius times.
 */

internal abstract class BaseConvolveRenderscriptBlurAlgorithm : BlurAlgorithm {

    abstract fun runScript(radius: Int, rs: RenderScript, original: Bitmap): Allocation

    private var renderscript: WeakReference<RenderScript?>? = null

    override fun setRenderscript(renderscript: RenderScript?) { this.renderscript = WeakReference(renderscript) }

    @Throws(RenderscriptException::class)
    override fun blur(original: Bitmap, radius: Int, options: BlurOptions): Bitmap {

        val rs = renderscript?.get() ?: throw RenderscriptException("Renderscript is null!")

        val output: Allocation
        try { output = runScript(radius, rs, original) }
        catch (e: Exception) {
            e.printStackTrace()
            throw RenderscriptException("Renderscript error while blurring! \n" + e.localizedMessage)
        }



        if (!options.isStaticBlur) {
            val bitmap = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
            output.copyTo(bitmap)
            output.destroy()
            return bitmap
        } else {
            return if (original.isMutable) {
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
}