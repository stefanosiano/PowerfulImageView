package com.stefanosiano.powerful_libraries.imageview.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

class DrawableExtensions

/** Return [true] if no intrinsic width or height is available, or if this drawable represents a [ColorDrawable]. */
internal fun Drawable.hasNoIntrinsicSize() = intrinsicWidth <= 0 || intrinsicHeight <= 0 || this is ColorDrawable
internal fun Drawable.isVector() = javaClass.name == "android.graphics.drawable.VectorDrawable" ||
    this is VectorDrawableCompat

/** Creates a bitmap from a drawable. If it's a [ColorDrawable] the bitmap created will be 1x1, otherwise [w]x[h]. */
@Throws(IllegalArgumentException::class)
internal fun Drawable.createBitmap(w: Int, h: Int): Bitmap {
    val bitmap = if (hasNoIntrinsicSize()) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

internal fun Drawable.setBounds(bounds: RectF) = setBounds(
    bounds.left.toInt(),
    bounds.top.toInt(),
    bounds.right.toInt(),
    bounds.bottom.toInt()
)
