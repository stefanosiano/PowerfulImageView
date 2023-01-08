package com.stefanosiano.powerful_libraries.imageviewsample

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stefanosiano.powerful_libraries.imageview.PowerfulImageView
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeMode
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ShapeUiTest : BaseUiTest() {
    private lateinit var piv: PowerfulImageView
    private lateinit var normalBitmap: Bitmap
    private lateinit var shapeActivityScenario: ActivityScenario<ShapeActivity>

    @BeforeTest
    fun setup() {
        shapeActivityScenario = launchActivity<ShapeActivity>()
        shapeActivityScenario.onActivity {
            piv = it.binding.shapeImage
            normalBitmap = piv.createBitmap()
        }
    }

    @AfterTest
    fun close() {
        shapeActivityScenario.moveToState(Lifecycle.State.DESTROYED)
        Thread.sleep(100)
    }

    @Test
    fun changeShapeModesCreateDifferentBitmaps() {
        piv.getShapeOptions().radiusX = 64F
        piv.getShapeOptions().radiusY = 64F
        piv.getShapeOptions().ratio = 3F

        val normal = setModeAndGetBitmap(piv, PivShapeMode.NORMAL)
        val circle = setModeAndGetBitmap(piv, PivShapeMode.CIRCLE)
        val oval = setModeAndGetBitmap(piv, PivShapeMode.OVAL)
        val roundedRectangle = setModeAndGetBitmap(piv, PivShapeMode.ROUNDED_RECTANGLE)
        val square = setModeAndGetBitmap(piv, PivShapeMode.SQUARE)
        val rectangle = setModeAndGetBitmap(piv, PivShapeMode.RECTANGLE)
        val solidCircle = setModeAndGetBitmap(piv, PivShapeMode.SOLID_CIRCLE)
        val solidOval = setModeAndGetBitmap(piv, PivShapeMode.SOLID_OVAL)
        val solidRoundedRectangle = setModeAndGetBitmap(piv, PivShapeMode.SOLID_ROUNDED_RECTANGLE)
        val normal2 = setModeAndGetBitmap(piv, PivShapeMode.NORMAL)
        val circle2 = setModeAndGetBitmap(piv, PivShapeMode.CIRCLE)
        val oval2 = setModeAndGetBitmap(piv, PivShapeMode.OVAL)
        val roundedRectangle2 = setModeAndGetBitmap(piv, PivShapeMode.ROUNDED_RECTANGLE)
        val square2 = setModeAndGetBitmap(piv, PivShapeMode.SQUARE)
        val rectangle2 = setModeAndGetBitmap(piv, PivShapeMode.RECTANGLE)
        val solidCircle2 = setModeAndGetBitmap(piv, PivShapeMode.SOLID_CIRCLE)
        val solidOval2 = setModeAndGetBitmap(piv, PivShapeMode.SOLID_OVAL)
        val solidRoundedRectangle2 = setModeAndGetBitmap(piv, PivShapeMode.SOLID_ROUNDED_RECTANGLE)

        assertTrue(normal.contentEquals(normalBitmap))
        assertBitmapsDifferent(normalBitmap, circle, oval, roundedRectangle, square, rectangle, solidCircle, solidOval, solidRoundedRectangle)
        assertTrue(normal2.contentEquals(normalBitmap))
        assertTrue(circle.contentEquals(circle2))
        assertTrue(oval.contentEquals(oval2))
        assertTrue(roundedRectangle.contentEquals(roundedRectangle2))
        assertTrue(square.contentEquals(square2))
        assertTrue(rectangle.contentEquals(rectangle2))
        assertTrue(solidCircle.contentEquals(solidCircle2))
        assertTrue(solidOval.contentEquals(solidOval2))
        assertTrue(solidRoundedRectangle.contentEquals(solidRoundedRectangle2))
    }

    @Test
    fun rectanglesEqualsNormalIfNoRatioSpecified() {
        val normal = setModeAndGetBitmap(piv, PivShapeMode.NORMAL)
        val rectangle = setModeAndGetBitmap(piv, PivShapeMode.RECTANGLE)
        assertTrue(normal.contentEquals(rectangle))
    }

    @Test
    fun solidShapesEqualNormalShapesIfSolidColorIsTransparent() {
        piv.getShapeOptions().solidColor = Color.TRANSPARENT
        val rectangle = setModeAndGetBitmap(piv, PivShapeMode.RECTANGLE)
        val solidRoundedRectangle = setModeAndGetBitmap(piv, PivShapeMode.SOLID_ROUNDED_RECTANGLE)
        val square = setModeAndGetBitmap(piv, PivShapeMode.SQUARE)
        val solidCircle = setModeAndGetBitmap(piv, PivShapeMode.SOLID_CIRCLE)
        val solidOval = setModeAndGetBitmap(piv, PivShapeMode.SOLID_OVAL)
        assertTrue(rectangle.contentEquals(solidRoundedRectangle))
        assertTrue(rectangle.contentEquals(solidOval))
        assertTrue(square.contentEquals(solidCircle))
    }

    @Test
    fun rectanglesEqualSquareAndCircleWhenRatioIs1() {
        piv.getShapeOptions().ratio = 1F
        val rectangle = setModeAndGetBitmap(piv, PivShapeMode.RECTANGLE)
        val square = setModeAndGetBitmap(piv, PivShapeMode.SQUARE)
        val circle = setModeAndGetBitmap(piv, PivShapeMode.CIRCLE)
        val oval = setModeAndGetBitmap(piv, PivShapeMode.OVAL)
        val solidCircle = setModeAndGetBitmap(piv, PivShapeMode.SOLID_CIRCLE)
        val solidOval = setModeAndGetBitmap(piv, PivShapeMode.SOLID_OVAL)
        assertTrue(rectangle.contentEquals(square))
        assertTrue(circle.contentEquals(oval))
        assertTrue(solidOval.contentEquals(solidCircle))
    }

    private fun setModeAndGetBitmap(piv: PowerfulImageView, mode: PivShapeMode): Bitmap {
        piv.setShapeMode(mode)
        Thread.sleep(100)
        return piv.createBitmap()
    }
}