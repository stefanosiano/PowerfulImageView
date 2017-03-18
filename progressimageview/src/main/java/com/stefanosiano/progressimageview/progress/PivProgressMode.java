package com.stefanosiano.progressimageview.progress;

/**
 * Created by stefano on 14/03/17.
 */

public enum PivProgressMode {
    PROGRESS_MODE_DISABLED(0),
    PROGRESS_MODE_INDETERMINATE(1),
    PROGRESS_MODE_DETERMINATE(2),
    PROGRESS_MODE_HORIZONTAL_DETERMINATE(3),
    PROGRESS_MODE_HORIZONTAL_INDETERMINATE(4);

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
            case 3:
                return PROGRESS_MODE_HORIZONTAL_DETERMINATE;
            case 4:
                return PROGRESS_MODE_HORIZONTAL_INDETERMINATE;
            default:
            case 0:
                return PROGRESS_MODE_DISABLED;
        }
    }
}