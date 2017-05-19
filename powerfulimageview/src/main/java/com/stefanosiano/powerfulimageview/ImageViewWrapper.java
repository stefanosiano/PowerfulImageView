package com.stefanosiano.powerfulimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * ImageView Wrapper that enables to catch all the methods where the image or a size changes and react accordingly.
 */

abstract class ImageViewWrapper extends AppCompatImageView {

    public ImageViewWrapper(Context context) {
        super(context);
    }

    public ImageViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }





    /**
     * Method called when the drawable has been changed, thruogh a set..() method
     *
     * @param isBlurred True if the image is already bluured, False otherwise
     */
    abstract void onDrawableChanged(boolean isBlurred);

    /* Methods to check! Should they be handled by ShapeDrawerManager? */
    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
    }

    @Override
    public boolean getCropToPadding() {
        return super.getCropToPadding();
    }


    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        super.setColorFilter(cf);
    }

    @Override
    public void setImageAlpha(int alpha) {
        super.setImageAlpha(alpha);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public void setAlpha(int alpha) {
        setImageAlpha(alpha);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable dr) {
        super.invalidateDrawable(dr);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        super.setAdjustViewBounds(adjustViewBounds);
    }











    //these methods propagate their effects to the methods of the PIV


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        onDrawableChanged(false);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        onDrawableChanged(false);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        onDrawableChanged(false);
    }

    @Override
    public void setImageIcon(Icon icon) {
        super.setImageIcon(icon);
        onDrawableChanged(false);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        onDrawableChanged(false);
    }

    protected void setBlurredBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        onDrawableChanged(true);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
    }

    /**
     * returns selected color (default color if selected color is not available) for any api level
     */
    @SuppressWarnings("deprecation")
    protected int getColor(TypedArray a, int colorId, int defaultColorId){
        return a.getColor(colorId, ContextCompat.getColor(getContext(), defaultColorId));
    }

}
