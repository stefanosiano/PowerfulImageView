package com.stefanosiano.powerful_libraries.imageview.blur.algorithms


import android.graphics.Bitmap

import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions

import java.lang.ref.WeakReference

import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import com.stefanosiano.powerfullibraries.imageview.ScriptC_stackblur

/**
 * by kikoso
 * from https://github.com/kikoso/android-stackblur/blob/master/StackBlur/src/blur.rs
 */
internal class StackRenderscriptBlurAlgorithm: BlurAlgorithm {

    private var renderscript: WeakReference<RenderScript?>? = null

    override fun setRenderscript(renderscript: RenderScript?) { this.renderscript = WeakReference(renderscript) }


    @Throws(RenderscriptException::class)
    override fun blur(original: Bitmap, radius: Int, options: BlurOptions): Bitmap {

        val rs = renderscript?.get() ?: throw RenderscriptException("Renderscript is null!")

        val width = original.width
        val height = original.height

        val blurScript = ScriptC_stackblur(rs)
        val inAllocation = Allocation.createFromBitmap(rs, original)

        blurScript._gIn = inAllocation
        blurScript._width = width.toLong()
        blurScript._height = height.toLong()
        blurScript._radius = radius.toLong()

        var row_indices = IntArray(height) { i -> i }

        val rows = Allocation.createSized(rs, Element.U32(rs), height, Allocation.USAGE_SCRIPT)
        rows.copyFrom(row_indices)

        row_indices = IntArray(width) { i -> i }

        val columns = Allocation.createSized(rs, Element.U32(rs), width, Allocation.USAGE_SCRIPT)
        columns.copyFrom(row_indices)

        blurScript.forEach_blur_h(rows)
        blurScript.forEach_blur_v(columns)


        if (!options.isStaticBlur) {
            val bitmap = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
            inAllocation.copyTo(bitmap)
            inAllocation.destroy()
            rows.destroy()
            columns.destroy()
            return bitmap
        } else {
            return if (original.isMutable) {
                inAllocation.copyTo(original)
                inAllocation.destroy()
                rows.destroy()
                columns.destroy()
                original
            } else {
                val bitmap = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
                original.recycle()
                inAllocation.copyTo(bitmap)
                inAllocation.destroy()
                rows.destroy()
                columns.destroy()
                bitmap
            }
        }


    }
}