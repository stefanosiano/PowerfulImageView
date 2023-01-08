package com.stefanosiano.powerful_libraries.imageviewsample

import android.graphics.Bitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.BeforeTest

@RunWith(AndroidJUnit4::class)
class MainUiTest : BaseUiTest() {

//    private lateinit var normalBitmap: Bitmap
//    private lateinit var piv: PowerfulImageView
    private lateinit var mainActivityScenario: ActivityScenario<MainActivity>

    @BeforeTest
    fun setup() {
        mainActivityScenario = launchActivity<MainActivity>()
        mainActivityScenario.onActivity {
//            piv = it.binding.blurImage
//            normalBitmap = piv.createBitmap()
        }
    }
}