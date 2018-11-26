package com.stefanosiano.powerfulimageview.blur

import android.os.Parcel
import android.os.Parcelable
import java.lang.ref.WeakReference


/**
 * Class that helps managing the options that will be used to blur the image
 */

class BlurOptions() : Parcelable {

    /** Rate to downSample the image width and height, based on the view size  */
    private var mDownSamplingRate: Float = 0f

    /** Whether the original bitmap should be blurred only once. If true, optimizations occur. If false, trying to blur a second time won't have effect  */
    private var mIsStaticBlur: Boolean = false

    /** Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs  */
    private var mUseRsFallback: Boolean = false

    /** Number of threads to use to blur the image (no more than available)  */
    private var mNumThreads: Int = 0

    /** Listener that will update the blur manager on changes, with a weak reference to be sure to not leak memory  */
    private var listener: WeakReference<BlurOptionsListener>? = null


    /**
     * Creates the object that will manage the blur options
     *
     * @param downSamplingRate Rate to downSample the image width and height, based on the view size. Cannot be less than 1
     * @param isStaticBlur Whether the original bitmap should be kept in memory. If false, trying to blur a second time won't have effect
     * @param useRsFallback Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs
     */
    constructor(downSamplingRate: Float, isStaticBlur: Boolean, useRsFallback: Boolean, numThreads: Int): this() {
        this.mDownSamplingRate = downSamplingRate.coerceAtLeast(1f)
        this.mIsStaticBlur = isStaticBlur
        this.mUseRsFallback = useRsFallback
        this.mNumThreads = numThreads
    }

    fun setOptions(other: BlurOptions) {
        this.mDownSamplingRate = other.mDownSamplingRate
        this.mIsStaticBlur = other.mIsStaticBlur
        this.mUseRsFallback = other.mUseRsFallback
        this.mNumThreads = other.mNumThreads
        this.listener = other.listener
    }

    interface BlurOptionsListener {
        fun onStaticBlurChanged()
        fun onDownsamplingRateChanged()
    }


    fun setListener(listener: BlurOptionsListener) { this.listener = WeakReference(listener) }

    /**
     * @return The rate to downSample the image width and height, based on the view size.
     * Bitmap is downsampled to be no more than the view size divided by this rate.
     */
    fun getDownSamplingRate(): Float = mDownSamplingRate


    /**
     * Sets the rate to downSample the image width and height, based on the view size.
     * If a value less than 1 is passed, downSampling rate 1 is used.
     * Bitmap is downsampled to be no more than the view size divided by this rate.
     */
    fun setDownSamplingRate(downSamplingRate: Float) {
        this.mDownSamplingRate = downSamplingRate.coerceAtLeast(1f)
        this.listener?.get()?.onDownsamplingRateChanged()
    }

    /** @return If the original bitmap should be blurred only once */
    fun isStaticBlur(): Boolean = mIsStaticBlur

    /**
     * @param isStaticBlur If the original bitmap should be blurred only once. If the image is already blurred
     * and false is passed to this function, original bitmap memory will be released.
     */
    fun setStaticBlur(isStaticBlur: Boolean) {
        this.mIsStaticBlur = isStaticBlur
        this.listener?.get()?.onStaticBlurChanged()
    }

    /** @return Whether the image should be blurred with the Java method equivalent to the renderscript one used. Used only if a renderscript mode is selected */
    fun isUseRsFallback() = mUseRsFallback

    /** @return Number of threads to use to blur the image */
    fun getNumThreads(): Int = mNumThreads

    /**
     * @param useRsFallback Whether the image should be blurred with the Java equivalent function
     * of renderscript one used. Used only if a renderscript mode is selected.
     */
    fun setUseRsFallback(useRsFallback: Boolean) { this.mUseRsFallback = useRsFallback }


    /**
     * Number of threads to use for blurring
     * @param numThreads if it's 0 or negative, available cores number will be used
     */
    fun setNumThreads(numThreads: Int) { this.mNumThreads = numThreads }



    constructor(parcel: Parcel) : this() {
        mDownSamplingRate = parcel.readFloat()
        mIsStaticBlur = parcel.readByte() != 0.toByte()
        mUseRsFallback = parcel.readByte() != 0.toByte()
        mNumThreads = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(mDownSamplingRate)
        parcel.writeByte(if (mIsStaticBlur) 1 else 0)
        parcel.writeByte(if (mUseRsFallback) 1 else 0)
        parcel.writeInt(mNumThreads)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BlurOptions> {
        override fun createFromParcel(parcel: Parcel): BlurOptions = BlurOptions(parcel)
        override fun newArray(size: Int): Array<BlurOptions?> = arrayOfNulls(size)
    }
}