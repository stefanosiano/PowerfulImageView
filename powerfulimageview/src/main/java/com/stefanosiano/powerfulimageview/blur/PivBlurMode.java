package com.stefanosiano.powerfulimageview.blur;

/**
 * Created by stefano on 19/05/17.
 */

public enum PivBlurMode {
    DISABLED(0);


    private final int value;
    PivBlurMode(int value){this.value = value;}

    public final int getValue() {
        return value;
    }

    /** Returns the mode associated to the passed value, or disabled if the value is invalid */
    public static PivBlurMode fromValue(int value){
        switch (value){
            default:
            case 0:
                return DISABLED;
        }
    }
}
