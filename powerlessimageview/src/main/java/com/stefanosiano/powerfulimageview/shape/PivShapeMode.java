package com.stefanosiano.powerfulimageview.shape;

/** Mode of the shape of the image */
public enum PivShapeMode {

    /** Normal (default) shape */
    NORMAL(0),

    /** Circle shape */
    CIRCLE(1),

    /** Square shape */
    SQUARE(2),

    /** Rectangle shape */
    RECTANGLE(3),

    /** Oval shape */
    OVAL(4),

    /** Rounded rectangle shape */
    ROUNDED_RECTANGLE(5),

    /** Solid circle shape */
    SOLID_CIRCLE(6),

    /** Solid oval shape */
    SOLID_OVAL(7),

    /** Solid rounded rectangle shape */
    SOLID_ROUNDED_RECTANGLE(8);


    private final int value;
    PivShapeMode(int value){this.value = value;}

    public final int getValue() {
        return value;
    }

    /** Returns the mode associated to the passed value, or normal if the value is invalid */
    public static PivShapeMode fromValue(int value){
        switch (value){
            case 1:
                return CIRCLE;
            case 2:
                return SQUARE;
            case 3:
                return RECTANGLE;
            case 4:
                return OVAL;
            case 5:
                return ROUNDED_RECTANGLE;
            case 6:
                return SOLID_CIRCLE;
            case 7:
                return SOLID_OVAL;
            case 8:
                return SOLID_ROUNDED_RECTANGLE;
            default:
            case 0:
                return NORMAL;
        }
    }

    /**
     * Shapes achieved through bitmap shaders.
     * Note: They are incompatible with animations, so you shouldn't use them. Also, vector drawables
     *      don't scale as expected.
     *
     * @return True if the image is circle, oval or rounded_rectangle, false otherwise
     */
    public boolean isRounded(){
        return this == CIRCLE ||this == OVAL ||this == ROUNDED_RECTANGLE;
    }

    /**
     * Solid shapes are shapes with a solid color drawn over them. They are very efficient and compatible
     * with any image loader library, any animation and any drawable. They also cover eventual backgrounds.
     * Note: If you want to show something behind the image, you cannot use these shapes.
     *
     * @return True if the image is solid_circle, solid_oval or solid_rounded_rectangle, false otherwise
     */
    public boolean isSolid(){
        return this == SOLID_CIRCLE ||this == SOLID_OVAL ||this == SOLID_ROUNDED_RECTANGLE;
    }

    /**
     * Only size is changed in these shapes. They should perform as efficiently as normal imageViews
     *
     * @return True if the image is normal, square or rectangle, false otherwise
     */
    public boolean isRectangular(){
        return this == NORMAL ||this == SQUARE ||this == RECTANGLE;
    }
}