package com.stefanosiano.powerfullibraries.imageview.progress;

/** Mode of the progress indicator that will be drawn on the image */
public enum PivProgressMode {

    /** No progress indicator */
    NONE(0),

    /** Circular progress indicator */
    CIRCULAR(1),

    /** Horizontal progress indicator */
    HORIZONTAL(2);

    private final int value;
    PivProgressMode(int value){this.value = value;}

    public final int getValue() {
        return value;
    }

    /** Returns the mode associated to the passed value, or none if the value is invalid */
    public static PivProgressMode fromValue(int value){
        switch (value){
            case 1:
                return CIRCULAR;
            case 2:
                return HORIZONTAL;
            default:
            case 0:
                return NONE;
        }
    }
}