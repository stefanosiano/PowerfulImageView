package com.stefanosiano.powerful_libraries.imageviewsample

import android.graphics.Bitmap
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stefanosiano.powerful_libraries.imageview.PowerfulImageView
import com.stefanosiano.powerful_libraries.imageview.blur.PivBlurMode
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class BlurUiTest : BaseUiTest() {

    @Test
    fun changeDrawable() {
        val blurActivityScenario = launchActivity<BlurActivity>()
        lateinit var piv: PowerfulImageView
            blurActivityScenario.onActivity {
            piv = it.binding.blurImage
        }
        assertNotNull(piv)
        val orig = piv.getBlurOriginalBitmap()
        val disabled = blurAndGet(piv, PivBlurMode.DISABLED, 1)
        assertEquals(orig, disabled)
        val box3x3 = blurAndGet(piv, PivBlurMode.BOX3X3, 1)
        assertNotEquals(orig, box3x3)
    }

    private fun blurAndGet(piv: PowerfulImageView, mode: PivBlurMode, radius: Int): Bitmap? {
        runner.runOnMainSync {
            piv.setBlurMode(mode, radius)
        }
        return piv.getBlurBlurredBitmap()
    }
}