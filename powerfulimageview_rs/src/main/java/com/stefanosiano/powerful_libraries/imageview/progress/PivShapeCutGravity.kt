package com.stefanosiano.powerful_libraries.imageview.progress

/** Gravity of the progress indicator. */
enum class PivShapeCutGravity private constructor(val value: Int) {

    /** Shape will be cut at the start of the image. It respect rtl layouts (on api 17+). */
    START(1),

    /** Shape will be cut at the end of the image. It respect rtl layouts (on api 17+). */
    END(2),

    /** Shape will be cut at the top|center of the image. */
    TOP(3),

    /** Shape will be cut at the top|start of the image. It respect rtl layouts (on api 17+). */
    TOP_START(4),

    /** Shape will be cut at the top|end of the image. It respect rtl layouts (on api 17+). */
    TOP_END(5),

    /** Shape will be cut at the bottom|center of the image. */
    BOTTOM(6),

    /** Shape will be cut at the bottom|start of the image. It respect rtl layouts (on api 17+). */
    BOTTOM_START(7),

    /** Shape will be cut at the bottom|end of the image. It respect rtl layouts (on api 17+). */
    BOTTOM_END(8);

    internal fun isGravityTop(): Boolean = when (this) {
        TOP, TOP_START, TOP_END -> true
        else -> false
    }

    internal fun isGravityBottom(): Boolean = when (this) {
        BOTTOM, BOTTOM_START, BOTTOM_END -> true
        else -> false
    }

    internal fun isGravityLeft(isRtl: Boolean): Boolean = when (this) {
        START, BOTTOM_START, TOP_START -> !isRtl
        END, BOTTOM_END, TOP_END -> isRtl
        else -> false
    }

    internal fun isGravityRight(isRtl: Boolean): Boolean = when (this) {
        START, BOTTOM_START, TOP_START -> isRtl
        END, BOTTOM_END, TOP_END -> !isRtl
        else -> false
    }

    companion object {

        /** Returns the gravity associated to the passed value, or center if the value is invalid. */
        fun fromValue(value: Int): PivShapeCutGravity = when (value) {
            2 -> END
            3 -> TOP
            4 -> TOP_START
            5 -> TOP_END
            6 -> BOTTOM
            7 -> BOTTOM_START
            8 -> BOTTOM_END
            1 -> START
            else -> START
        }
    }
}
