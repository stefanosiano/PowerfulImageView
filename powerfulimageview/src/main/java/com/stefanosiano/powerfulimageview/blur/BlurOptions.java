package com.stefanosiano.powerfulimageview.blur;

import java.lang.ref.WeakReference;

/**
 * Class that helps managing the options that will be used to blur the image
 */

public final class BlurOptions {

    /** Rate to downSample the image width and height, based on the view size */
    private float mDownSamplingRate;

    /** Whether the original bitmap should be kept in memory. If false, trying to blur a second time won't have effect */
    private boolean mKeepOriginal;

    /** Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs */
    private boolean mUseRsFallback;

    /** Listener that will update the blur manager on changes, with a weak reference to be sure to not leak memory */
    private WeakReference<BlurOptionsListener> listener;

    /**
     * Creates the object that will manage the blur options
     *
     * @param downSamplingRate Rate to downSample the image width and height, based on the view size
     * @param keepOriginal Whether the original bitmap should be kept in memory. If false, trying to blur a second time won't have effect
     * @param useRsFallback Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs
     */
    public BlurOptions(float downSamplingRate, boolean keepOriginal, boolean useRsFallback) {
        this.mDownSamplingRate = downSamplingRate;
        this.mKeepOriginal = keepOriginal;
        this.mUseRsFallback = useRsFallback;
    }

    public interface BlurOptionsListener{
        void onKeepOriginalChanged();
        void onDownsamplingRateChanged();
    }

    public void setListener(BlurOptionsListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    public float getDownSamplingRate() {
        return mDownSamplingRate;
    }

    public void setDownSamplingRate(float downSamplingRate) {
        this.mDownSamplingRate = downSamplingRate;
    }

    public boolean isKeepOriginal() {
        return mKeepOriginal;
    }

    public void setKeepOriginal(boolean keepOriginal) {
        this.mKeepOriginal = keepOriginal;
    }

    public boolean isUseRsFallback() {
        return mUseRsFallback;
    }

    public void setUseRsFallback(boolean useRsFallback) {
        this.mUseRsFallback = useRsFallback;
    }


}
