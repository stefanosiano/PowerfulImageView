package com.stefanosiano.progressimageview.progress;

/**
 * Created by stefano on 17/03/17.
 */

public class ProgressOptions {

    boolean isDeterminateAnimationEnabled;
    int borderWidth;
    int size;
    float sizePercent;
    float valuePercent;
    int frontColor;
    int backColor;
    int indeterminateColor;
    PivProgressGravity gravity;

    private float left, top, right, bottom;
    private final boolean isCircleBorderWidthFixed, isRtl;

    public ProgressOptions(boolean isDeterminateAnimationEnabled, int borderWidth, int size, float sizePercent, float valuePercent, int frontColor, int backColor, int indeterminateColor, int gravity, boolean rtl) {
        this.isDeterminateAnimationEnabled = isDeterminateAnimationEnabled;
        this.borderWidth = borderWidth;
        this.isCircleBorderWidthFixed = borderWidth > 0;
        this.size = size;
        this.sizePercent = sizePercent;
        this.valuePercent = valuePercent;
        this.frontColor = frontColor;
        this.backColor = backColor;
        this.indeterminateColor = indeterminateColor;
        this.gravity = PivProgressGravity.fromValue(gravity);
        this.isRtl = rtl;

        calculateBounds(0, 0, PivProgressMode.NONE);
    }

    public void calculateBounds(int w, int h, PivProgressMode mode){
        if(sizePercent >= 0){
            size = (int) (w * (double) sizePercent / 100);
        }
        if(!isCircleBorderWidthFixed){
            borderWidth = Math.round(size /10);
        }
        if(borderWidth < 1)
            borderWidth = 1;

        switch(mode){
            case DETERMINATE:
            case INDETERMINATE:
                left = w - size - borderWidth;
                right = w - borderWidth;
                top = h - size - borderWidth;
                bottom = h - borderWidth;
                break;
            case HORIZONTAL_DETERMINATE:
            case HORIZONTAL_INDETERMINATE:
                left = w - size;
                right = w;
                top = h - borderWidth;
                bottom = h;
                break;
            case NONE:
            default:
                left = 0;
                right = 0;
                top = 0;
                bottom = 0;
                break;
        }
    }


    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }
}
