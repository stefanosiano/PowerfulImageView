package com.stefanosiano.powerfulimageview.blur.algorithms


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView

import com.stefanosiano.powerfulimageview.blur.BlurOptions
import com.stefanosiano.powerfulimageview.blur.PivBlurMode

import java.lang.ref.WeakReference

import androidx.renderscript.RenderScript
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat


/**
 * Manager class for blurring. Used to manage and blur the image.
 */

class BlurManager
/**
 * Manager class for blur. Used to initialize and blur the image with the right algorithm.
 *
 * @param view View to show the blurred image into
 * @param blurOptions Options of the blur
 */(view: ImageView, blurOptions: BlurOptions) : BlurOptions.BlurOptionsListener {

    /** Drawable of the imageview to blur  */
    private var mDrawable: Drawable? = null

    /** Original bitmap, downsampled if needed  */
    private var mOriginalBitmap: Bitmap? = null

    /** Last blurred bitmap  */
    private var mBlurredBitmap: Bitmap? = null

    /** Options to use to blur bitmap  */
    private var mBlurOptions: BlurOptions = blurOptions

    /** Mode to use to blur the image  */
    private var mMode: PivBlurMode = PivBlurMode.DISABLED

    /** Strength of the blur  */
    private var mRadius: Int = 0

    /** Whether the renderscript context is managed: if I added this view's context to the SharedBlurManager  */
    private var mIsRenderscriptManaged: Boolean = false

    /** Whether the bitmap has been already blurred. On static blur, it will only  blur once!  */
    private var mIsAlreadyBlurred: Boolean = false

    /** Width of the view. Used to calculate the original bitmap  */
    private var mWidth: Int = 0

    /** Height of the view. Used to calculate the original bitmap  */
    private var mHeight: Int = 0

    /** Last width of the calculated bitmap. Used to calculate the original bitmap  */
    private var mLastSizeX: Int = 0

    /** Last height of the calculated bitmap. Used to calculate the original bitmap  */
    private var mLastSizeY: Int = 0

    /** Last radius used to blur the image. Used to avoid blurring twice again the same image with the same radius  */
    private var mLastRadius: Int = -1

    //Using a weakRefence to be sure to not leak memory
    private var mView: WeakReference<ImageView> = WeakReference(view)

    //Algorithms
    private var mBox3x3BlurAlgorithm: Box3x3BlurAlgorithm? = null
    private var mBox3x3RenderscriptBlurAlgorithm: Box3x3RenderscriptBlurAlgorithm? = null
    private var mBox5x5BlurAlgorithm: Box5x5BlurAlgorithm? = null
    private var mBox5x5RenderscriptBlurAlgorithm: Box5x5RenderscriptBlurAlgorithm? = null
    private var mGaussian5x5BlurAlgorithm: Gaussian5x5BlurAlgorithm? = null
    private var mGaussian5x5RenderscriptBlurAlgorithm: Gaussian5x5RenderscriptBlurAlgorithm? = null
    private var mGaussian3x3BlurAlgorithm: Gaussian3x3BlurAlgorithm? = null
    private var mGaussian3x3RenderscriptBlurAlgorithm: Gaussian3x3RenderscriptBlurAlgorithm? = null
    private var mGaussianBlurAlgorithm: GaussianBlurAlgorithm? = null
    private var mGaussianRenderscriptBlurAlgorithm: GaussianRenderscriptBlurAlgorithm? = null
    private var mStackBlurAlgorithm: StackBlurAlgorithm? = null
    private var mStackRenderscriptBlurAlgorithm: StackRenderscriptBlurAlgorithm? = null
    private var mDummyBlurAlgorithm: DummyBlurAlgorithm? = null

    /** Selected algorithm to blur the image  */
    private var mBlurAlgorithm: BlurAlgorithm = DummyBlurAlgorithm()


    /**
     * Method that updates the drawable and bitmap to show
     *
     * @param drawable drawable to show
     */
    fun changeDrawable(drawable: Drawable?) {
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

    /**
     * Changes the blur mode of the image.
     *
     * @param blurMode mode to use to blur the image
     * @param radius strength of the image
     */
    fun changeMode(blurMode: PivBlurMode, radius: Int) {

        //If there's no change, I don't do anything
        if (blurMode == mMode && radius == mRadius)
            return

        removeContext(true)
        mMode = blurMode
        addContext(true)

        //otherwise i need to blur the image again
        updateAlgorithms(blurMode)
        mLastRadius = -1
        mRadius = radius
    }

    /**
     * Changes the blur mode of the image.
     *
     * @param radius strength of the image
     */
    fun changeRadius(radius: Int) {
        changeMode(mMode, radius)
    }


    /**
     * Blurs the image, if not already blurred.
     * To get the image call getLastBlurredBitmap()!
     *
     * @param radius Strength of the blurring
     */
    fun blur(radius: Int) {
        mRadius = radius

        val origBitmap = mOriginalBitmap ?: return

        if (origBitmap.isRecycled) return

        //if I already blurred the image with this radius, I don't do anything
        if (mLastRadius == radius && mBlurredBitmap != null) return


        if (mIsAlreadyBlurred && mBlurOptions.isStaticBlur) return

        this.mLastRadius = radius

        if (mBlurredBitmap != null && origBitmap != mBlurredBitmap)
            mBlurredBitmap?.recycle()

        var bitmap: Bitmap?
        addContext(false)

        try {
            bitmap = if (radius == 0) origBitmap
            else mBlurAlgorithm.blur(origBitmap, mRadius, mBlurOptions)
            mIsAlreadyBlurred = true

        } catch (e: RenderscriptException) {
            //Something wrong occurred with renderscript: fallback to java or nothing, based on option...

            //changing mode to fallback one if enabled
            mMode = if (mBlurOptions.isUseRsFallback) mMode.fallbackMode else PivBlurMode.DISABLED

            Log.w(BlurManager::class.java.simpleName, e.localizedMessage + "\nFalling back to another blurring method: " + mMode.name)

            updateAlgorithms(mMode)

            try {
                bitmap = mBlurAlgorithm.blur(origBitmap, mRadius, mBlurOptions)
                mIsAlreadyBlurred = true
            }
            catch (e1: RenderscriptException) { bitmap = null }

        }

        removeContext(false)
        if (mBlurOptions.isStaticBlur)
            mOriginalBitmap = bitmap ?: origBitmap

        mBlurredBitmap = bitmap ?: mBlurredBitmap

    }


    /** Updates the saved width and height, used to calculate the blurred bitmap  */
    fun onSizeChanged(width: Int, height: Int, drawable: Drawable?) {
        this.mWidth = width
        this.mHeight = height
        if (drawable != null)
            changeDrawable(drawable)
    }


    /**
     * Updates the algorithm used to blur the image
     *
     * @param blurMode Algorithm to use
     */
    private fun updateAlgorithms(blurMode: PivBlurMode) {
        val renderScript = SharedBlurManager.getRenderScriptContext()
        mMode = blurMode

        //if renderscript is null and the mode uses it, there was a problem getting it: let's use java or dummy
        if(mMode.isUsesRenderscript) renderScript ?: return updateAlgorithms(if (mBlurOptions.isUseRsFallback) blurMode.fallbackMode else PivBlurMode.DISABLED)

        addContext(false)

        mBlurAlgorithm = when (blurMode) {
            PivBlurMode.STACK_RS -> mStackRenderscriptBlurAlgorithm ?: StackRenderscriptBlurAlgorithm()
            PivBlurMode.STACK -> mStackBlurAlgorithm ?: StackBlurAlgorithm()
            PivBlurMode.GAUSSIAN5X5_RS -> mGaussian5x5RenderscriptBlurAlgorithm ?: Gaussian5x5RenderscriptBlurAlgorithm()
            PivBlurMode.GAUSSIAN5X5 -> mGaussian5x5BlurAlgorithm ?: Gaussian5x5BlurAlgorithm()
            PivBlurMode.GAUSSIAN3X3_RS -> mGaussian3x3RenderscriptBlurAlgorithm ?: Gaussian3x3RenderscriptBlurAlgorithm()
            PivBlurMode.GAUSSIAN3X3 -> mGaussian3x3BlurAlgorithm ?: Gaussian3x3BlurAlgorithm()
            PivBlurMode.BOX5X5_RS -> mBox5x5RenderscriptBlurAlgorithm ?: Box5x5RenderscriptBlurAlgorithm()
            PivBlurMode.BOX5X5 -> mBox5x5BlurAlgorithm ?: Box5x5BlurAlgorithm()
            PivBlurMode.BOX3X3_RS -> mBox3x3RenderscriptBlurAlgorithm ?: Box3x3RenderscriptBlurAlgorithm()
            PivBlurMode.BOX3X3 -> mBox3x3BlurAlgorithm ?: Box3x3BlurAlgorithm()
            PivBlurMode.GAUSSIAN_RS -> mGaussianRenderscriptBlurAlgorithm ?: GaussianRenderscriptBlurAlgorithm()
            PivBlurMode.GAUSSIAN -> mGaussianBlurAlgorithm ?: GaussianBlurAlgorithm()
            PivBlurMode.DISABLED -> mDummyBlurAlgorithm ?: DummyBlurAlgorithm()
            else -> mDummyBlurAlgorithm ?: DummyBlurAlgorithm()
        }
        mBlurAlgorithm.setRenderscript(renderScript)
    }

    /**
     * Check if the current options require the bitmap to be blurred
     *
     * @return True if the bitmap should be blurred, false otherwise
     */
    fun shouldBlur(drawable: Drawable, checkDrawable: Boolean): Boolean = mMode != PivBlurMode.DISABLED && (mLastRadius != mRadius || checkDrawable && mDrawable != drawable)

    /** @return The blurred bitmap. If any problem occurs, the original bitmap (nullable) will be returned. */
    fun getLastBlurredBitmap(): Bitmap? {
        blur(mRadius)
        return mBlurredBitmap ?: mOriginalBitmap
    }

    /** @return Returns the bitmap of the drawable, downsampled if needed */
    private fun getOriginalBitmapFromDrawable(mLastDrawable: Drawable?, drawable: Drawable?): Bitmap? {
        val bitmap: Bitmap

        if (drawable == null || mWidth <= 0 || mHeight <= 0)
            return null

        //bitmap size should not be bigger than the view size
        val ratio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
        var sizeX: Int
        var sizeY: Int
        val maxWidth = (Math.max(mWidth.toFloat(), mHeight * ratio) / mBlurOptions.downSamplingRate).toInt()
        val maxHeight = (Math.max(mHeight.toFloat(), mWidth / ratio) / mBlurOptions.downSamplingRate).toInt()

        if (drawable.intrinsicWidth > maxWidth && maxWidth > 0 && drawable.intrinsicHeight > maxHeight && maxHeight > 0) {
            sizeX = maxWidth
            sizeY = maxHeight
        } else {
            sizeX = drawable.intrinsicWidth
            sizeY = drawable.intrinsicHeight
        }

        //vector drawables should always display at max resolution
        if (drawable.javaClass.name == "android.graphics.drawable.VectorDrawable" || drawable is VectorDrawableCompat) {
            sizeX = maxWidth
            sizeY = maxHeight
        }

        //if i already decoded the bitmap i reuse it
        if (sizeX > 0 && sizeY > 0 && mOriginalBitmap != null && mOriginalBitmap?.isRecycled == false && mLastSizeX == sizeX && mLastSizeY == sizeY && mLastDrawable === drawable)
            return mOriginalBitmap

        mLastSizeX = sizeX
        mLastSizeY = sizeY


        try {

            bitmap = when {
                drawable is BitmapDrawable -> Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888)
                drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0 -> Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
                drawable is ColorDrawable -> Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                else -> Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888)
            }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap

        } catch (e: Exception) {
            e.printStackTrace()
            return mOriginalBitmap
        }

    }


    /**
     * Adds the context to the renderscript manager, if needed.
     * If the blur is static renderscript context is managed by the manager itself, to release it as soon as possible.
     * If the blur is not static renderscript context is managed by the view itself, to keep it as long as it needs.
     *
     * @param fromView If the function was called by the view
     */
    fun addContext(fromView: Boolean) {
        if (mIsRenderscriptManaged || mView.get() == null)
            return

        val context = mView.get()?.context?.applicationContext ?: return
        if (mBlurOptions.isStaticBlur != fromView) {
            if (mMode.isUsesRenderscript) {
                mIsRenderscriptManaged = true
                SharedBlurManager.addRenderscriptContext(context.applicationContext)
            }
        }
    }

    /**
     * Removes the context from the renderscript manager, if needed.
     * If the blur is static renderscript context is managed by the manager itself, to release it as soon as possible.
     * If the blur is not static renderscript context is managed by the view itself, to keep it as long as it needs.
     *
     * @param fromView If the function was called by the view
     */
    fun removeContext(fromView: Boolean) {
        if (!mIsRenderscriptManaged)
            return

        if (mBlurOptions.isStaticBlur != fromView) {
            if(mMode.isUsesRenderscript) {
                mIsRenderscriptManaged = false
                SharedBlurManager.removeRenderscriptContext()
                updateAlgorithms(mMode)
            }
        }
    }

    override fun onStaticBlurChanged() {
        //If staticBlur is true, i release original bitmap and swap it with the blurred one, if it exists
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

    override fun onDownsamplingRateChanged() {
        //if downSampling rate changes, i reload the bitmap and blur it
        changeDrawable(mDrawable)
        blur(mRadius)
        if (mView.get() != null) {
            val bitmap = getLastBlurredBitmap()
            if (bitmap != null)
                mView.get()!!.setImageBitmap(getLastBlurredBitmap())
        }
    }


    /** Returns the selected mode used for blurring  */
    fun getBlurMode(): PivBlurMode {
        return mMode
    }

    /** Returns the options used for blurring  */
    fun getBlurOptions(): BlurOptions {
        return mBlurOptions
    }

    /** Returns the selected radius used for blurring  */
    fun getRadius(): Int {
        return mRadius
    }

    /**
     * @return The original bitmap used to blur. If static blur option is enabled, this will be the
     * same as the blurred one, since the original bitmap has been released.
     */
    fun getOriginalBitmap(): Bitmap? = mOriginalBitmap

    /** Saves state into a bundle.  */
    fun saveInstanceState(): Bundle {
        val bundle = Bundle()
        bundle.putParcelable("blur_options", mBlurOptions)
        bundle.putInt("blur_mode", mMode.value)
        bundle.putInt("blur_radius", mRadius)
        return bundle
    }

    /** Restores state from a bundle.  */
    fun restoreInstanceState(state: Bundle?) {
        if (state == null)
            return

        mBlurOptions.setOptions(state.getParcelable<Parcelable>("blur_options") as BlurOptions)
        val blurMode = PivBlurMode.fromValue(state.getInt("blur_mode"))
        mWidth = state.getInt("blur_width")
        mHeight = state.getInt("blur_height")
        mRadius = state.getInt("blur_radius")
        mLastRadius = -1
        changeMode(blurMode, mRadius)
    }

}