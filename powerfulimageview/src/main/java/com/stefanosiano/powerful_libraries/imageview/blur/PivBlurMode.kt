package com.stefanosiano.powerful_libraries.imageview.blur

/** Mode of the blur that will be used to blur the image. */

enum class PivBlurMode constructor(
    internal val value: Int,
    internal val fallbackMode: PivBlurMode,
    /** Returns the fallback mode to try in case of error (made for Renderscript). */
    internal val usesRenderscript: Boolean
) {

    /** No blur. */
    DISABLED(0, DISABLED, false),

    /** Gaussian blur with area of 5x5 (java). */
    GAUSSIAN5X5(1, DISABLED, false),

    /** Gaussian blur with area of 5x5 (Renderscript). Available only on imageview_rs module. */
    GAUSSIAN5X5_RS(2, GAUSSIAN5X5, true),

    /** Gaussian blur with area of 3x3 (java). */
    GAUSSIAN3X3(3, DISABLED, false),

    /** Gaussian blur with area of 3x3 (Renderscript). Available only on imageview_rs module. */
    GAUSSIAN3X3_RS(4, GAUSSIAN3X3, true),

    /** Gaussian blur with custom area size (java). */
    GAUSSIAN(5, DISABLED, false),

    /** Gaussian blur with custom area size (Renderscript). Available only on imageview_rs module. */
    GAUSSIAN_RS(6, GAUSSIAN, true),

    /** Box blur with area of 3x3 (java). */
    BOX3X3(7, DISABLED, false),

    /** Box blur with area of 3x3 (Renderscript). Available only on imageview_rs module. */
    BOX3X3_RS(8, BOX3X3, true),

    /** Box blur with area of 5x5 (java). */
    BOX5X5(9, DISABLED, false),

    /** Box blur with area of 5x5 (Renderscript). Available only on imageview_rs module. */
    BOX5X5_RS(10, BOX5X5, true),

    /** Stack blur. Only Java method is available, but it's the fastest among the java methods.
     *  Use it if you don't want to use Renderscript. */
    STACK(11, DISABLED, false);

    internal fun getFallbackMode(useRsFallback: Boolean) = fallbackMode.takeIf { useRsFallback } ?: DISABLED

    internal companion object {

        /** Returns the mode associated to the passed value, or disabled if the value is invalid. */
        fun fromValue(value: Int): PivBlurMode = when (value) {
            11 -> STACK
            10 -> BOX5X5_RS
            9 -> BOX5X5
            8 -> BOX3X3_RS
            7 -> BOX3X3
            6 -> GAUSSIAN_RS
            5 -> GAUSSIAN
            4 -> GAUSSIAN3X3_RS
            3 -> GAUSSIAN3X3
            2 -> GAUSSIAN5X5_RS
            1 -> GAUSSIAN5X5
            0 -> DISABLED
            else -> DISABLED
        }
    }
}
