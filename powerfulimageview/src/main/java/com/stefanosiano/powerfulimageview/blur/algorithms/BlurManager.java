package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;
import com.stefanosiano.powerfulimageview.blur.PivBlurMode;

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

    /** Selected algorithm to blur the image */
    private BlurAlgorithm mBlurAlgorithm;

    /** Width of the view. Used to calculate the original bitmap */
    private int mWidth;

    /** Height of the view. Used to calculate the original bitmap */
    private int mHeight;

    /** Last radius used to blur the image. Used to avoid blurring twice again the same image with the same radius */
    private int mLastRadius;

    /**
     * Manager class for blur. Used to initialize and blur the image with the right algorithm.
     *
     * @param view View to show the blurred image into
     * @param blurOptions Options of the blur
     */
    public BlurManager(View view, final BlurOptions blurOptions){
        mBlurOptions = blurOptions;
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

        //otherwise i need to blur the image again
        updateAlgorithms(blurMode);
        mLastRadius = -1;
        mRadius = radius;

        mBlurAlgorithm.setup(mBlurOptions);
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

        mBlurAlgorithm.setup(mBlurOptions);
        mBlurredBitmap = mBlurAlgorithm.blur(mOriginalBitmap);
        return mBlurredBitmap;
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
        switch (blurMode){
            default:
            case DISABLED:
                if(mGaussianFastBlurAlgorithm == null)
                    mGaussianFastBlurAlgorithm = new GaussianFastBlurAlgorithm();
                mBlurAlgorithm = mGaussianFastBlurAlgorithm;
                break;
        }
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
        mBlurredBitmap = blur(mRadius);
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
        if (sizeX > 0 && sizeY > 0 && mOriginalBitmap != null && !mOriginalBitmap.isRecycled() && mLastDrawable == drawable)
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


    @Override
    public void onKeepOriginalChanged() {

    }

    @Override
    public void onDownsamplingRateChanged() {

    }
}
