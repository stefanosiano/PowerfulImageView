package com.stefanosiano.powerful_libraries.imageview.shape.drawers

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeScaleType
import com.stefanosiano.powerful_libraries.imageview.shape.ShapeOptions

/** Interface that handles options, drawing and updating of the image shapes on the View. */
internal interface ShapeDrawer {

    /** Method that updates the [drawable] to show on normal, square and rectangle shapes. */
    fun changeDrawable(drawable: Drawable?)

    /** Method that informs if the drawer requires the changeBitmap() method. */
    fun requireBitmap(): Boolean

    /** Method that updates the [bitmap] to show on rounded, circle and oval shapes. */
    fun changeBitmap(bitmap: Bitmap?)

    /** Set the [matrix] to be used in the shape, and the current [scaleType]. */
    fun setMatrix(scaleType: PivShapeScaleType, matrix: Matrix)

    /** Initialize or updates all the variables needed to work, reading all values from [shapeOptions]. */
    fun setup(shapeOptions: ShapeOptions)

    /**
     * Draws the progress indicator, using the [canvas], [shapeBounds] and [imageBounds].
     * No operation should be performed here, except drawing, for efficiency.
     * No object creation, no allocation, no calculation. Just draw.
     */
    fun draw(canvas: Canvas, borderBounds: RectF, shapeBounds: RectF, imageBounds: RectF)
}
