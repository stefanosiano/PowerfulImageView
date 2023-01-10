package com.stefanosiano.powerful_libraries.imageviewsample

import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stefanosiano.powerful_libraries.imageview.PowerfulImageView
import com.stefanosiano.powerful_libraries.imageview.progress.PivProgressMode
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ProgressUiTest : BaseUiTest() {
    private lateinit var piv: PowerfulImageView
    private lateinit var normalBitmap: Bitmap
    private lateinit var progressActivityScenario: ActivityScenario<ProgressActivity>

    @BeforeTest
    fun setup() {
        progressActivityScenario = launchActivity<ProgressActivity>()
        progressActivityScenario.onActivity {
            piv = it.binding.progressImage
            normalBitmap = piv.createBitmap()
        }
    }

    @AfterTest
    fun close() {
        progressActivityScenario.moveToState(Lifecycle.State.DESTROYED)
        Thread.sleep(100)
    }

    @Test
    fun changeProgressModesCreateDifferentBitmaps() {
        runner.runOnMainSync {
            piv.setProgressIndeterminate(false)
        }
        val circular = changeProgressMode(PivProgressMode.CIRCULAR)
        val horizontal = changeProgressMode(PivProgressMode.HORIZONTAL)

        runner.runOnMainSync {
            piv.setProgressIndeterminate(true)
        }
        val circularIndeterminate = changeProgressMode(PivProgressMode.CIRCULAR)
        val horizontalIndeterminate = changeProgressMode(PivProgressMode.HORIZONTAL)

        val none = changeProgressMode(PivProgressMode.NONE)

        assertTrue(none.contentEquals(normalBitmap))
        assertBitmapsDifferent(normalBitmap, circular, horizontal, circularIndeterminate, horizontalIndeterminate)
    }

    private fun runAndGetBitmap(f: (piv: PowerfulImageView) -> Unit): Bitmap {
        runner.runOnMainSync { f(piv) }
        Thread.sleep(10)
        return piv.createBitmap()
    }

    private fun changeProgressMode(mode: PivProgressMode): Bitmap = runAndGetBitmap {
        it.setProgressMode(mode)
    }
}