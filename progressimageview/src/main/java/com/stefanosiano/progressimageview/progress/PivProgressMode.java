package com.stefanosiano.progressimageview.progress;

/**
 * Created by stefano on 14/03/17.
 */

public enum PivProgressMode {
    NONE(0),
    INDETERMINATE(1),
    DETERMINATE(2),
    HORIZONTAL_DETERMINATE(3),
    HORIZONTAL_INDETERMINATE(4);

    private final int value;
    PivProgressMode(int value){this.value = value;}

    public int getValue() {
        return value;
    }
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