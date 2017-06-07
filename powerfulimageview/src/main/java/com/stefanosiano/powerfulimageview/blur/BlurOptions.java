package com.stefanosiano.powerfulimageview.blur;

/**
 * Created by stefano on 19/05/17.
 */

public class BlurOptions {

    private int mRadius;

    public BlurOptions(int radius) {
        this.mRadius = radius;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }
}
