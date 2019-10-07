package com.stefanosiano.powerful_libraries.imageview.blur

import android.os.Parcel
import android.os.Parcelable
import java.lang.ref.WeakReference


/**
 * Class that helps managing the options that will be used to blur the image
 */

class BlurOptions() : Parcelable {

    /** Rate to downSample the image width and height, so that bitmap is no more than the view size divided by this rate.
     * If a value less than 1 is passed, downSampling rate 1 is used. */
    var downSamplingRate: Float = 0f
        set(value) { field = value.coerceAtLeast(1f); if(isInitialized) this.listener?.get()?.onDownsamplingRateChanged() }

    /** Whether the original bitmap should be blurred only once. If true, optimizations occur. If false, trying to blur a second time won't have effect  */
    var isStaticBlur: Boolean = false
        set(value) { field = value; if(isInitialized) listener?.get()?.onStaticBlurChanged() }

    /** Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs. Used only if a renderscript mode is selected */
    var useRsFallback: Boolean = false

    /** Number of threads to use to blur the image (no more than available). If 0 or negative, available cores number will be used  */
    var numThreads: Int = 0

    /** Listener that will update the blur manager on changes, with a weak reference to be sure to not leak memory  */
    private var listener: WeakReference<BlurOptionsListener>? = null

    /** Flag to check if the object's constructor was called */
    private var isInitialized = false


    /**
     * Creates the object that will manage the blur options
     *
     * @param downSamplingRate Rate to downSample the image width and height, based on the view size. Cannot be less than 1
     * @param isStaticBlur Whether the original bitmap should be kept in memory. If false, trying to blur a second time won't have effect
     * @param useRsFallback Whether the image should be blurred with a java equivalent of the renderscript algorithm if an error occurs
     */
    constructor(downSamplingRate: Float, isStaticBlur: Boolean, useRsFallback: Boolean, numThreads: Int): this() {
        this.downSamplingRate = downSamplingRate.coerceAtLeast(1f)
        this.isStaticBlur = isStaticBlur
        this.useRsFallback = useRsFallback
        this.numThreads = numThreads
        this.isInitialized = true
    }

    fun setOptions(other: BlurOptions) {
        this.downSamplingRate = other.downSamplingRate
        this.isStaticBlur = other.isStaticBlur
        this.useRsFallback = other.useRsFallback
        this.numThreads = other.numThreads
        this.listener = other.listener
        this.isInitialized = true
    }

    interface BlurOptionsListener {
        fun onStaticBlurChanged()
        fun onDownsamplingRateChanged()
    }

    internal fun setListener(listener: BlurOptionsListener) { this.listener = WeakReference(listener) }


    constructor(parcel: Parcel) : this() {
        downSamplingRate = parcel.readFloat()
        isStaticBlur = parcel.readByte() != 0.toByte()
        useRsFallback = parcel.readByte() != 0.toByte()
        numThreads = parcel.readInt()
        isInitialized = true
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(downSamplingRate)
        parcel.writeByte(if (isStaticBlur) 1 else 0)
        parcel.writeByte(if (useRsFallback) 1 else 0)
        parcel.writeInt(numThreads)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BlurOptions> {
        override fun createFromParcel(parcel: Parcel): BlurOptions = BlurOptions(parcel)
        override fun newArray(size: Int): Array<BlurOptions?> = arrayOfNulls(size)
    }
}