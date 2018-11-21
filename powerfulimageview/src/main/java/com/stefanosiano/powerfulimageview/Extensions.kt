package com.stefanosiano.powerfulimageview

import java.lang.Exception

class Extensions

fun tryOrPrint(function: () -> Unit) = try { function.invoke() } catch (e: Exception) { e.printStackTrace() }
fun <T> tryOr(value: T, function: () -> T): T = try { function.invoke() } catch (e: Exception) { value }
fun <T> tryOrNull(function: () -> T): T? = try { function.invoke() } catch (e: Exception) { null }
