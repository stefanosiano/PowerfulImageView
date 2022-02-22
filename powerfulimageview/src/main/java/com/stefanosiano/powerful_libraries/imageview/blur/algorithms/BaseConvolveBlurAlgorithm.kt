package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap
import android.graphics.Color
import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions
import java.util.ArrayList
import java.util.concurrent.Callable


/**
 * Base class that performs a convolution blur.
 * Should be overridden to provide the coefficients to use, so that it can perform the box or gaussian blu.
 * Changing radius will repeat the process radius times.
 */

internal abstract class BaseConvolveBlurAlgorithm : BlurAlgorithm {

    private var w: Int = 0
    private var h: Int = 0


    /** Returns a 1D gaussian filter to perform the 2D blur  */
    internal abstract fun getFilter(): FloatArray

    @Throws(RenderscriptException::class)
    override fun blur(original: Bitmap, radius: Int, options: BlurOptions): Bitmap? {

        w = original.width
        h = original.height
        val pix = IntArray(w * h)
        original.getPixels(pix, 0, w, 0, 0, w, h)

        val cores = options.numThreads.coerceAtLeast(Runtime.getRuntime().availableProcessors())

        val horizontal = ArrayList<BlurTask>(cores)
        val vertical = ArrayList<BlurTask>(cores)

        for (i in 0 until cores) {
            horizontal.add(BlurTask(pix, w, h, radius, cores, i, 1))
            vertical.add(BlurTask(pix, w, h, radius, cores, i, 2))
        }

        try {
            SharedBlurManager.executorService.invokeAll(horizontal)
            SharedBlurManager.executorService.invokeAll(vertical)
        } catch (e: InterruptedException) {
            return null
        }


        return if (!options.isStaticBlur) {
            Bitmap.createBitmap(pix, 0, w, w, h, Bitmap.Config.ARGB_8888)
        } else {
            if (original.isMutable) {
                original.setPixels(pix, 0, w, 0, 0, w, h)
                original
            } else {
                original.recycle()
                Bitmap.createBitmap(pix, 0, w, w, h, Bitmap.Config.ARGB_8888)
            }
        }
    }


    /**
     * Returns a pixel from the provided vector.
     * Returns a pixel on the edge if the index passed is not valid.
     */
    // sample a repeated image. Returns a valid result for any x and y.
    // w is the image width, h the image height and pix the image itself.
    private fun getPixel(x2: Int, y2: Int, pix: IntArray): Int {
        var x = x2
        var y = y2
        //if the pixel doen't exists i return a transparent pixel
        if (x < 0) x = 0
        if (x >= w) x = w - 1
        if (y < 0) y = 0
        if (y >= h) y = h - 1

        return pix[y * w + x]
    }


    private fun apply(srcPix: IntArray, w: Int, h: Int, radius: Int, cores: Int, core: Int, step: Int) {

        //getting gaussian filter
        val gaussianFilter = getFilter()
        val filterLength = gaussianFilter.size

        // Auxiliary array of current pixels to blur, moving it through the image, so i don't use additional memory
        val tmpPix = IntArray(filterLength)


        // horizontal filtering
        if (step == 1) {

            //repeating radius times
            (0 until radius).forEach { _ ->

                val minY = core * h / cores
                val maxY = (core + 1) * h / cores
                val minIndex = minY * w

                for (y in minY until maxY) {

                    val row = y * w
                    var position = 0

                    for (x in 0 until w) {

                        val sum: Int
                        var r = 0
                        var g = 0
                        var b = 0
                        var a = 0

                        //applying blur to the filterLength close pixels, weighting through the corresponding filter
                        for (i in 0 until filterLength) {
                            val pixel = getPixel(x + i - filterLength / 2, y, srcPix)

                            r += (Color.red(pixel) * gaussianFilter[i]).toInt()
                            g += (Color.green(pixel) * gaussianFilter[i]).toInt()
                            b += (Color.blue(pixel) * gaussianFilter[i]).toInt()
                            a += (Color.alpha(pixel) * gaussianFilter[i]).toInt()
                        }

                        sum = Color.argb(a, r, g, b)

                        // store the pixel
                        position = row + x
                        if (position >= minIndex + filterLength)
                            srcPix[position - filterLength] = tmpPix[position % filterLength]

                        tmpPix[position % filterLength] = sum
                    }

                    //store remaining pixels to src vector
                    position++
                    for (i in 0 until filterLength)
                        srcPix[position - filterLength + i] = tmpPix[(position + i) % filterLength]

                }
            }
        }


        // vertical filtering
        if (step == 2) {

            //repeating radius times
            (0 until radius).forEach { _ ->

                val minX = core * w / cores
                val maxX = (core + 1) * w / cores

                for (x in minX until maxX) {

                    var row = x
                    var position = 0

                    for (y in 0 until h) {

                        val sum: Int
                        var r = 0
                        var g = 0
                        var b = 0
                        var a = 0

                        //applying blur to the filterLength close pixels, weighting through the corresponding filter
                        for (i in 0 until filterLength) {
                            val pixel = getPixel(x, y + i - filterLength / 2, srcPix)

                            r += (Color.red(pixel) * gaussianFilter[i]).toInt()
                            g += (Color.green(pixel) * gaussianFilter[i]).toInt()
                            b += (Color.blue(pixel) * gaussianFilter[i]).toInt()
                            a += (Color.alpha(pixel) * gaussianFilter[i]).toInt()
                        }

                        sum = Color.argb(a, r, g, b)

                        if (position >= filterLength)
                            srcPix[row - filterLength * w] = tmpPix[position % filterLength]

                        tmpPix[position % filterLength] = sum

                        position++
                        row += w
                    }

                    //store remaining pixels to src vector
                    for (i in 0 until filterLength)
                        srcPix[row - (filterLength - i) * w] = tmpPix[(position + i) % filterLength]

                }
            }
        }

    }


    private inner class BlurTask constructor(
        private val src: IntArray,
        private val w: Int,
        private val h: Int,
        private val radius: Int,
        private val totalCores: Int,
        private val coreIndex: Int,
        private val round: Int) : Callable<Void> {

        @Throws(Exception::class)
        override fun call(): Void? {
            apply(src, w, h, radius, totalCores, coreIndex, round)
            return null
        }

    }
}
