package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.stefanosiano.powerful_libraries.imageview.blur.BlurOptions
import com.stefanosiano.powerful_libraries.imageview.blur.PivBlurMode
import com.stefanosiano.powerful_libraries.imageview.extensions.createBitmap
import com.stefanosiano.powerful_libraries.imageview.extensions.isVector
import java.lang.ref.WeakReference

/** Manager class for blurring. Used to manage and blur the image. */

@Suppress("TooManyFunctions")
internal class BlurManager(view: ImageView, blurOptions: BlurOptions) : BlurOptions.BlurOptionsListener {

    /** Drawable of the imageview to blur. */
    private var mDrawable: Drawable? = null

    /** Original bitmap, down-sampled if needed. */
    private var mOriginalBitmap: Bitmap? = null

    /** Last blurred bitmap. */
    private var mBlurredBitmap: Bitmap? = null

    /** Options to use to blur bitmap. */
    private var mBlurOptions: BlurOptions = blurOptions

    /** Mode to use to blur the image. */
    private var mMode: PivBlurMode = PivBlurMode.DISABLED

    /** Strength of the blur. */
    private var mRadius: Int = 0

    /** Whether the renderscript context is managed: if I added this view's context to the SharedBlurManager. */
    private var mIsRenderscriptManaged: Boolean = false

    /** Whether the bitmap has been already blurred. On static blur, it will only  blur once! */
    private var mIsAlreadyBlurred: Boolean = false

    /** Width of the view. Used to calculate the original bitmap. */
    private var mWidth: Int = 0

    /** Height of the view. Used to calculate the original bitmap. */
    private var mHeight: Int = 0

    /** Last width of the calculated bitmap. Used to calculate the original bitmap. */
    private var mLastSizeX: Int = 0

    /** Last height of the calculated bitmap. Used to calculate the original bitmap. */
    private var mLastSizeY: Int = 0

    /** Last radius used to blur the image. Used to avoid blurring twice again the same image with the same radius. */
    private var mLastRadius: Int = -1

    // Using a weakReference to be sure not to leak memory
    private var mView: WeakReference<ImageView> = WeakReference(view)

    // Algorithms
    private val mBox3x3BlurAlgorithm by lazy { Box3x3BlurAlgorithm() }
    private val mBox3x3RenderscriptBlurAlgorithm by lazy { Box3x3RenderscriptBlurAlgorithm() }
    private val mBox5x5BlurAlgorithm by lazy { Box5x5BlurAlgorithm() }
    private val mBox5x5RenderscriptBlurAlgorithm by lazy { Box5x5RenderscriptBlurAlgorithm() }
    private val mGaussian5x5BlurAlgorithm by lazy { Gaussian5x5BlurAlgorithm() }
    private val mGaussian5x5RenderscriptBlurAlgorithm by lazy { Gaussian5x5RenderscriptBlurAlgorithm() }
    private val mGaussian3x3BlurAlgorithm by lazy { Gaussian3x3BlurAlgorithm() }
    private val mGaussian3x3RenderscriptBlurAlgorithm by lazy { Gaussian3x3RenderscriptBlurAlgorithm() }
    private val mGaussianBlurAlgorithm by lazy { GaussianBlurAlgorithm() }
    private val mGaussianRenderscriptBlurAlgorithm by lazy { GaussianRenderscriptBlurAlgorithm() }
    private val mStackBlurAlgorithm by lazy { StackBlurAlgorithm() }
    private val mDummyBlurAlgorithm by lazy { DummyBlurAlgorithm() }

    /** Selected algorithm to blur the image. */
    private var mBlurAlgorithm: BlurAlgorithm = mDummyBlurAlgorithm

    /** Method that updates the [drawable] and bitmap to show. */
    fun changeDrawable(drawable: Drawable?) {
        if (drawable == mDrawable) return

        if (!shouldBlur(drawable, true)) return

        val mLastDrawable = mDrawable
        val lastOriginalBitmap = mOriginalBitmap
        this.mDrawable = drawable
        this.mOriginalBitmap = getOriginalBitmapFromDrawable(mLastDrawable, drawable)

        if (lastOriginalBitmap != mOriginalBitmap) {
            mIsAlreadyBlurred = false
            lastOriginalBitmap?.recycle()
            mLastRadius = -1
        }
    }

    /** Changes the [blurMode] and/or [radius] of the image. */
    fun changeMode(blurMode: PivBlurMode, radius: Int) {
        // If there's no change, I don't do anything
        if (blurMode == mMode && radius == mRadius) {
            return
        }

        removeContext(true)
        mMode = blurMode
        addContext(true)

        // Otherwise i need to blur the image again
        updateAlgorithms(blurMode)
        mLastRadius = -1
        mRadius = radius
    }

    /** Changes the [radius] (strength) of the blur method. */
    fun changeRadius(radius: Int) = changeMode(mMode, radius)

    /**
     * Blurs the image with the specified [radius], if not already blurred.
     * To get the image call [getLastBlurredBitmap].
     */
    fun blur(radius: Int) {
        mRadius = radius

        val origBitmap = mOriginalBitmap ?: return

        // If I already blurred the image with this radius, I won't do anything
        val blurredWithLastRadius = mLastRadius == radius && mBlurredBitmap != null
        // If I already blurred the image and it's a static blur, I won't have to blur anymore
        val blurredStatic = mIsAlreadyBlurred && mBlurOptions.isStaticBlur

        if (origBitmap.isRecycled || blurredWithLastRadius || blurredStatic) {
            return
        }

        this.mLastRadius = radius

        if (origBitmap != mBlurredBitmap) {
            mBlurredBitmap?.recycle()
        }

        var bitmap: Bitmap?
        addContext(false)

        try {
            bitmap = mBlurAlgorithm.blur(origBitmap, mRadius, mBlurOptions)
            mIsAlreadyBlurred = true
        } catch (e: RenderscriptException) {
            // Something wrong occurred with renderscript: fallback to java or nothing, based on option...

            // Changing mode to fallback one if enabled
            mMode = if (mBlurOptions.useRsFallback) mMode.fallbackMode else PivBlurMode.DISABLED

            Log.w(
                BlurManager::class.java.simpleName,
                "${e.message}\nFalling back to another blurring method: ${mMode.name}"
            )

            updateAlgorithms(mMode)

            try {
                bitmap = mBlurAlgorithm.blur(origBitmap, mRadius, mBlurOptions)
                mIsAlreadyBlurred = true
            } catch (e1: RenderscriptException) {
                // The second blur failed: we now return a null bitmap as it will be reverted to the original one
                e1.printStackTrace()
                bitmap = null
            }
        }

        removeContext(false)
        if (mBlurOptions.isStaticBlur) {
            mOriginalBitmap = bitmap ?: origBitmap
        }

        mBlurredBitmap = bitmap ?: mBlurredBitmap
    }

    /** Updates the saved width and height, used to calculate the blurred bitmap. */
    fun onSizeChanged(width: Int, height: Int, drawable: Drawable?) {
        this.mWidth = width
        this.mHeight = height
        if (drawable != null) {
            changeDrawable(drawable)
        }
    }

    /** Updates the algorithm used to blur the image, based on [blurMode]. */
    private fun updateAlgorithms(blurMode: PivBlurMode?) {
        val renderScript = SharedBlurManager.getRenderScriptContext()
        mMode = blurMode ?: PivBlurMode.DISABLED

        // If renderscript is null and the mode uses it, there was a problem getting it: let's use java or dummy
        if (mMode.usesRenderscript && renderScript == null) {
            return updateAlgorithms(mMode.getFallbackMode(mBlurOptions.useRsFallback))
        }

        addContext(false)

        mBlurAlgorithm = getBlurAlgorithmFromBlurMode()
        mBlurAlgorithm.setRenderscript(renderScript)
    }

    private fun getBlurAlgorithmFromBlurMode() = when (mMode) {
        PivBlurMode.STACK -> mStackBlurAlgorithm
        PivBlurMode.GAUSSIAN5X5_RS -> mGaussian5x5RenderscriptBlurAlgorithm
        PivBlurMode.GAUSSIAN5X5 -> mGaussian5x5BlurAlgorithm
        PivBlurMode.GAUSSIAN3X3_RS -> mGaussian3x3RenderscriptBlurAlgorithm
        PivBlurMode.GAUSSIAN3X3 -> mGaussian3x3BlurAlgorithm
        PivBlurMode.BOX5X5_RS -> mBox5x5RenderscriptBlurAlgorithm
        PivBlurMode.BOX5X5 -> mBox5x5BlurAlgorithm
        PivBlurMode.BOX3X3_RS -> mBox3x3RenderscriptBlurAlgorithm
        PivBlurMode.BOX3X3 -> mBox3x3BlurAlgorithm
        PivBlurMode.GAUSSIAN_RS -> mGaussianRenderscriptBlurAlgorithm
        PivBlurMode.GAUSSIAN -> mGaussianBlurAlgorithm
        PivBlurMode.DISABLED -> mDummyBlurAlgorithm
    }

    /** Check if the current options require the bitmap to be blurred. */
    fun shouldBlur(drawable: Drawable?, checkDrawable: Boolean): Boolean =
        mMode != PivBlurMode.DISABLED && (mLastRadius != mRadius || (checkDrawable && mDrawable != drawable))

    /** Returns the blurred bitmap. If any problem occurs, the original bitmap (nullable) will be returned. */
    fun getLastBlurredBitmap(): Bitmap? {
        blur(mRadius)
        return mBlurredBitmap ?: mOriginalBitmap
    }

    /** Returns the bitmap of the drawable, down-sampled if needed. */
    private fun getOriginalBitmapFromDrawable(mLastDrawable: Drawable?, drawable: Drawable?): Bitmap? {
        if (drawable == null || mWidth <= 0 || mHeight <= 0) {
            return null
        }

        // Bitmap size should not be bigger than the view size
        val ratio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
        var sizeX: Int = drawable.intrinsicWidth
        var sizeY: Int = drawable.intrinsicHeight
        val maxWidth = (mWidth.toFloat().coerceAtLeast(mHeight * ratio) / mBlurOptions.downSamplingRate).toInt()
        val maxHeight = (mHeight.toFloat().coerceAtLeast(mWidth / ratio) / mBlurOptions.downSamplingRate).toInt()

        val isTooWide = drawable.intrinsicWidth > maxWidth.coerceAtLeast(1)
        val isTooHigh = drawable.intrinsicHeight > maxHeight.coerceAtLeast(1)
        val isTooBig = isTooWide && isTooHigh

        if (isTooBig || drawable.isVector()) {
            sizeX = maxWidth
            sizeY = maxHeight
        }

        // CoerceAtLeast(1) let us know if the size is correct (it must be > 0)
        val sizeChanged = mLastSizeX == sizeX.coerceAtLeast(1) && mLastSizeY == sizeY.coerceAtLeast(1)
        val originalBitmapValid = mOriginalBitmap?.isRecycled == false && mLastDrawable === drawable

        // If i already decoded the bitmap i reuse it
        return if (!sizeChanged && originalBitmapValid) {
            mOriginalBitmap
        } else {
            try {
                mLastSizeX = sizeX
                mLastSizeY = sizeY
                drawable.createBitmap(sizeX, sizeY)
            } catch (e: IllegalArgumentException) {
                Log.e(BlurManager::class.java.simpleName, e.message ?: "")
                mOriginalBitmap
            }
        }
    }

    /**
     * Adds the context to the renderscript manager, if needed.
     * If the blur is static renderscript context is managed by the manager itself, to release it as soon as possible.
     * If the blur is not static renderscript context is managed by the view itself, to keep it as long as it needs.
     * [fromView] specifies if the function was called by the view.
     */
    fun addContext(fromView: Boolean) {
        if (mIsRenderscriptManaged || mView.get() == null) {
            return
        }

        val context = mView.get()?.context?.applicationContext ?: return
        if (mBlurOptions.isStaticBlur != fromView && mMode.usesRenderscript) {
            mIsRenderscriptManaged = true
            SharedBlurManager.addRenderscriptContext(context.applicationContext)
        }
    }

    /**
     * Removes the context from the renderscript manager, if needed.
     * If the blur is static renderscript context is managed by the manager itself, to release it as soon as possible.
     * If the blur is not static renderscript context is managed by the view itself, to keep it as long as it needs.
     * [fromView] specifies if the function was called by the view.
     */
    fun removeContext(fromView: Boolean) {
        if (!mIsRenderscriptManaged) {
            return
        }

        if (mBlurOptions.isStaticBlur != fromView && mMode.usesRenderscript) {
            mIsRenderscriptManaged = false
            SharedBlurManager.removeRenderscriptContext()
            updateAlgorithms(mMode)
        }
    }

    override fun onStaticBlurChanged() {
        // If staticBlur is true, i release original bitmap and swap it with the blurred one, if it exists
        if (mBlurOptions.isStaticBlur) {
            if (mBlurredBitmap != null && mBlurredBitmap != mOriginalBitmap) {
                mOriginalBitmap?.recycle()
                mOriginalBitmap = mBlurredBitmap
                mBlurredBitmap = null
            }
            removeContext(false)
        } else {
            addContext(true)
        }
    }

    override fun onDownSamplingRateChanged() {
        // If downSampling rate changes, i reload the bitmap and blur it
        changeDrawable(mDrawable)
        blur(mRadius)
        getLastBlurredBitmap()?.let {
            mView.get()?.setImageBitmap(it)
        }
    }

    /** Returns the selected mode used for blurring. */
    fun getBlurMode(): PivBlurMode = mMode

    /** Returns the options used for blurring. */
    fun getBlurOptions(): BlurOptions = mBlurOptions

    /** Returns the selected radius used for blurring. */
    fun getRadius(): Int = mRadius

    /**
     * Returns the original bitmap used to blur.
     * If static blur is enabled, this will be the same as the blurred one, since the original bitmap has been released.
     */
    fun getOriginalBitmap(): Bitmap? = mOriginalBitmap
}
