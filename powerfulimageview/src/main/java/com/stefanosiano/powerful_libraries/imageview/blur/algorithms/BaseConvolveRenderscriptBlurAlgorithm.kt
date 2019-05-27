package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap

import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions
import java.lang.ref.WeakReference


/**
 * Class that performs the gaussian blur with 3x3 coefficient matrix using renderscript.
 * Changing radius will repeat the process radius times.
 */

internal abstract class BaseConvolveRenderscriptBlurAlgorithm : BlurAlgorithm {}