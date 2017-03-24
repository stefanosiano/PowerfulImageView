package com.stefanosiano.powerfulimageview.progress;

/** Mode of the progress indicator that will be drawn on the image */
public enum PivProgressMode {

    /** No progress indicator */
    NONE(0),

    /** Circular indeterminate progress indicator */
    INDETERMINATE(1),

    /** Circular determinate progress indicator */
    DETERMINATE(2),

    /** Horizontal determinate progress indicator */
    HORIZONTAL_DETERMINATE(3),

    /** Horizontal indeterminate progress indicator */
    HORIZONTAL_INDETERMINATE(4);

    private final int value;
    PivProgressMode(int value){this.value = value;}

    public final int getValue() {
        return value;
    }

    /** Returns the mode associated to the passed value, or none if the value is invalid */
    public static PivProgressMode fromValue(int value){
        switch (value){
            case 1:
                return INDETERMINATE;
            case 2:
                return DETERMINATE;
            case 3:
                return HORIZONTAL_DETERMINATE;
            case 4:
                return HORIZONTAL_INDETERMINATE;
            default:
            case 0:
                return NONE;
        }
    }
}