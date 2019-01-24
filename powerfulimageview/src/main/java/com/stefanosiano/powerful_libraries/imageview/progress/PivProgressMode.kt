package com.stefanosiano.powerful_libraries.imageview.progress


/** Mode of the progress indicator that will be drawn on the image  */
enum class PivProgressMode private constructor(val value: Int) {

    /** No progress indicator  */
    NONE(0),

    /** Circular progress indicator  */
    CIRCULAR(1),

    /** Horizontal progress indicator  */
    HORIZONTAL(2);

    companion object {

        /** Returns the mode associated to the passed value, or none if the value is invalid  */
        fun fromValue(value: Int): PivProgressMode {
            return when (value) {
                1 -> CIRCULAR
                2 -> HORIZONTAL
                0 -> NONE
                else -> NONE
            }
        }
    }
}