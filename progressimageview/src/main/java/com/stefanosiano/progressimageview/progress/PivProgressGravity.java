package com.stefanosiano.progressimageview.progress;

/**
 * Created by stefano on 3/19/17.
 */

public enum PivProgressGravity {
    CENTER(0),
    START(1),
    END(2),
    TOP(3),
    TOP_START(4),
    TOP_END(5),
    BOTTOM(6),
    BOTTOM_START(7),
    BOTTOM_END(8);

    private final int value;
    PivProgressGravity(int value){this.value = value;}

    public final int getValue() {
        return value;
    }
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
}
