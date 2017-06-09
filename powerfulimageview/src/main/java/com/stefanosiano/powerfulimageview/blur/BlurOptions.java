package com.stefanosiano.powerfulimageview.blur;

/**
 * Created by stefano on 19/05/17.
 */

public final class BlurOptions {

    private int mRadius;
    private int mDownSamplingRate;
    private boolean mKeepOriginal;
    private boolean mAnimate;

    /** Whether the algorithm should use a fallback system on fail for Renderscript -> Java equivalent */
    private boolean mUseRsFallback;

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
