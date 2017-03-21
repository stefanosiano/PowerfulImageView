package com.stefanosiano.progressimageview.progress;

/**
 * Created by stefano on 17/03/17.
 */

public final class ProgressOptions {

    //options used by drawers
    private boolean isDeterminateAnimationEnabled;
    private int borderWidth;
    private float valuePercent;
    private int frontColor;
    private int backColor;
    private int indeterminateColor;
    private boolean drawWedge;

    //variables used to calculate bounds
    private int size;
    private int padding;
    private float sizePercent;
    private PivProgressGravity gravity;
    private final boolean isRtlSupportDisabled, isCircleBorderWidthFixed, isRtl;
    //bounds of the progress indicator
    private float left, top, right, bottom;

    public ProgressOptions(boolean isDeterminateAnimationEnabled, int borderWidth, int size, int padding, float sizePercent,
                           float valuePercent, int frontColor, int backColor, int indeterminateColor, int gravity, boolean rtl, boolean disableRtlSupport, boolean drawWedge) {
        this.isDeterminateAnimationEnabled = isDeterminateAnimationEnabled;
        this.borderWidth = borderWidth;
        this.isCircleBorderWidthFixed = borderWidth > 0;
        this.size = size;
        this.padding = padding;
        this.sizePercent = sizePercent;
        this.valuePercent = valuePercent;
        this.frontColor = frontColor;
        this.backColor = backColor;
        this.indeterminateColor = indeterminateColor;
        this.gravity = PivProgressGravity.fromValue(gravity);
        this.isRtl = rtl;
        this.isRtlSupportDisabled = disableRtlSupport;
        this.drawWedge = drawWedge;
    }

    public final void calculateBounds(int w, int h, PivProgressMode mode){
        int maxSize = w < h ? w : h;
        maxSize = maxSize - padding - padding;

        if(sizePercent >= 0){
            size = (int) (maxSize * (double) sizePercent / 100);
        }
        if(size > maxSize)
            size = maxSize;
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
                            left = w - size + borderWidth/2 - padding;
                            right = w - borderWidth/2 - padding;
                        }
                        else {
                            left = borderWidth/2 + padding;
                            right = size - borderWidth/2 + padding;
                        }
                        break;
                    case END:
                    case BOTTOM_END:
                    case TOP_END:
                        if(isRtl && !isRtlSupportDisabled){
                            left = borderWidth/2 + padding;
                            right = size - borderWidth/2 + padding;
                        }
                        else {
                            left = w - size + borderWidth/2 - padding;
                            right = w - borderWidth/2 - padding;
                        }
                        break;
                    case TOP:
                    case BOTTOM:
                    case CENTER:
                        left = (w - size + borderWidth) /2;
                        right = (w + size - borderWidth) /2;
                        break;
                }
                switch (gravity){
                    case TOP_START:
                    case TOP_END:
                    case TOP:
                        top = borderWidth/2 + padding;
                        bottom = size - borderWidth/2 + padding;
                        break;
                    case BOTTOM:
                    case BOTTOM_START:
                    case BOTTOM_END:
                        top = h - size + borderWidth/2 - padding;
                        bottom = h - borderWidth/2 - padding;
                        break;
                    case END:
                    case START:
                    case CENTER:
                        top = (h - size + borderWidth) /2;
                        bottom = (h + size - borderWidth) /2;
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
                            left = w - size - padding;
                            right = w - padding;
                        }
                        else {
                            left = padding;
                            right = size + padding;
                        }
                        break;
                    case END:
                    case BOTTOM_END:
                    case TOP_END:
                        if(isRtl && !isRtlSupportDisabled){
                            left = padding;
                            right = size + padding;
                        }
                        else {
                            left = w - size - padding;
                            right = w - padding;
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
                        top = padding;
                        bottom = borderWidth + padding;
                        break;
                    case BOTTOM:
                    case BOTTOM_START:
                    case BOTTOM_END:
                        top = h - borderWidth - padding;
                        bottom = h - padding;
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

    public final float getLeft() {
        return left;
    }

    public final float getTop() {
        return top;
    }

    public final float getRight() {
        return right;
    }

    public final float getBottom() {
        return bottom;
    }















    public final boolean isDeterminateAnimationEnabled() {
        return isDeterminateAnimationEnabled;
    }

    public final int getBorderWidth() {
        return borderWidth;
    }

    public final float getValuePercent() {
        return valuePercent;
    }

    public final int getFrontColor() {
        return frontColor;
    }

    public final int getBackColor() {
        return backColor;
    }

    public final int getIndeterminateColor() {
        return indeterminateColor;
    }

    public final boolean isDrawWedge() {
        return drawWedge;
    }
}
