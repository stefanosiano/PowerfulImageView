package com.stefanosiano.powerfullibraries.imageview.blur


/** Mode of the blur that will be used to blur the image */

enum class PivBlurMode constructor(
        val value: Int,
        val fallbackMode: PivBlurMode?,
        /** Returns the fallback mode to try in case of error (made for renderscript)  */
        val usesRenderscript: Boolean) {

    DISABLED(0, null, false),
    GAUSSIAN5X5(1, DISABLED, false),
    GAUSSIAN5X5_RS(2, GAUSSIAN5X5, true),
    GAUSSIAN3X3(3, DISABLED, false),
    GAUSSIAN3X3_RS(4, GAUSSIAN3X3, true),
    GAUSSIAN(5, DISABLED, false),
    GAUSSIAN_RS(6, GAUSSIAN, true),
    BOX3X3(7, DISABLED, false),
    BOX3X3_RS(8, BOX3X3, true),
    BOX5X5(9, DISABLED, false),
    BOX5X5_RS(10, BOX5X5, true),
    STACK(11, DISABLED, false),
    STACK_RS(12, STACK, true);


    internal companion object {

        /** Returns the mode associated to the passed value, or disabled if the value is invalid  */
        fun fromValue(value: Int): PivBlurMode {
            when (value) {
                12 -> return STACK_RS
                11 -> return STACK
                10 -> return BOX5X5_RS
                9 -> return BOX5X5
                8 -> return BOX3X3_RS
                7 -> return BOX3X3
                6 -> return GAUSSIAN_RS
                5 -> return GAUSSIAN
                4 -> return GAUSSIAN3X3_RS
                3 -> return GAUSSIAN3X3
                2 -> return GAUSSIAN5X5_RS
                1 -> return GAUSSIAN5X5
                0 -> return DISABLED
                else -> return DISABLED
            }
        }
    }
}