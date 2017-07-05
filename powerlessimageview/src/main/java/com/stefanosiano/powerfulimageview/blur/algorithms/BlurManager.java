package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.util.Log;
import android.widget.ImageView;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;
import com.stefanosiano.powerfulimageview.blur.PivBlurMode;

import java.lang.ref.WeakReference;

/**
 * Manager class for blurring. Used to manage and blur the image.
 */

public final class BlurManager implements BlurOptions.BlurOptionsListener {

    /** Drawable of the imageview to blur */
    private Drawable mDrawable;

    /** Original bitmap, downsampled if needed */
    private Bitmap mOriginalBitmap;

    /** Last blurred bitmap */
    private Bitmap mBlurredBitmap;

    /** Options to use to blur bitmap */
    private BlurOptions mBlurOptions;

    /** Mode to use to blur the image */
    private PivBlurMode mMode;

    /** Strength of the blur */
    private int mRadius;

    /** Whether the renderscript context is managed: if I added this view's context to the SharedBlurManager */
    private boolean mIsRenderscriptManaged;

    /** Whether the bitmap has been already blurred. On static blur, it will only  blur once! */
    private boolean mIsAlreadyBlurred;

    //Algorithms
    private Box3x3BlurAlgorithm mBox3x3BlurAlgorithm;
    private Box3x3RenderscriptBlurAlgorithm mBox3x3RenderscriptBlurAlgorithm;
    private Box5x5BlurAlgorithm mBox5x5BlurAlgorithm;
    private Box5x5RenderscriptBlurAlgorithm mBox5x5RenderscriptBlurAlgorithm;
    private Gaussian5x5BlurAlgorithm mGaussian5x5BlurAlgorithm;
    private Gaussian5x5RenderscriptBlurAlgorithm mGaussian5x5RenderscriptBlurAlgorithm;
    private Gaussian3x3BlurAlgorithm mGaussian3x3BlurAlgorithm;
    private Gaussian3x3RenderscriptBlurAlgorithm mGaussian3x3RenderscriptBlurAlgorithm;
    private GaussianBlurAlgorithm mGaussianBlurAlgorithm;
    private GaussianRenderscriptBlurAlgorithm mGaussianRenderscriptBlurAlgorithm;
    private StackBlurAlgorithm mStackBlurAlgorithm;
    private StackRenderscriptBlurAlgorithm mStackRenderscriptBlurAlgorithm;
    private DummyBlurAlgorithm mDummyBlurAlgorithm;

    /** Selected algorithm to blur the image */
    private BlurAlgorithm mBlurAlgorithm;

    /** Width of the view. Used to calculate the original bitmap */
    private int mWidth;

    /** Height of the view. Used to calculate the original bitmap */
    private int mHeight;

    /** Last width of the calculated bitmap. Used to calculate the original bitmap */
    private int mLastSizeX;

    /** Last height of the calculated bitmap. Used to calculate the original bitmap */
    private int mLastSizeY;

    /** Last radius used to blur the image. Used to avoid blurring twice again the same image with the same radius */
    private int mLastRadius;

    //Using a weakRefence to be sure to not leak memory
    private final WeakReference<ImageView> mView;

    /**
     * Manager class for blur. Used to initialize and blur the image with the right algorithm.
     *
     * @param view View to show the blurred image into
     * @param blurOptions Options of the blur
     */
    public BlurManager(ImageView view, final BlurOptions blurOptions){
        mView = new WeakReference<>(view);
        mBlurOptions = blurOptions;
        mMode = PivBlurMode.DISABLED;
        mLastRadius = -1;
        mRadius = 0;
        mWidth = 0;
        mHeight = 0;
        mLastSizeX = 0;
        mLastSizeY = 0;
        mIsRenderscriptManaged = false;
        mIsAlreadyBlurred = false;
    }

    /**
     * Method that updates the drawable and bitmap to show
     *
     * @param drawable drawable to show
     */
    public void changeDrawable(Drawable drawable) {
        Drawable mLastDrawable = mDrawable;
        Bitmap lastOriginalBitmap = mOriginalBitmap;
        this.mDrawable = drawable;
        this.mOriginalBitmap = getOriginalBitmapFromDrawable(mLastDrawable, drawable);

        if(lastOriginalBitmap != mOriginalBitmap) {
            mIsAlreadyBlurred = false;
            if(lastOriginalBitmap != null)
                lastOriginalBitmap.recycle();
            mLastRadius = -1;
        }
    }

    /**
     * Changes the blur mode of the image.
     *
     * @param blurMode mode to use to blur the image
     * @param radius strength of the image
     */
    public final void changeMode(PivBlurMode blurMode, int radius){

        //If there's no change, I don't do anything
        if(blurMode == mMode && radius == mRadius)
            return;

        removeContext(true);
        mMode = blurMode;
        addContext(true);

        //otherwise i need to blur the image again
        updateAlgorithms(blurMode);
        mLastRadius = -1;
        mRadius = radius;
    }

    /**
     * Changes the blur mode of the image.
     *
     * @param radius strength of the image
     */
    public final void changeRadius(int radius){
        changeMode(mMode, radius);
    }


    /**
     * Blurs the image, if not already blurred.
     * To get the image call getLastBlurredBitmap()!
     *
     * @param radius Strength of the blurring
     */
    public void blur(int radius){
        mRadius = radius;

        if(mOriginalBitmap == null || mOriginalBitmap.isRecycled())
            return;

        //if I already blurred the image with this radius, I don't do anything
        if(mLastRadius == radius && mBlurredBitmap != null){
            return;
        }

        if(mIsAlreadyBlurred && mBlurOptions.isStaticBlur())
            return;

        this.mLastRadius = radius;

        if(mBlurredBitmap != null && mOriginalBitmap != mBlurredBitmap)
            mBlurredBitmap.recycle();

        Bitmap bitmap;
        addContext(false);
        try {
            if(radius == 0)
                bitmap = mOriginalBitmap;
            else
                bitmap = mBlurAlgorithm.blur(mOriginalBitmap, mRadius, mBlurOptions);
            mIsAlreadyBlurred = true;

        } catch (RenderscriptException e){
            //Something wrong occurred with renderscript: fallback to java or nothing, based on option...

            //changing mode to fallback one if enabled
            mMode = mBlurOptions.isUseRsFallback() ? mMode.getFallbackMode() : PivBlurMode.DISABLED;

            Log.w(BlurManager.class.getSimpleName(), e.getLocalizedMessage() + "\nFalling back to another blurring method: " + mMode.name());

            updateAlgorithms(mMode);

            try {
                bitmap = mBlurAlgorithm.blur(mOriginalBitmap, mRadius, mBlurOptions);
                mIsAlreadyBlurred = true;
            } catch (RenderscriptException e1){
                bitmap = null;
            }

        }
        removeContext(false);
        if (mBlurOptions.isStaticBlur()) {
            mOriginalBitmap = bitmap == null ? mOriginalBitmap : bitmap;
        }
        mBlurredBitmap = bitmap == null ? mBlurredBitmap : bitmap;

    }


    /** Updates the saved width and height, used to calculate the blurred bitmap */
    public void onSizeChanged(int width, int height, Drawable drawable){
        this.mWidth = width;
        this.mHeight = height;
        if(drawable != null)
            changeDrawable(drawable);
    }


    /**
     * Updates the algorithm used to blur the image
     *
     * @param blurMode Algorithm to use
     */
    private void updateAlgorithms(PivBlurMode blurMode){
        RenderScript renderScript;
        mMode = blurMode;

        addContext(false);


        switch (blurMode){

            case STACK_RS:
                renderScript = SharedBlurManager.getRenderScriptContext();
                if(renderScript != null) {
                    if (mStackRenderscriptBlurAlgorithm == null)
                        mStackRenderscriptBlurAlgorithm = new StackRenderscriptBlurAlgorithm();
                    mBlurAlgorithm = mStackRenderscriptBlurAlgorithm;
                    mBlurAlgorithm.setRenderscript(renderScript);
                }
                //if renderscript is null, there was a problem getting it: let's use java or dummy
                else
                    updateAlgorithms(mBlurOptions.isUseRsFallback() ? blurMode.getFallbackMode() : PivBlurMode.DISABLED);
                break;

            case STACK:
                if(mStackBlurAlgorithm == null)
                    mStackBlurAlgorithm = new StackBlurAlgorithm();
                mBlurAlgorithm = mStackBlurAlgorithm;
                break;

            case GAUSSIAN5X5_RS:
                renderScript = SharedBlurManager.getRenderScriptContext();
                if(renderScript != null) {
                    if (mGaussian5x5RenderscriptBlurAlgorithm == null)
                        mGaussian5x5RenderscriptBlurAlgorithm = new Gaussian5x5RenderscriptBlurAlgorithm();
                    mBlurAlgorithm = mGaussian5x5RenderscriptBlurAlgorithm;
                    mBlurAlgorithm.setRenderscript(renderScript);
                }
                //if renderscript is null, there was a problem getting it: let's use java or dummy
                else
                    updateAlgorithms(mBlurOptions.isUseRsFallback() ? blurMode.getFallbackMode() : PivBlurMode.DISABLED);
                break;

            case GAUSSIAN5X5:
                if(mGaussian5x5BlurAlgorithm == null)
                    mGaussian5x5BlurAlgorithm = new Gaussian5x5BlurAlgorithm();
                mBlurAlgorithm = mGaussian5x5BlurAlgorithm;
                break;

            case GAUSSIAN3X3_RS:
                renderScript = SharedBlurManager.getRenderScriptContext();
                if(renderScript != null) {
                    if (mGaussian3x3RenderscriptBlurAlgorithm == null)
                        mGaussian3x3RenderscriptBlurAlgorithm = new Gaussian3x3RenderscriptBlurAlgorithm();
                    mBlurAlgorithm = mGaussian3x3RenderscriptBlurAlgorithm;
                    mBlurAlgorithm.setRenderscript(renderScript);
                }
                //if renderscript is null, there was a problem getting it: let's use java or dummy
                else
                    updateAlgorithms(mBlurOptions.isUseRsFallback() ? blurMode.getFallbackMode() : PivBlurMode.DISABLED);
                break;

            case GAUSSIAN3X3:
                if(mGaussian3x3BlurAlgorithm == null)
                    mGaussian3x3BlurAlgorithm = new Gaussian3x3BlurAlgorithm();
                mBlurAlgorithm = mGaussian3x3BlurAlgorithm;
                break;

            case BOX5X5_RS:
                renderScript = SharedBlurManager.getRenderScriptContext();
                if(renderScript != null) {
                    if (mBox5x5RenderscriptBlurAlgorithm == null)
                        mBox5x5RenderscriptBlurAlgorithm  = new Box5x5RenderscriptBlurAlgorithm();
                    mBlurAlgorithm = mBox5x5RenderscriptBlurAlgorithm ;
                    mBlurAlgorithm.setRenderscript(renderScript);
                }
                //if renderscript is null, there was a problem getting it: let's use java or dummy
                else
                    updateAlgorithms(mBlurOptions.isUseRsFallback() ? blurMode.getFallbackMode() : PivBlurMode.DISABLED);
                break;

            case BOX5X5:
                if(mBox5x5BlurAlgorithm == null)
                    mBox5x5BlurAlgorithm = new Box5x5BlurAlgorithm();
                mBlurAlgorithm = mBox5x5BlurAlgorithm;
                break;

            case BOX3X3_RS:
                renderScript = SharedBlurManager.getRenderScriptContext();
                if(renderScript != null) {
                    if (mBox3x3RenderscriptBlurAlgorithm == null)
                        mBox3x3RenderscriptBlurAlgorithm = new Box3x3RenderscriptBlurAlgorithm();
                    mBlurAlgorithm = mBox3x3RenderscriptBlurAlgorithm;
                    mBlurAlgorithm.setRenderscript(renderScript);
                }
                //if renderscript is null, there was a problem getting it: let's use java or dummy
                else
                    updateAlgorithms(mBlurOptions.isUseRsFallback() ? blurMode.getFallbackMode() : PivBlurMode.DISABLED);
                break;

            case BOX3X3:
                if(mBox3x3BlurAlgorithm == null)
                    mBox3x3BlurAlgorithm = new Box3x3BlurAlgorithm();
                mBlurAlgorithm = mBox3x3BlurAlgorithm;
                break;


            case GAUSSIAN_RS:
                renderScript = SharedBlurManager.getRenderScriptContext();
                if(renderScript != null) {
                    if (mGaussianRenderscriptBlurAlgorithm == null)
                        mGaussianRenderscriptBlurAlgorithm = new GaussianRenderscriptBlurAlgorithm();
                    mBlurAlgorithm = mGaussianRenderscriptBlurAlgorithm;
                    mBlurAlgorithm.setRenderscript(renderScript);
                }
                //if renderscript is null, there was a problem getting it: let's use java or dummy
                else
                    updateAlgorithms(mBlurOptions.isUseRsFallback() ? blurMode.getFallbackMode() : PivBlurMode.DISABLED);
                break;

            case GAUSSIAN:
                if(mGaussianBlurAlgorithm == null)
                    mGaussianBlurAlgorithm = new GaussianBlurAlgorithm();
                mBlurAlgorithm = mGaussianBlurAlgorithm;
                break;


            default:
            case DISABLED:
                if(mDummyBlurAlgorithm == null)
                    mDummyBlurAlgorithm = new DummyBlurAlgorithm();
                mBlurAlgorithm = mDummyBlurAlgorithm;
                break;
        }
    }

    /**
     * Check if the current options require the bitmap to be blurred
     *
     * @return True if the bitmap should be blurred, false otherwise
     */
    public boolean shouldBlur(Drawable drawable, boolean checkDrawable){
        return mMode != PivBlurMode.DISABLED &&
                (mLastRadius != mRadius || (checkDrawable && mDrawable != drawable));
    }

    /**
     * @return The blurred bitmap. If any problem occurs, the original bitmap (nullable) will be returned.
     */
    public Bitmap getLastBlurredBitmap(){
        blur(mRadius);
        return mBlurredBitmap != null ? mBlurredBitmap : mOriginalBitmap;
    }

    /**
     * @return Returns the bitmap of the drawable, downsampled if needed
     */
    private Bitmap getOriginalBitmapFromDrawable(Drawable mLastDrawable, Drawable drawable) {
        Bitmap bitmap;

        if (drawable == null || mWidth <= 0 || mHeight <= 0) {
            return null;
        }

        //bitmap size should not be bigger than the view size
        float ratio = (float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
        int sizeX;
        int sizeY;
        int maxWidth = (int) (Math.max(mWidth, mHeight * ratio) / mBlurOptions.getDownSamplingRate());
        int maxHeight = (int) (Math.max(mHeight, mWidth / ratio) / mBlurOptions.getDownSamplingRate());

        if (drawable.getIntrinsicWidth() > maxWidth && maxWidth > 0 && drawable.getIntrinsicHeight() > maxHeight && maxHeight > 0) {
            sizeX = maxWidth;
            sizeY = maxHeight;
        } else {
            sizeX = drawable.getIntrinsicWidth();
            sizeY = drawable.getIntrinsicHeight();
        }

        //if i already decoded the bitmap i reuse it
        if (sizeX > 0 && sizeY > 0 && mOriginalBitmap != null && !mOriginalBitmap.isRecycled() && mLastSizeX == sizeX && mLastSizeY == sizeY && mLastDrawable == drawable)
            return mOriginalBitmap;

        mLastSizeX = sizeX;
        mLastSizeY = sizeY;


        try {

            if (drawable instanceof BitmapDrawable) {
                bitmap = Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888);
            } else if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {

                bitmap = Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return mOriginalBitmap;
        }

    }


    /**
     * Adds the context to the renderscript manager, if needed.
     * If the blur is static renderscript context is managed by the manager itself, to release it as soon as possible.
     * If the blur is not static renderscript context is managed by the view itself, to keep it as long as it needs.
     *
     * @param fromView If the function was called by the view
     */
    public void addContext(boolean fromView){
        if (mIsRenderscriptManaged || mView.get() == null)
            return;

        Context context = mView.get().getContext().getApplicationContext();
        if(mView.get().getContext().getApplicationContext() != null && mBlurOptions.isStaticBlur() != fromView) {
            if(mMode.isUsesRenderscript()) {
                mIsRenderscriptManaged = true;
                SharedBlurManager.addRenderscriptContext(context.getApplicationContext());
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
    public void removeContext(boolean fromView){
        if (!mIsRenderscriptManaged)
            return;

        if(mBlurOptions.isStaticBlur() != fromView) {
            switch (mMode) {
                case GAUSSIAN5X5_RS:
                    mIsRenderscriptManaged = false;
                    SharedBlurManager.removeRenderscriptContext();
                    updateAlgorithms(mMode);
                    break;

                case GAUSSIAN5X5:
                case DISABLED:
                default:
                    break;
            }
        }
    }

    @Override
    public void onStaticBlurChanged() {
        //If staticBlur is true, i release original bitmap and swap it with the blurred one, if it exists
        if(mBlurOptions.isStaticBlur()) {
            if (mBlurredBitmap != null && mBlurredBitmap != mOriginalBitmap) {
                mOriginalBitmap.recycle();
                mOriginalBitmap = mBlurredBitmap;
                mBlurredBitmap = null;
            }
            removeContext(false);
        }
        else {
            addContext(true);
        }
    }

    @Override
    public void onDownsamplingRateChanged() {
        //if downSampling rate changes, i reload the bitmap and blur it
        changeDrawable(mDrawable);
        blur(mRadius);
        if(mView.get() != null) {
            Bitmap bitmap = getLastBlurredBitmap();
            if(bitmap != null)
                mView.get().setImageBitmap(getLastBlurredBitmap());
        }
    }


    /** Returns the selected mode used for blurring */
    public PivBlurMode getBlurMode() {
        return mMode;
    }

    /** Returns the options used for blurring */
    public BlurOptions getBlurOptions() {
        return mBlurOptions;
    }

    /** Returns the selected radius used for blurring */
    public int getRadius() {
        return mRadius;
    }

    /**
     * @return The original bitmap used to blur. If static blur option is enabled, this will be the
     * same as the blurred one, since the original bitmap has been released.
     */
    public Bitmap getOriginalBitmap() {
        return mOriginalBitmap;
    }


    /** Saves state into a bundle. */
    public Bundle saveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("blur_options", mBlurOptions);
        bundle.putInt("blur_mode", mMode.getValue());
        bundle.putInt("blur_radius", mRadius);
        return bundle;
    }

    /** Restores state from a bundle. */
    public void restoreInstanceState(Bundle state) {
        if (state == null)
            return;

        mBlurOptions.setOptions((BlurOptions) state.getParcelable("blur_options"));
        PivBlurMode blurMode = PivBlurMode.fromValue(state.getInt("blur_mode"));
        mWidth = state.getInt("blur_width");
        mHeight = state.getInt("blur_height");
        mRadius = state.getInt("blur_radius");
        mLastRadius = -1;
        changeMode(blurMode, mRadius);
    }

}
