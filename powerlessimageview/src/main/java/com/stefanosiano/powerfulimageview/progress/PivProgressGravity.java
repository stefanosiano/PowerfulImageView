package com.stefanosiano.powerfulimageview.progress;

/** Gravity of the progress indicator. */
public enum PivProgressGravity {
    /** Indicator will be drawn at the center of the image */
    CENTER(0),

    /** Indicator will be drawn at the start of the image. It respect rtl layouts (on api 17+) */
    START(1),

    /** Indicator will be drawn at the end of the image. It respect rtl layouts (on api 17+) */
    END(2),

    /** Indicator will be drawn at the top|center of the image */
    TOP(3),

    /** Indicator will be drawn at the top|start of the image. It respect rtl layouts (on api 17+) */
    TOP_START(4),

    /** Indicator will be drawn at the top|end of the image. It respect rtl layouts (on api 17+) */
    TOP_END(5),

    /** Indicator will be drawn at the bottom|center of the image */
    BOTTOM(6),

    /** Indicator will be drawn at the bottom|start of the image. It respect rtl layouts (on api 17+) */
    BOTTOM_START(7),

    /** Indicator will be drawn at the bottom|end of the image. It respect rtl layouts (on api 17+) */
    BOTTOM_END(8);

    private final int value;
    PivProgressGravity(int value){this.value = value;}

    public final int getValue() {
        return value;
    }

    /** Returns the gravity associated to the passed value, or center if the value is invalid */
    public static PivProgressGravity fromValue(int value){
        switch (value){
            case 1:
                return START;
            case 2:
                return END;
            case 3:
                return TOP;
            case 4:
                return TOP_START;
            case 5:
                return TOP_END;
            case 6:
                return BOTTOM;
            case 7:
                return BOTTOM_START;
            case 8:
                return BOTTOM_END;
            default:
            case 0:
                return CENTER;
        }
    }

    public final boolean isGravityLeft(boolean isRtl){
        switch (this){
            case START:
            case BOTTOM_START:
            case TOP_START:
                return (!isRtl);
            case END:
            case BOTTOM_END:
            case TOP_END:
                return (isRtl);
            default:
                return false;
        }
    }

    public final boolean isGravityRight(boolean isRtl){
        switch (this){
            case START:
            case BOTTOM_START:
            case TOP_START:
                return (isRtl);
            case END:
            case BOTTOM_END:
            case TOP_END:
                return (!isRtl);
            default:
                return false;
        }
    }

    public final boolean isGravityTop(){
        switch (this){
            case TOP:
            case TOP_START:
            case TOP_END:
                return true;
            default:
                return false;
        }
    }

    public final boolean isGravityBottom(){
        switch (this){
            case BOTTOM:
            case BOTTOM_START:
            case BOTTOM_END:
                return true;
            default:
                return false;
        }
    }
}
