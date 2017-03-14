package com.stefanosiano.progressimageview.progress;

/**
 * Created by stefano on 14/03/17.
 */

public enum PivProgressMode {
    PROGRESS_MODE_DISABLED(0),
    PROGRESS_MODE_INDETERMINATE(1),
    PROGRESS_MODE_DETERMINATE(2);

    public int value;
    PivProgressMode(int value){this.value = value;}

    public int getValue() {
        return value;
    }
    public static PivProgressMode fromValue(int value){
        switch (value){
            case 1:
                return PROGRESS_MODE_INDETERMINATE;
            case 2:
                return PROGRESS_MODE_DETERMINATE;
            default:
            case 0:
                return PROGRESS_MODE_DISABLED;
        }
    }
}