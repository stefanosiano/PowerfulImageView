package com.stefanosiano.powerful_libraries.imageview.shape


/** Mode of the shape of the image  */
enum class PivShapeMode private constructor(internal val value: Int) {

    /** Normal (default) shape  */
    NORMAL(0),

    /** Circle shape  */
    CIRCLE(1),

    /** Square shape  */
    SQUARE(2),

    /** Rectangle shape  */
    RECTANGLE(3),

    /** Oval shape  */
    OVAL(4),

    /** Rounded rectangle shape  */
    ROUNDED_RECTANGLE(5),

    /** Solid circle shape  */
    SOLID_CIRCLE(6),

    /** Solid oval shape  */
    SOLID_OVAL(7),

    /** Solid rounded rectangle shape  */
    SOLID_ROUNDED_RECTANGLE(8);

    /**
     * Shapes achieved through bitmap shaders.
     * Note: They are incompatible with animations, so you shouldn't use them. Also, vector drawables
     * don't scale as expected.
     *
     * @return True if the image is circle, oval or rounded_rectangle, false otherwise
     */
    fun isRounded(): Boolean = this == CIRCLE || this == OVAL || this == ROUNDED_RECTANGLE

    /**
     * Solid shapes are shapes with a solid color drawn over them. They are very efficient and compatible
     * with any image loader library, any animation and any drawable. They also cover eventual backgrounds.
     * Note: If you want to show something behind the image, you cannot use these shapes.
     *
     * @return True if the image is solid_circle, solid_oval or solid_rounded_rectangle, false otherwise
     */
    fun isSolid(): Boolean = this == SOLID_CIRCLE || this == SOLID_OVAL || this == SOLID_ROUNDED_RECTANGLE

    /**
     * Only size is changed in these shapes. They should perform as efficiently as normal imageViews
     *
     * @return True if the image is normal, square or rectangle, false otherwise
     */
    fun isRectangular(): Boolean = this == NORMAL || this == SQUARE || this == RECTANGLE


    companion object {

        /** Returns the mode associated to the passed value, or normal if the value is invalid  */
        fun fromValue(value: Int): PivShapeMode = when (value) {
            1 -> CIRCLE
            2 -> SQUARE
            3 -> RECTANGLE
            4 -> OVAL
            5 -> ROUNDED_RECTANGLE
            6 -> SOLID_CIRCLE
            7 -> SOLID_OVAL
            8 -> SOLID_ROUNDED_RECTANGLE
            0 -> NORMAL
            else -> NORMAL

        }
    }
}