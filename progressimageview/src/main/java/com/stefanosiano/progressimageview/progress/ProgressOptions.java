package com.stefanosiano.progressimageview.progress;

/**
 * Created by stefano on 17/03/17.
 */

public class ProgressOptions {

    boolean isDeterminateAnimationEnabled;
    int circleBorderWidth;
    int circleSize;
    float circleSizePercent;
    float valuePercent;
    int frontColor;
    int backColor;
    int indeterminateColor;
    PivProgressMode mode;

    private final boolean isCircleBorderWidthFixed;

    public ProgressOptions(boolean isDeterminateAnimationEnabled, int circleBorderWidth, int circleSize, float circleSizePercent, float valuePercent, int frontColor, int backColor, int indeterminateColor, int mode) {
        this.isDeterminateAnimationEnabled = isDeterminateAnimationEnabled;
        this.circleBorderWidth = circleBorderWidth;
        this.isCircleBorderWidthFixed = circleBorderWidth > 0;
        this.circleSize = circleSize;
        this.circleSizePercent = circleSizePercent;
        this.valuePercent = valuePercent;
        this.frontColor = frontColor;
        this.backColor = backColor;
        this.indeterminateColor = indeterminateColor;
        this.mode = PivProgressMode.fromValue(mode);

        calculateValues(0, 0);
    }

    public void calculateValues(int w, int h){
        if(circleSizePercent >= 0){
            circleSize = (int) (w * (double)circleSizePercent);
        }
        if(!isCircleBorderWidthFixed){
            circleBorderWidth = Math.round(circleSize/10);
        }
        if(circleBorderWidth < 1)
            circleBorderWidth = 1;
    }

    public int getProgressAngle(){
        return (int) (valuePercent * 3.6f);
    }
    public boolean isDeterminateAnimationEnabled() {
        return isDeterminateAnimationEnabled;
    }

    public void setDeterminateAnimationEnabled(boolean determinateAnimationEnabled) {
        isDeterminateAnimationEnabled = determinateAnimationEnabled;
    }

    public int getCircleBorderWidth() {
        return circleBorderWidth;
    }

    public void setCircleBorderWidth(int circleBorderWidth) {
        this.circleBorderWidth = circleBorderWidth;
    }

    public int getCircleSize() {
        return circleSize;
    }

    public void setCircleSize(int circleSize) {
        this.circleSize = circleSize;
    }

    public float getCircleSizePercent() {
        return circleSizePercent;
    }

    public void setCircleSizePercent(float circleSizePercent) {
        this.circleSizePercent = circleSizePercent;
    }

    public float getValuePercent() {
        return valuePercent;
    }

    public void setValuePercent(float valuePercent) {
        this.valuePercent = valuePercent;
    }

    public int getFrontColor() {
        return frontColor;
    }

    public void setFrontColor(int frontColor) {
        this.frontColor = frontColor;
    }

    public int getBackColor() {
        return backColor;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public int getIndeterminateColor() {
        return indeterminateColor;
    }

    public void setIndeterminateColor(int indeterminateColor) {
        this.indeterminateColor = indeterminateColor;
    }

    public PivProgressMode getMode() {
        return mode;
    }

    public void setMode(PivProgressMode mode) {
        this.mode = mode;
    }
}
