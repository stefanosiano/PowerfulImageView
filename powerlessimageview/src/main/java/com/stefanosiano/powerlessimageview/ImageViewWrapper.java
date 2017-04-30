package com.stefanosiano.powerlessimageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ImageView Wrapper that enables to catch all the methods where the image or a size changes and react accordingly.
 */

abstract class ImageViewWrapper extends ImageView {

    public ImageViewWrapper(Context context) {
        super(context);
    }

    public ImageViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewWrapper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ImageViewWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }





    abstract void onBitmapChanged();

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
    public void invalidateDrawable(Drawable dr) {
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
        onBitmapChanged();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        onBitmapChanged();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        onBitmapChanged();
    }

    @Override
    public void setImageIcon(Icon icon) {
        super.setImageIcon(icon);
        onBitmapChanged();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        onBitmapChanged();
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
        return a.getColor(colorId,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                        getResources().getColor(defaultColorId, getContext().getTheme()) :
                        getResources().getColor(defaultColorId));

    }


    /**
     * @return Returns the bitmap of the drawable
     */
    protected Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
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
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
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
