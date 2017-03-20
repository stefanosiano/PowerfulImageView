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
    boolean isRtlSupportDisabled;
    boolean drawWedge;

    private float left, top, right, bottom;
    private final boolean isCircleBorderWidthFixed, isRtl;

    public ProgressOptions(boolean isDeterminateAnimationEnabled, int borderWidth, int size, float sizePercent,
                           float valuePercent, int frontColor, int backColor, int indeterminateColor, int gravity, boolean rtl, boolean disableRtlSupport, boolean drawWedge) {
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
        this.isRtlSupportDisabled = disableRtlSupport;
        this.drawWedge = drawWedge;

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
                switch (gravity){
                    case START:
                    case BOTTOM_START:
                    case TOP_START:
                        if(isRtl && !isRtlSupportDisabled){
                            left = w - size - borderWidth;
                            right = w - borderWidth;
                        }
                        else {
                            left = borderWidth;
                            right = size + borderWidth;
                        }
                        break;
                    case END:
                    case BOTTOM_END:
                    case TOP_END:
                        if(isRtl && !isRtlSupportDisabled){
                            left = borderWidth;
                            right = size + borderWidth;
                        }
                        else {
                            left = w - size - borderWidth;
                            right = w - borderWidth;
                        }
                        break;
                    case TOP:
                    case BOTTOM:
                    case CENTER:
                        left = (w - size)/2;
                        right = (w + size)/2;
                        break;
                }
                switch (gravity){
                    case TOP_START:
                    case TOP_END:
                    case TOP:
                        top = borderWidth;
                        bottom = size + borderWidth;
                        break;
                    case BOTTOM:
                    case BOTTOM_START:
                    case BOTTOM_END:
                        top = h - size - borderWidth;
                        bottom = h - borderWidth;
                        break;
                    case END:
                    case START:
                    case CENTER:
                        top = (h - size)/2;
                        bottom = (h + size)/2;
                        break;
                }
                break;
            case HORIZONTAL_DETERMINATE:
            case HORIZONTAL_INDETERMINATE:
                switch (gravity){
                    case START:
                    case BOTTOM_START:
                    case TOP_START:
                        if(isRtl && !isRtlSupportDisabled){
                            left = w - size;
                            right = w;
                        }
                        else {
                            left = 0;
                            right = size;
                        }
                        break;
                    case END:
                    case BOTTOM_END:
                    case TOP_END:
                        if(isRtl && !isRtlSupportDisabled){
                            left = 0;
                            right = size;
                        }
                        else {
                            left = w - size;
                            right = w;
                        }
                        break;
                    case TOP:
                    case BOTTOM:
                    case CENTER:
                        left = (w - size)/2;
                        right = (w + size)/2;
                        break;
                }
                switch (gravity){
                    case TOP_START:
                    case TOP_END:
                    case TOP:
                        top = 0;
                        bottom = borderWidth;
                        break;
                    case BOTTOM:
                    case BOTTOM_START:
                    case BOTTOM_END:
                        top = h - borderWidth;
                        bottom = h;
                        break;
                    case END:
                    case START:
                    case CENTER:
                        top = (h - borderWidth)/2;
                        bottom = (h + borderWidth)/2;
                        break;
                }
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
