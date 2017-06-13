package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v8.renderscript.RenderScript;
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

    //Algorithms
    private GaussianFastBlurAlgorithm mGaussianFastBlurAlgorithm;
    private GaussianRenderscriptBlurAlgorithm mGaussianRenderscriptBlurAlgorithm;
    private DummyBlurAlgorithm mDummyBlurAlgorithm;

    /** Selected algorithm to blur the image */
    private BlurAlgorithm mBlurAlgorithm;

    /** Width of the view. Used to calculate the original bitmap */
    private int mWidth;

    /** Height of the view. Used to calculate the original bitmap */
    private int mHeight;

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
            if(lastOriginalBitmap != null)
                lastOriginalBitmap.recycle();
            mLastRadius = -1;
            blur(mRadius);
        }
    }

    /**
     * Changes the blur mode of the image.
     *
     * @param blurMode mode to use to blur the image
     */
    public final void changeBlurMode(PivBlurMode blurMode, int radius){

        //If there's no change, I don't do anything
        if(blurMode == mMode && radius == mRadius)
            return;

        //updating renderscript context if needed
        if(mView.get() != null)
            removeContext();
        mMode = blurMode;
        if(mView.get() != null)
            addContext(mView.get().getContext());

        //otherwise i need to blur the image again
        updateAlgorithms(blurMode);
        mLastRadius = -1;
        mRadius = radius;
    }


    /**
     * Blurs the image, if not already blurred
     *
     * @param radius Strength of the blurring
     * @return The blurred image
     */
    public Bitmap blur(int radius){
        mRadius = radius;

        if(mOriginalBitmap == null || mOriginalBitmap.isRecycled())
            return null;

        if(mLastRadius == radius && mBlurredBitmap != null){
            //if I already blurred the image with this radius, I return it
            return mBlurredBitmap;
        }

        this.mLastRadius = radius;

        if(mBlurredBitmap != null)
            mBlurredBitmap.recycle();

        //todo use mUseRsFallback!

        if(mBlurOptions.isKeepOriginal()){
            mBlurredBitmap = mBlurAlgorithm.blur(mOriginalBitmap, mRadius, mBlurOptions);
            return mBlurredBitmap;
        }
        else {
            mOriginalBitmap = mBlurAlgorithm.blur(mOriginalBitmap, mRadius, mBlurOptions);
            return mOriginalBitmap;
        }
    }


    /** Updates the saved width and height, used to calculate the blurred bitmap */
    public void onSizeChanged(int width, int height){
        this.mWidth = width;
        this.mHeight = height;
    }


    /**
     * Updates the algorithm used to blur the image
     *
     * @param blurMode Algorithm to use
     */
    private void updateAlgorithms(PivBlurMode blurMode){
        RenderScript renderScript = RenderscriptManager.getRenderScript();
        //todo use mUseRsFallback!
        switch (blurMode){

            case GAUSSIAN_RS:
                if(renderScript != null) {
                    if (mGaussianRenderscriptBlurAlgorithm == null)
                        mGaussianRenderscriptBlurAlgorithm = new GaussianRenderscriptBlurAlgorithm();
                    mBlurAlgorithm = mGaussianRenderscriptBlurAlgorithm;
                }
                //if renderscript is null, there was a problem getting it: let's use java or dummy
                else if(mBlurOptions.isUseRsFallback()){
                    if (mGaussianFastBlurAlgorithm == null)
                        mGaussianFastBlurAlgorithm = new GaussianFastBlurAlgorithm();
                    mBlurAlgorithm = mGaussianFastBlurAlgorithm;
                }
                else {
                    if(mDummyBlurAlgorithm == null)
                        mDummyBlurAlgorithm = new DummyBlurAlgorithm();
                    mBlurAlgorithm = mDummyBlurAlgorithm;
                }
                break;

            case GAUSSIAN:
                if(mGaussianFastBlurAlgorithm == null)
                    mGaussianFastBlurAlgorithm = new GaussianFastBlurAlgorithm();
                mBlurAlgorithm = mGaussianFastBlurAlgorithm;
                break;
            default:

            case DISABLED:
                if(mDummyBlurAlgorithm == null)
                    mDummyBlurAlgorithm = new DummyBlurAlgorithm();
                mBlurAlgorithm = mDummyBlurAlgorithm;
                break;
        }
        mBlurAlgorithm.setRenderscript(renderScript);
    }

    /**
     * Check if the current options require the bitmap to be blurred
     *
     * @return True if the bitmap should be blurred, false otherwise
     */
    public boolean shouldBlur(Drawable drawable){
        return mMode != PivBlurMode.DISABLED && mRadius > 0 && (mLastRadius != mRadius || mDrawable != drawable);
    }

    /**
     * @return The blurred bitmap. If any problem occurs, the original bitmap (nullable) will be returned.
     */
    public Bitmap getLastBlurredBitmap(){
        Bitmap bitmap = blur(mRadius);
        return bitmap != null ? bitmap : mOriginalBitmap;
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
        if (sizeX > 0 && sizeY > 0 && mOriginalBitmap != null && !mOriginalBitmap.isRecycled() && mOriginalBitmap.getWidth() == sizeX && mOriginalBitmap.getHeight() == mHeight && mLastDrawable == drawable)
            return mOriginalBitmap;

        //otherwise I free its memory
        if (mOriginalBitmap != null)
            mOriginalBitmap.recycle();


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
            return null;
        }

    }


    public void addContext(Context context){
        switch (mMode){
            case GAUSSIAN_RS:
                RenderscriptManager.addContext(context);
                updateAlgorithms(mMode);
                break;

            case GAUSSIAN:
            case DISABLED:
                default:
                    break;
        }
    }

    public void removeContext(){
        switch (mMode){
            case GAUSSIAN_RS:
                RenderscriptManager.removeContext();
                updateAlgorithms(mMode);
                break;

            case GAUSSIAN:
            case DISABLED:
            default:
                break;
        }
    }

    @Override
    public void onKeepOriginalChanged() {
        //If keepOriginal is false, i release original bitmap and swap it with the blurred one, if it exists
        if(!mBlurOptions.isKeepOriginal()) {
            if (mBlurredBitmap != null && mBlurredBitmap != mOriginalBitmap) {
                mOriginalBitmap.recycle();
                mOriginalBitmap = mBlurredBitmap;
                mBlurredBitmap = null;
            }
        }
    }

    @Override
    public void onDownsamplingRateChanged() {
        //if downSampling rate changes, i reload the bitmap and blur it
        changeDrawable(mDrawable);
        if(mView.get() != null) {
            Bitmap bitmap = getLastBlurredBitmap();
            if(bitmap != null)
                mView.get().setImageBitmap(getLastBlurredBitmap());
        }
    }
}
