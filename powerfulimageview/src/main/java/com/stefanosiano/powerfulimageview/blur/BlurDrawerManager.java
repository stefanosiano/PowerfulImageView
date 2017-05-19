package com.stefanosiano.powerfulimageview.blur;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by stefano on 19/05/17.
 */

public final class BlurDrawerManager {

    private Drawable mDrawable;

    private Bitmap mOriginalBitmap;

    private Bitmap mBlurredBitmap;

    private PivBlurMode mMode;

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
    public BlurDrawerManager(View view, final BlurOptions blurOptions){

    }

    /**
     * Method that updates the drawable and bitmap to show
     *
     * @param drawable drawable to show
     */
    public void changeDrawable(Drawable drawable) {
        if(mMode == PivBlurMode.DISABLED)
            return;
        Drawable mLastDrawable = mDrawable;
        this.mDrawable = drawable;
        this.mOriginalBitmap = getOriginalBitmapFromDrawable(mLastDrawable, drawable);
    }

    /**
     * @return The blurred bitmap. If any problem occurs, the original bitmap will be returned.
     */
    public Bitmap getBlurredBitmap(){
        mBlurredBitmap = blur(mLastRadius);
        return mBlurredBitmap != null ? mBlurredBitmap : mOriginalBitmap;
    }

    /**
     * Changes the blur mode of the image.
     *
     * @param blurMode mode to use to blur the image
     */
    public final void changeBlurMode(PivBlurMode blurMode, int radius){
        this.mLastRadius = mRadius;
        this.mRadius = radius;
    }


    public Bitmap blur(int radius){
        this.mLastRadius = mRadius;
        this.mRadius = radius;

        return mOriginalBitmap;
    }

    public boolean shouldBlur(){
        return mMode != PivBlurMode.DISABLED && mRadius > 0;
    }



    public void setSize(int width, int height){
        this.mWidth = width;
        this.mHeight = height;
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
                int maxHeight = (int) Math.max(mHeight, mHeight / ratio);

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
