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
}