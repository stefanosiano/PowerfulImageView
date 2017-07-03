package com.stefanosiano.powerfulimageview.blur;

/** Mode of the blur that will be used to blur the image */

public enum PivBlurMode {
    DISABLED(0, null, false),
    GAUSSIAN5X5(1, DISABLED, false),
    GAUSSIAN5X5_RS(2, GAUSSIAN5X5, true),
    GAUSSIAN3X3(3, DISABLED, false),
    GAUSSIAN3X3_RS(4, GAUSSIAN3X3, true),
    GAUSSIAN(5, DISABLED, false),
    GAUSSIAN_RS(6, GAUSSIAN, true),
    STACK(7, DISABLED, false),
    STACK_RS(8, STACK, true);


    private final int value;
    private final PivBlurMode fallbackMode;
    private final boolean usesRenderscript;
    PivBlurMode(int value, PivBlurMode fallbackMode, boolean usesRenderscript){this.value = value;this.fallbackMode = fallbackMode; this.usesRenderscript = usesRenderscript;}

    public final int getValue() {
        return value;
    }

    public boolean isUsesRenderscript() {
        return usesRenderscript;
    }

    /** Returns the fallback mode to try in case of error (made for renderscript) */
    public PivBlurMode getFallbackMode() {
        return fallbackMode == null ? DISABLED : fallbackMode;
    }

    /** Returns the mode associated to the passed value, or disabled if the value is invalid */
    public static PivBlurMode fromValue(int value){
        switch (value){
            case 8:
                return STACK_RS;
            case 7:
                return STACK;
            case 6:
                return GAUSSIAN_RS;
            case 5:
                return GAUSSIAN;
            case 4:
                return GAUSSIAN3X3_RS;
            case 3:
                return GAUSSIAN3X3;
            case 2:
                return GAUSSIAN5X5_RS;
            case 1:
                return GAUSSIAN5X5;
            default:
            case 0:
                return DISABLED;
        }
    }
}
