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
 * Created by stefano on 19/05/17.
 */

public final class BlurManager {

    private Drawable mDrawable;

    private Bitmap mOriginalBitmap;

    private Bitmap mBlurredBitmap;

    private PivBlurMode mMode;

    private GaussianFastBlurAlgorithm mGaussianFastBlurAlgorithm;
    private BlurAlgorithm mBlurAlgorithm;

    private int mWidth;
    private int mHeight;
    private int mRadius;
    private int mLastRadius;

    /**
     * Manager class for blur. Used to initialize and blur the image with the right algorithm.
     *
     * @param view View to show the blurred image into
     * @param blurOptions Options of the blur
     */
    public BlurManager(View view, final BlurOptions blurOptions){
    }

    /**
     * Method that updates the drawable and bitmap to show
     *
     * @param drawable drawable to show
     */
    public void changeDrawable(Drawable drawable) {
        Drawable mLastDrawable = mDrawable;
        this.mDrawable = drawable;
        if(shouldBlur(mDrawable))
            this.mOriginalBitmap = getOriginalBitmapFromDrawable(mLastDrawable, drawable);
    }

    /**
     * Changes the blur mode of the image.
     *
     * @param blurMode mode to use to blur the image
     */
    public final void changeBlurMode(PivBlurMode blurMode, int radius){

        switch (blurMode){
            case DISABLED:
                if(mGaussianFastBlurAlgorithm == null)
                    mGaussianFastBlurAlgorithm = new GaussianFastBlurAlgorithm();
                mBlurAlgorithm = mGaussianFastBlurAlgorithm;
                break;
        }

        mLastRadius = -1;
        this.mRadius = radius;
        //blur(radius);
    }


    public Bitmap blur(int radius){
        this.mRadius = radius;

        if(mOriginalBitmap == null)
            return null;

        if(mLastRadius == radius && mBlurredBitmap != null){
            this.mLastRadius = radius;

            //if I already blurred the image with this radius, I return it
            return mBlurredBitmap;
        }

        this.mLastRadius = radius;

        if(mBlurredBitmap != null)
            mBlurredBitmap.recycle();

        mBlurredBitmap = mBlurAlgorithm.blur(mOriginalBitmap);
        return mBlurredBitmap;
    }


    public void onSizeChanged(int width, int height){
        this.mWidth = width;
        this.mHeight = height;
        changeDrawable(mDrawable);
    }

    /**
     * Check if the current options require the bitmap to be blurred
     *
     * @return True if the bitmap should be blurred, false otherwise
     */
    public boolean shouldBlur(Drawable drawable){
        return (mMode != PivBlurMode.DISABLED && mRadius > 0 && mLastRadius != mRadius);// || mDrawable != drawable;
    }

    /**
     * @return The blurred bitmap. If any problem occurs, the original bitmap (nullable) will be returned.
     */
    public Bitmap getLastBlurredBitmap(){
        mBlurredBitmap = blur(mRadius);
        return mBlurredBitmap != null ? mBlurredBitmap : mOriginalBitmap;
    }

    /**
     * @return Returns the bitmap of the drawable
     */
    private Bitmap getOriginalBitmapFromDrawable(Drawable mLastDrawable, Drawable drawable) {
        if (drawable == null || mWidth <= 0 || mHeight <= 0) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                //bitmap size should not be bigger than the view size
                float ratio = drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                int sizeX;
                int sizeY;
                int maxWidth = (int) Math.max(mWidth, mHeight * ratio);
                int maxHeight = (int) Math.max(mHeight, mWidth / ratio);

                if(drawable.getIntrinsicWidth() > maxWidth && maxWidth > 0 && drawable.getIntrinsicHeight() > maxHeight && maxHeight > 0){
                    sizeX = maxWidth;
                    sizeY = maxHeight;
                }
                else {
                    sizeX = drawable.getIntrinsicWidth();
                    sizeY = drawable.getIntrinsicHeight();
                }

                //if i already decoded the bitmap i reuse it
                if(sizeX > 0 && sizeY > 0 && mOriginalBitmap != null && mLastDrawable == drawable)
                    return mOriginalBitmap;

                //otherwise I free its memory
                if(mOriginalBitmap != null)
                    mOriginalBitmap.recycle();

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
}
