package com.stefanosiano.powerlessimageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by stefano on 06/04/17.
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
        //todo check this!
        return false;
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        super.setAdjustViewBounds(adjustViewBounds);
    }


    @Override
    public void setMaxWidth(int maxWidth) {
        super.setMaxWidth(maxWidth);
    }


    @Override
    public void setMaxHeight(int maxHeight) {
        super.setMaxHeight(maxHeight);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageIcon(Icon icon) {
        super.setImageIcon(icon);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageState(int[] state, boolean merge) {
        super.setImageState(state, merge);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }

    @Override
    public void setImageLevel(int level) {
        super.setImageLevel(level);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
    }

    @Override
    public boolean getCropToPadding() {
        return super.getCropToPadding();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        return super.setFrame(l, t, r, b);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void setBaseline(int baseline) {
        super.setBaseline(baseline);
    }

    @Override
    public void setBaselineAlignBottom(boolean aligned) {
        super.setBaselineAlignBottom(aligned);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        super.setColorFilter(cf);
    }

    @Override
    public void setImageAlpha(int alpha) {
        super.setImageAlpha(alpha);
    }

    @Override
    public void setAlpha(int alpha) {
        setImageAlpha(alpha);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
