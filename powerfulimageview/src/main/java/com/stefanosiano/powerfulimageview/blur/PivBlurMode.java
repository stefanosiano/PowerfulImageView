package com.stefanosiano.powerfulimageview.blur;

/** Mode of the blur that will be used to blur the image */

public enum PivBlurMode {
    DISABLED(0, null),
    GAUSSIAN(1, DISABLED),
    GAUSSIAN_RS(2, GAUSSIAN);


    private final int value;
    private final PivBlurMode fallbackMode;
    PivBlurMode(int value, PivBlurMode fallbackMode){this.value = value;this.fallbackMode = fallbackMode;}

    public final int getValue() {
        return value;
    }

    /** Returns the fallback mode to try in case of error (made for renderscript) */
    public PivBlurMode getFallbackMode() {
        return fallbackMode == null ? DISABLED : fallbackMode;
    }

    /** Returns the mode associated to the passed value, or disabled if the value is invalid */
    public static PivBlurMode fromValue(int value){
        switch (value){
            case 2:
                return GAUSSIAN_RS;
            case 1:
                return GAUSSIAN;
            default:
            case 0:
                return DISABLED;
        }
    }
}
