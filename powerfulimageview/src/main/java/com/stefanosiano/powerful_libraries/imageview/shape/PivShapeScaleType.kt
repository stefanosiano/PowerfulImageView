package com.stefanosiano.powerful_libraries.imageview.shape

import android.graphics.Matrix
import android.widget.ImageView


/** Custom scale type of the image */
enum class PivShapeScaleType constructor(internal val value: Int) {
    /**
     * Scale using the image matrix when drawing. The image matrix can be set using
     * [ImageView.setImageMatrix]. From XML, use this syntax:
     * `app:piv_shape_scaleType="matrix"`.
     */
    MATRIX(0),
    /**
     * Scale the image using [Matrix.ScaleToFit.FILL].
     * From XML, use this syntax: `app:piv_shape_scaleType="fitXY"`.
     */
    FIT_XY(1),
    /**
     * Scale the image using [Matrix.ScaleToFit.START].
     * From XML, use this syntax: `app:piv_shape_scaleType="fitStart"`.
     */
    FIT_START(2),
    /**
     * Scale the image using [Matrix.ScaleToFit.CENTER].
     * From XML, use this syntax:
     * `app:piv_shape_scaleType="fitCenter"`.
     */
    FIT_CENTER(3),
    /**
     * Scale the image using [Matrix.ScaleToFit.END].
     * From XML, use this syntax: `app:piv_shape_scaleType="fitEnd"`.
     */
    FIT_END(4),
    /**
     * Center the image in the view, but perform no scaling.
     * From XML, use this syntax: `app:piv_shape_scaleType="center"`.
     */
    CENTER(5),
    /**
     * Scale the image uniformly (maintain the image's aspect ratio) so
     * that both dimensions (width and height) of the image will be equal
     * to or larger than the corresponding dimension of the view
     * (minus padding). The image is then centered in the view.
     * From XML, use this syntax: `app:piv_shape_scaleType="centerCrop"`.
     */
    CENTER_CROP(6),
    /**
     * Scale the image uniformly (maintain the image's aspect ratio) so
     * that both dimensions (width and height) of the image will be equal
     * to or less than the corresponding dimension of the view
     * (minus padding). The image is then centered in the view.
     * From XML, use this syntax: `app:piv_shape_scaleType="centerInside"`.
     */
    CENTER_INSIDE(7),
    /**
     * Scale the image uniformly (maintain the image's aspect ratio) so
     * that both dimensions (width and height) of the image will be equal
     * to or larger than the corresponding dimension of the view
     * (minus padding). The image is then translated so that the top of the image is shown.
     * From XML, use this syntax: `app:piv_shape_scaleType="topCrop"`.
     */
    TOP_CROP(8),
    /**
     * Scale the image uniformly (maintain the image's aspect ratio) so
     * that both dimensions (width and height) of the image will be equal
     * to or larger than the corresponding dimension of the view
     * (minus padding). The image is then translated so that the bottom of the image is shown.
     * From XML, use this syntax: `app:piv_shape_scaleType="topCrop"`.
     */
    BOTTOM_CROP(9);

    companion object {

        /** Returns the custom scaleType from the android scaleType or CENTER if the scaleType passed is null  */
        fun getFromScaleType(scaleType: ImageView.ScaleType?): PivShapeScaleType = when (scaleType ?: CENTER) {
            ImageView.ScaleType.MATRIX -> MATRIX
            ImageView.ScaleType.FIT_XY -> FIT_XY
            ImageView.ScaleType.FIT_START -> FIT_START
            ImageView.ScaleType.FIT_CENTER -> FIT_CENTER
            ImageView.ScaleType.FIT_END -> FIT_END
            ImageView.ScaleType.CENTER -> CENTER
            ImageView.ScaleType.CENTER_CROP -> CENTER_CROP
            ImageView.ScaleType.CENTER_INSIDE -> CENTER_INSIDE
            else -> MATRIX
        }


        /** Returns the scale type associated to the passed value, or CENTER if the value is invalid  */
        fun fromValue(value: Int): PivShapeScaleType = when (value) {
            0 -> MATRIX
            1 -> FIT_XY
            2 -> FIT_START
            3 -> FIT_CENTER
            4 -> FIT_END
            5 -> CENTER
            6 -> CENTER_CROP
            7 -> CENTER_INSIDE
            8 -> TOP_CROP
            9 -> BOTTOM_CROP
            else -> CENTER
        }
    }
}
