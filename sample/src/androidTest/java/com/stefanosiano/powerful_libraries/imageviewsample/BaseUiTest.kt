package com.stefanosiano.powerful_libraries.imageviewsample

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import kotlin.test.BeforeTest
import kotlin.test.assertFalse

abstract class BaseUiTest {

    /** Runner of the test. */
    protected lateinit var runner: AndroidJUnitRunner

    /** Application context for the current test. */
    protected lateinit var context: Context

    @BeforeTest
    fun baseSetUp() {
        runner = InstrumentationRegistry.getInstrumentation() as AndroidJUnitRunner
        context = ApplicationProvider.getApplicationContext()
        context.cacheDir.deleteRecursively()
    }

    protected fun assertBitmapsDifferent(vararg bitmaps: Bitmap?) {
        for (i in bitmaps.indices) {
            for (j in bitmaps.indices) {
                if (i != j) {
                    assertFalse(bitmaps[i].contentEquals(bitmaps[j]))
                }
            }
        }
    }
}

internal fun Bitmap?.contentEquals(b2: Bitmap?): Boolean {
    if (this == null && b2 == null) {
        return true
    }
    if (this == null || b2 == null) {
        return false
    }
    if (byteCount != b2.byteCount) {
        return false
    }
    for (i in 0 until width) {
        for (j in 0 until height) {
            val c1 = getColor(i, j)
            val c2 = b2.getColor(i, j)
            if (c1 != c2) {
                return false
            }
        }
    }
    return true
}

internal fun Drawable.createBitmap(): Bitmap {
    val bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0 || this is ColorDrawable) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

internal fun ImageView.createBitmap(): Bitmap {
    val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0 || drawable is ColorDrawable) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
