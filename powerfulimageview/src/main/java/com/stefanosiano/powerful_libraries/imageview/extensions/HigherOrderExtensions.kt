package com.stefanosiano.powerful_libraries.imageview.extensions

import android.graphics.RectF
import android.util.Log

internal class HigherOrderExtensions

@Suppress("TooGenericExceptionCaught")
internal fun <T> tryOrPrint(function: () -> T) = try { function.invoke() } catch (e: Exception) {
    Log.e(function::class.java.name, e.message ?: "")
    null
}

@Suppress("SwallowedException", "TooGenericExceptionCaught")
internal fun <T> tryOr(value: T, function: () -> T): T = try { function.invoke() } catch (e: Exception) { value }

@Suppress("SwallowedException", "TooGenericExceptionCaught")
internal fun <T> tryOrNull(function: () -> T): T? = try { function.invoke() } catch (e: Exception) { null }

internal fun rectF(width: Int, height: Int) = RectF(0f, 0f, width.toFloat(), height.toFloat())
