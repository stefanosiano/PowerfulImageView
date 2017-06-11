package com.stefanosiano.powerfulimageview.blur;

/**
 * Class that helps managing the options that will be used to blur the image
 */

public final class BlurOptions {

    /** Strength of the blur */
    private int mRadius;

    /** Rate to downSample the image width and height, based on the view size */
    private float mDownSamplingRate;

    /** Whether the original bitmap should be kept in memory. If false, trying to blur a second time won't have effect */
    private boolean mKeepOriginal;

    /** Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs */
    private boolean mUseRsFallback;

    /**
     * Creates the object that will manage the blur options
     *
     * @param radius Sets the strength of the blur
     * @param downSamplingRate Rate to downSample the image width and height, based on the view size
     * @param keepOriginal Whether the original bitmap should be kept in memory. If false, trying to blur a second time won't have effect
     * @param useRsFallback Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs
     */
    public BlurOptions(int radius, float downSamplingRate, boolean keepOriginal, boolean useRsFallback) {
        this.mRadius = radius;
        this.mDownSamplingRate = downSamplingRate;
        this.mKeepOriginal = keepOriginal;
        this.mUseRsFallback = useRsFallback;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
    }
}
