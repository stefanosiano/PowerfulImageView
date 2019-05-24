package com.stefanosiano.powerful_libraries.imageview

import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import java.lang.Exception

class Extensions

internal fun tryOrPrint(function: () -> Unit) = try { function.invoke() } catch (e: Exception) { e.printStackTrace() }
internal fun <T> tryOr(value: T, function: () -> T): T = try { function.invoke() } catch (e: Exception) { value }
internal fun <T> tryOrNull(function: () -> T): T? = try { function.invoke() } catch (e: Exception) { null }

internal fun Bitmap.safeWidth(): Int? = if(isRecycled) null else width
internal fun Bitmap.safeHeight(): Int? = if(isRecycled) null else height

internal fun RectF.toRect() = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
internal fun RectF.toRect(multiplier: Float) = Rect((left*multiplier).toInt(), (top*multiplier).toInt(), (right*multiplier).toInt(), (bottom*multiplier).toInt())
internal fun Path.moveTo(x: Int, y: Int) = moveTo(x.toFloat(), y.toFloat())
internal fun Path.lineTo(x: Int, y: Int) = lineTo(x.toFloat(), y.toFloat())