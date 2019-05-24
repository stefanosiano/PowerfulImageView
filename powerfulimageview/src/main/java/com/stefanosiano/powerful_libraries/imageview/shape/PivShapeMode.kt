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
    SOLID_ROUNDED_RECTANGLE(8)
/*
    /** Shape cut by a diagonal */
    DIAGONAL(9),

    /** Solid shape cut by a diagonal */
    SOLID_DIAGONAL(10),

    /** Shape cut by an arc */
    ARC(11),

    /** Solid shape cut by an arc */
    SOLID_ARC(12),

    /** Shape cut by a corner */
    CORNER(13),

    /** Solid shape cut by a corner */
    SOLID_CORNER(14),

    /** Shape cut by a rounded corner */
    ROUNDED_CORNER(15),

    /** Solid shape cut by a rounded corner */
    SOLID_ROUNDED_CORNER(16)*/;

    /**
     * Shapes achieved through bitmap shaders.
     * Note: They are incompatible with animations, so you shouldn't use them.
     *
     * @return True if the image is circle, oval or rounded_rectangle, false otherwise
     */
    fun isRounded(): Boolean = this == CIRCLE || this == OVAL || this == ROUNDED_RECTANGLE

    /**
     * Shapes achieved through paths.
     * Note: They are incompatible with animations, so you shouldn't use them.
     * Also, they are slightly less efficient then bitmap shaders
     *
     * @return True if the image is diagonal, arc, corner or rounded_corner, false otherwise
     */
//    fun isCut(): Boolean = this == DIAGONAL || this == ARC || this == CORNER || this == ROUNDED_CORNER

    /**
     * Solid shapes are shapes with a solid color drawn over them. They are very efficient and compatible
     * with any image loader library, any animation and any drawable. They also cover eventual backgrounds.
     * Note: If you want to show something behind the image, you cannot use these shapes.
     *
     * @return True if the image is solid_circle, solid_oval, solid_rounded_rectangle, solid_diagonal, solid_arc, solid_corner or solid_rounded_corner, false otherwise
     */
    fun isSolid(): Boolean = this == SOLID_CIRCLE || this == SOLID_OVAL || this == SOLID_ROUNDED_RECTANGLE //|| this == SOLID_DIAGONAL || this == SOLID_ARC || this == SOLID_CORNER || this == SOLID_ROUNDED_CORNER

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