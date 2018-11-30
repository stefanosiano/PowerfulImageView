package com.stefanosiano.powerfullibraries.imageview.blur;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.ref.WeakReference;

/**
 * Class that helps managing the options that will be used to blur the image
 */

public final class BlurOptions implements Parcelable {

    /** Rate to downSample the image width and height, based on the view size */
    private float mDownSamplingRate;

    /** Whether the original bitmap should be blurred only once. If true, optimizations occur. If false, trying to blur a second time won't have effect */
    private boolean mIsStaticBlur;

    /** Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs */
    private boolean mUseRsFallback;

    /** Number of threads to use to blur the image (no more than available) */
    private int mNumThreads;

    /** Listener that will update the blur manager on changes, with a weak reference to be sure to not leak memory */
    private WeakReference<BlurOptionsListener> listener;

    /**
     * Creates the object that will manage the blur options
     *
     * @param downSamplingRate Rate to downSample the image width and height, based on the view size. Cannot be less than 1
     * @param isStaticBlur Whether the original bitmap should be kept in memory. If false, trying to blur a second time won't have effect
     * @param useRsFallback Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs
     */
    public BlurOptions(float downSamplingRate, boolean isStaticBlur, boolean useRsFallback, int numThreads) {
        this.mDownSamplingRate = downSamplingRate;
        if(mDownSamplingRate < 1) mDownSamplingRate = 1;
        this.mIsStaticBlur = isStaticBlur;
        this.mUseRsFallback = useRsFallback;
        this.mNumThreads = numThreads;
    }

    public void setOptions(BlurOptions other) {
        this.mDownSamplingRate = other.mDownSamplingRate;
        this.mIsStaticBlur = other.mIsStaticBlur;
        this.mUseRsFallback = other.mUseRsFallback;
        this.mNumThreads = other.mNumThreads;
        this.listener = other.listener;
    }

    public interface BlurOptionsListener{
        void onStaticBlurChanged();
        void onDownsamplingRateChanged();
    }

    public void setListener(BlurOptionsListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    /**
     * @return The rate to downSample the image width and height, based on the view size.
     * Bitmap is downsampled to be no more than the view size divided by this rate.
     */
    public float getDownSamplingRate() {
        return mDownSamplingRate;
    }


    /**
     * Sets the rate to downSample the image width and height, based on the view size.
     * If a value less than 1 is passed, downSampling rate 1 is used.
     * Bitmap is downsampled to be no more than the view size divided by this rate.
     */
    public void setDownSamplingRate(float downSamplingRate) {
        this.mDownSamplingRate = downSamplingRate;
        if(mDownSamplingRate < 1) mDownSamplingRate = 1;
        if(this.listener.get() != null)
            this.listener.get().onDownsamplingRateChanged();
    }

    /**
     * @return If the original bitmap should be blurred only once
     */
    public boolean isStaticBlur() {
        return mIsStaticBlur;
    }

    /**
     * @param isStaticBlur If the original bitmap should be blurred only once. If the image is already blurred
     *                     and false is passed to this function, original bitmap memory will be released.
     */
    public void setStaticBlur(boolean isStaticBlur) {
        this.mIsStaticBlur = isStaticBlur;
        if(this.listener.get() != null)
            this.listener.get().onStaticBlurChanged();
    }

    /**
     * @return Whether the image should be blurred with the Java equivalent function of renderscript
     *          one used. Used only if a renderscript mode is selected.
     */
    public boolean isUseRsFallback() {
        return mUseRsFallback;
    }

    /** @return Number of threads to use to blur the image */
    public int getNumThreads() {
        return mNumThreads;
    }

    /**
     * @param useRsFallback Whether the image should be blurred with the Java equivalent function
     *                      of renderscript one used. Used only if a renderscript mode is selected.
     */
    public void setUseRsFallback(boolean useRsFallback) {
        this.mUseRsFallback = useRsFallback;
    }


    /**
     * Number of threads to use for blurring
     * @param numThreads if it's 0 or negative, available cores number will be used
     */
    public void setNumThreads(int numThreads) {
        this.mNumThreads = numThreads;
    }





    public static final Creator<BlurOptions> CREATOR = new Creator<BlurOptions>() {
        @Override
        public BlurOptions createFromParcel(Parcel in) {
            return new BlurOptions(in);
        }

        @Override
        public BlurOptions[] newArray(int size) {
            return new BlurOptions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(mDownSamplingRate);
        dest.writeByte((byte) (mIsStaticBlur ? 1 : 0));
        dest.writeByte((byte) (mUseRsFallback ? 1 : 0));
        dest.writeInt(mNumThreads);
    }

    protected BlurOptions(Parcel in) {
        mDownSamplingRate = in.readFloat();
        mIsStaticBlur = in.readByte() != 0;
        mUseRsFallback = in.readByte() != 0;
        mNumThreads = in.readInt();
    }
}
