package com.stefanosiano.powerful_libraries.imageview.extensions

import android.graphics.Bitmap

class BitmapExtensions

internal fun Bitmap?.safeWidth(fallbackWidth: Int): Int = if (this == null || isRecycled) fallbackWidth else width
internal fun Bitmap?.safeHeight(fallbackHeight: Int): Int = if (this == null || isRecycled) fallbackHeight else height
