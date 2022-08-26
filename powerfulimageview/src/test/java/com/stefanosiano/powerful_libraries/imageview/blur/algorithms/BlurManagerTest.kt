package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.nhaarman.mockitokotlin2.mock
import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions
import com.stefanosiano.powerful_libraries.imageview.blur.PivBlurMode
import com.stefanosiano.powerful_libraries.imageview.extensions.createBitmap
import io.mockk.every
import io.mockk.mockkStatic
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BlurManagerTest {

    private class Fixture {
        val view = mock<ImageView>()
        val blurOptions = BlurOptions()
        val draw1 = mock<ColorDrawable>()
        val draw2 = mock<ClipDrawable>()

        fun getBM() = BlurManager(view, blurOptions)
    }

    private val fixture = Fixture()
    private lateinit var bm: BlurManager

    @BeforeTest
    fun setupManager() {
        mockkStatic("com.stefanosiano.powerful_libraries.imageview.extensions.DrawableExtensionsKt")
        val bitmap1 = mock<Bitmap>()
        val bitmap2 = mock<Bitmap>()
        every { fixture.draw1.createBitmap(any(), any()) }.returns(bitmap1)
        every { fixture.draw2.createBitmap(any(), any()) }.returns(bitmap2)
        bm = fixture.getBM()
        bm.onSizeChanged(10, 10)
    }

    @Test
    fun changeDrawable() {
        mockkStatic("com.stefanosiano.powerful_libraries.imageview.extensions.DrawableExtensions")
        // Starts with null bitmaps
        assertNull(bm.getOriginalBitmap())
        // Changing drawable without a blur mode does nothing
        bm.changeDrawable(fixture.draw2)
        assertNull(bm.getOriginalBitmap())
        // Changing mode and radius only does nothing
        bm.changeMode(PivBlurMode.BOX3X3, 1)
        assertNull(bm.getOriginalBitmap())
        // Changing a drawable with a blur mode set changes the drawable
        bm.changeDrawable(fixture.draw2)
        assertNotNull(bm.getOriginalBitmap())
    }

    @Test
    fun changeMode() {
        // Starts with blur disabled
        assertEquals(PivBlurMode.DISABLED, bm.getBlurMode())
        bm.changeMode(PivBlurMode.BOX3X3, 1)
        assertEquals(PivBlurMode.BOX3X3, bm.getBlurMode())
        bm.changeMode(PivBlurMode.GAUSSIAN, 1)
        assertEquals(PivBlurMode.GAUSSIAN, bm.getBlurMode())
    }

    @Test
    fun changeRadius() {
        // Starts with radius 0
        assertEquals(0, bm.getRadius())
        bm.changeMode(PivBlurMode.BOX3X3, 1)
        assertEquals(1, bm.getRadius())
        bm.changeMode(PivBlurMode.GAUSSIAN, 1)
        assertEquals(1, bm.getRadius())
        bm.changeMode(PivBlurMode.GAUSSIAN, 5)
        assertEquals(5, bm.getRadius())
    }

    @Test
    fun shouldBlur() {
        // False with blur disabled
        assertFalse(bm.shouldBlur(fixture.draw1, false))

        // Setup blur
        bm.changeMode(PivBlurMode.BOX3X3, 1)
        bm.changeDrawable(fixture.draw1)

        // True with first radius change
        assertTrue(bm.shouldBlur(fixture.draw1, false))
        bm.setLastRadius(1)

        // False with no mode or radius change
        bm.changeMode(PivBlurMode.BOX3X3, 1)
        assertFalse(bm.shouldBlur(fixture.draw1, false))
        bm.setLastRadius(1)

        // True with mode changed
        bm.changeMode(PivBlurMode.BOX5X5, 1)
        assertTrue(bm.shouldBlur(fixture.draw1, false))
        bm.setLastRadius(1)

        // False with drawable changed but checkDrawable false
        bm.changeMode(PivBlurMode.BOX5X5, 1)
        assertFalse(bm.shouldBlur(fixture.draw2, false))
        bm.setLastRadius(1)

        // True with drawable changed and checkDrawable true
        bm.changeMode(PivBlurMode.BOX5X5, 1)
        assertTrue(bm.shouldBlur(fixture.draw2, true))
        bm.setLastRadius(1)

        // True with radius changed
        bm.changeMode(PivBlurMode.BOX5X5, 2)
        assertTrue(bm.shouldBlur(fixture.draw1, false))
        bm.setLastRadius(2)
    }
}
