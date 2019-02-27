package com.stefanosiano.powerful_libraries.imageview

import android.graphics.Bitmap
import java.lang.Exception

class Extensions

internal fun tryOrPrint(function: () -> Unit) = try { function.invoke() } catch (e: Exception) { e.printStackTrace() }
internal fun <T> tryOr(value: T, function: () -> T): T = try { function.invoke() } catch (e: Exception) { value }
internal fun <T> tryOrNull(function: () -> T): T? = try { function.invoke() } catch (e: Exception) { null }

internal fun Bitmap.safeWidth(): Int? = if(isRecycled) null else width
internal fun Bitmap.safeHeight(): Int? = if(isRecycled) null else height
