package com.stefanosiano.powerful_libraries.imageviewsample

import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import androidx.renderscript.RenderScript
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stefanosiano.powerful_libraries.imageview.PowerfulImageView
import com.stefanosiano.powerful_libraries.imageview.blur.PivBlurMode
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class BlurUiTest : BaseUiTest() {

    private val MAX_RADIUS_TO_TEST = 50
    private lateinit var normalBitmap: Bitmap
    private lateinit var piv: PowerfulImageView
    private lateinit var blurActivityScenario: ActivityScenario<BlurActivity>
    private var disabledRenderscript = false

    @BeforeTest
    fun setup() {
        val d = context.resources.getDrawable(R.drawable.sf1)
        disabledRenderscript = false
        normalBitmap = d.createBitmap()

        blurActivityScenario = launchActivity<BlurActivity>()
        blurActivityScenario.onActivity {
            piv = it.binding.blurImage
        }
    }

    @AfterTest
    fun close() {
        blurActivityScenario.moveToState(Lifecycle.State.DESTROYED)
        Thread.sleep(100)
    }

    @Test
    fun changeBlurModesAndRadiusBlurDifferently() {
        val orig = piv.getBlurOriginalBitmap()
        assertNull(orig)
        val disabled = resetAndBlurSingleBitmap(piv, PivBlurMode.DISABLED, 1)
        assertNull(disabled)
        val box3x3Rs = resetAndBlurBitmaps(piv, PivBlurMode.BOX3X3_RS)
        val box5x5Rs = resetAndBlurBitmaps(piv, PivBlurMode.BOX5X5_RS)
        val gaussianRs = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN_RS)
        val gaussian3x3Rs = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN3X3_RS)
        val gaussian5x5Rs = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN5X5_RS)
        val box3x3 = resetAndBlurBitmaps(piv, PivBlurMode.BOX3X3)
        val box5x5 = resetAndBlurBitmaps(piv, PivBlurMode.BOX5X5)
        val gaussian = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN)
        val gaussian3x3 = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN3X3)
        val gaussian5x5 = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN5X5)
        val stack = resetAndBlurBitmaps(piv, PivBlurMode.STACK)

        // Different radius for the same blur mode should give different bitmaps
        assertBitmapsDifferent(orig, *box3x3Rs)
        assertBitmapsDifferent(orig, *box5x5Rs)
        assertBitmapsDifferent(orig, *gaussianRs)
        assertBitmapsDifferent(orig, *gaussian3x3Rs)
        assertBitmapsDifferent(orig, *gaussian5x5Rs)
        assertBitmapsDifferent(orig, *box3x3)
        assertBitmapsDifferent(orig, *box5x5)
        assertBitmapsDifferent(orig, *gaussian)
        assertBitmapsDifferent(orig, *gaussian3x3)
        assertBitmapsDifferent(orig, *gaussian5x5)
        assertBitmapsDifferent(orig, *stack)

        // Same radius for different blur modes should give different bitmaps
        for (i in 0 until MAX_RADIUS_TO_TEST) {
            assertBitmapsDifferent(orig, box3x3[i], box5x5[i], gaussian[i], gaussian3x3[i], gaussian5x5[i], stack[i])
            assertBitmapsDifferent(orig, box3x3Rs[i], box5x5Rs[i], gaussianRs[i], gaussian3x3Rs[i], gaussian5x5Rs[i])
        }
    }

    @Test
    fun disabledBlurModesReturnsNonBlurredImageAfterFirstBlur() {
        piv.getBlurOptions().downSamplingRate = 1F
        // First time (before any blur happened) the getBlurOriginalBitmap returns null
        val orig = piv.getBlurOriginalBitmap()
        assertNull(orig)
        val disabled = resetAndBlurSingleBitmap(piv, PivBlurMode.DISABLED, 1)
        assertNull(disabled)
        val box3x3 = resetAndBlurSingleBitmap(piv, PivBlurMode.BOX3X3, 1)
        assertFalse(box3x3.contentEquals(normalBitmap))

        // Any other time after the first blur happened, the getBlurOriginalBitmap returns the original bitmap
        val disabled2 = resetAndBlurSingleBitmap(piv, PivBlurMode.DISABLED, 1)
        assertTrue(disabled2.contentEquals(normalBitmap))
    }

    @Test
    fun radius0ReturnsNonBlurredImageAfterFirstBlur() {
        piv.getBlurOptions().downSamplingRate = 1F
        // First time (before any blur happened) the getBlurOriginalBitmap returns null
        assertNull(piv.getBlurOriginalBitmap())
        val radius0Bitmap = resetAndBlurSingleBitmap(piv, PivBlurMode.BOX3X3, 0)
        assertNull(radius0Bitmap)
        val box3x3 = resetAndBlurSingleBitmap(piv, PivBlurMode.BOX3X3, 1)
        assertFalse(box3x3.contentEquals(normalBitmap))

        // Any other time after the first blur happened, the getBlurOriginalBitmap returns the original bitmap
        val radius0Bitmap2 = resetAndBlurSingleBitmap(piv, PivBlurMode.BOX3X3, 0)
        assertTrue(radius0Bitmap2.contentEquals(normalBitmap))
    }

    @Test
    fun downsamplingNotWorkingWhenBlurIsDisabled() {
        runner.runOnMainSync {
            piv.setImageDrawable(null)
            piv.getBlurOptions().downSamplingRate = 2F
            piv.setImageBitmap(normalBitmap)
        }
        // Without blurring the downsampling is ignored
        assertNull(piv.getBlurOriginalBitmap())
        assertNull(piv.getBlurBlurredBitmap())
        assertTrue(normalBitmap.contentEquals(piv.drawable.createBitmap()))
    }

    @Test
    fun downsamplingWorksOnlyWhenBlurring() {
        // First we set downsampling, then we set the bitmap
        runner.runOnMainSync {
            piv.setImageDrawable(null)
            piv.getBlurOptions().downSamplingRate = 2F
            piv.setBlurMode(PivBlurMode.BOX3X3, 1)
            piv.setImageBitmap(normalBitmap)
        }
        // The sleep lets the ui draw and get the real width and height: the downsampling is based on the view size
        Thread.sleep(100)
        // We get the max view size and multiply for 4 (the bytes to store a pixel)
        val pivPixelsBytes = piv.measuredWidth * piv.measuredHeight * 4
        // Downsampling of 2 means "width / 2 * height / 2" = pivPixelsBytes / 4
        assertEquals(pivPixelsBytes / 4, piv.getBlurBlurredBitmap()!!.byteCount)

        // First we set the bitmap, then we set the downsampling
        runner.runOnMainSync {
            piv.getBlurOptions().downSamplingRate = 4F
        }
        Thread.sleep(100)
        val pivPixelsBytes2 = piv.measuredWidth * piv.measuredHeight * 4
        // Downsampling of 4 means "width / 4 * height / 4" = pivPixelsBytes / 16
        assertEquals(pivPixelsBytes2 / 16, piv.getBlurBlurredBitmap()!!.byteCount)

        // Reducing downsampling is not currently supported
        runner.runOnMainSync {
            piv.getBlurOptions().downSamplingRate = 2F
        }
        Thread.sleep(100)
        assertEquals(pivPixelsBytes2 / 16, piv.getBlurBlurredBitmap()!!.byteCount)
    }

    @Test
    fun changeBlurRadiusMode() {
        // First blur
        runner.runOnMainSync {
            piv.setBlurMode(PivBlurMode.BOX3X3, 1)
        }
        val box3x3R1 = piv.getBlurBlurredBitmap()?.let { it.copy(it.config, it.isMutable) }
        assertNotNull(box3x3R1)

        // Blur with another radius and back to old radius and check they are the same
        runner.runOnMainSync {
            piv.setBlurMode(PivBlurMode.BOX3X3, 5)
        }
        val box3x3R5 = piv.getBlurBlurredBitmap()?.let { it.copy(it.config, it.isMutable) }
        assertFalse(box3x3R1.contentEquals(box3x3R5))
        runner.runOnMainSync {
            piv.setBlurMode(PivBlurMode.BOX3X3, 1)
        }
        var box3x3R1New = piv.getBlurBlurredBitmap()?.let { it.copy(it.config, it.isMutable) }
        assertTrue(box3x3R1.contentEquals(box3x3R1New))

        // Blur with another mode and back to old mode and check they are the same
        runner.runOnMainSync {
            piv.setBlurMode(PivBlurMode.GAUSSIAN5X5, 3)
        }
        val gaussian5x5R3 = piv.getBlurBlurredBitmap()?.let { it.copy(it.config, it.isMutable) }
        assertFalse(box3x3R1.contentEquals(gaussian5x5R3))
        runner.runOnMainSync {
            piv.setBlurMode(PivBlurMode.BOX3X3, 1)
        }
        box3x3R1New = piv.getBlurBlurredBitmap()?.let { it.copy(it.config, it.isMutable) }
        assertTrue(box3x3R1.contentEquals(box3x3R1New))
    }

    @Test
    fun staticBlurAllowsBlurringOnlyOnce() {
        // Enable static blur and blur the image
        runner.runOnMainSync {
            piv.setPivBlurStatic(true)
            piv.setBlurMode(PivBlurMode.BOX3X3, 1)
        }
        // The piv original bitmap is set to be the same as the blurred bitmap, both different from the "notmalBitmap"
        val box3x3R1 = piv.getBlurBlurredBitmap()?.let { it.copy(it.config, it.isMutable) }
        assertNotNull(box3x3R1)
        assertFalse(box3x3R1.contentEquals(normalBitmap))
        assertTrue(box3x3R1.contentEquals(piv.getBlurOriginalBitmap()))

        // Blur with another mode or radius is ignored when using static blur
        runner.runOnMainSync {
            piv.setBlurMode(PivBlurMode.BOX3X3, 5)
        }
        val box3x3R5 = piv.getBlurBlurredBitmap()?.let { it.copy(it.config, it.isMutable) }
        assertTrue(box3x3R1.contentEquals(box3x3R5))
    }

    @Test
    fun blurWithRenderscriptFallbacksMethods() {

        while (!disabledRenderscript) {
            val x = resetAndBlurBitmaps(piv, PivBlurMode.BOX3X3_RS)
            val y = resetAndBlurBitmaps(piv, PivBlurMode.BOX3X3)
            // Very ugly hack to cause an error in renderscript. Makes test flaky, but it's sadly needed.
            Thread {
                while (!disabledRenderscript) {
                    RenderScript.releaseAllContexts()
                }
            }.start()

            // We check the bitmap generated from the rs and java methods are the same.
            // If so, it means renderscript mode was disabled in library due to an error and it fell back to java blur.
            for (i in 0 until MAX_RADIUS_TO_TEST) {
                if(x[i].contentEquals(y[i])) {
                    disabledRenderscript = true
                    break
                }
            }
        }

        val box3x3Rs = resetAndBlurBitmaps(piv, PivBlurMode.BOX3X3_RS)
        val box5x5Rs = resetAndBlurBitmaps(piv, PivBlurMode.BOX5X5_RS)
        val gaussianRs = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN_RS)
        val gaussian3x3Rs = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN3X3_RS)
        val gaussian5x5Rs = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN5X5_RS)
        val box3x3 = resetAndBlurBitmaps(piv, PivBlurMode.BOX3X3)
        val box5x5 = resetAndBlurBitmaps(piv, PivBlurMode.BOX5X5)
        val gaussian = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN)
        val gaussian3x3 = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN3X3)
        val gaussian5x5 = resetAndBlurBitmaps(piv, PivBlurMode.GAUSSIAN5X5)

        // Same radius for renderscript and java blur modes should give same bitmaps, because since there was a
        // renderscript error, it fell back to corresponding java blur methods
        for (i in 0 until MAX_RADIUS_TO_TEST) {
            assertTrue(box3x3[i].contentEquals(box3x3Rs[i]))
            assertTrue(box5x5[i].contentEquals(box5x5Rs[i]))
            assertTrue(gaussian[i].contentEquals(gaussianRs[i]))
            assertTrue(gaussian3x3[i].contentEquals(gaussian3x3Rs[i]))
            assertTrue(gaussian5x5[i].contentEquals(gaussian5x5Rs[i]))
        }
    }

    private fun assertBitmapsDifferent(vararg bitmaps: Bitmap?) {
        for (i in bitmaps.indices) {
            for (j in bitmaps.indices) {
                if (i != j) {
                    assertFalse(bitmaps[i].contentEquals(bitmaps[j]))
                }
            }
        }
    }

    private fun resetAndBlurBitmaps(piv: PowerfulImageView, mode: PivBlurMode): Array<Bitmap?> {
        val bitmaps = (1..MAX_RADIUS_TO_TEST).map { radius ->
            resetAndBlurSingleBitmap(piv, mode, radius)
        }
        return bitmaps.toTypedArray()
    }

    private fun resetAndBlurSingleBitmap(piv: PowerfulImageView, mode: PivBlurMode, radius: Int): Bitmap? {
        runner.runOnMainSync {
            piv.setBlurMode(PivBlurMode.DISABLED, radius)
            piv.setImageBitmap(normalBitmap)
            piv.setBlurMode(mode, radius)
        }
        val blurred = piv.getBlurBlurredBitmap()
        return blurred?.copy(blurred.config, blurred.isMutable)
    }
}