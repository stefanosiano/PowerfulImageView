package com.stefanosiano.powerlessimageview.shape;

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
    OVAL(4);


    private final int value;
    PivShapeMode(int value){this.value = value;}

    public final int getValue() {
        return value;
    }

    /** Returns the mode associated to the passed value, or none if the value is invalid */
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
            default:
            case 0:
                return NORMAL;
        }
    }
}