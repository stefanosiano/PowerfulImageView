package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.stefanosiano.powerlessimageview.shape.ShapeOptions;

/**
 * Created by stefano on 14/04/17.
 */

final class NormalShapeDrawer implements ShapeDrawer {

    private final Paint mBackPaint;
    private final Paint mFrontPaint;

    private Matrix mMatrix;
    private Drawable mDrawable;
    private ImageView.ScaleType mScaleType;

    NormalShapeDrawer(Drawable drawable, Bitmap bitmap) {
        this.mDrawable = drawable;
        this.mBackPaint = new Paint();
        this.mFrontPaint = new Paint();
        this.mMatrix = new Matrix();
    }

    @Override
    public void changeBitmap(Drawable drawable, Bitmap bitmap) {
        this.mDrawable = drawable;
    }

    @Override
    public void setMatrix(ImageView.ScaleType scaleType, Matrix matrix) {
        this.mScaleType = scaleType;
        this.mMatrix = matrix;

    }

    @Override
    public void setup(ShapeOptions shapeOptions) {

        mBackPaint.setColor(shapeOptions.getBackgroundColor());
        mFrontPaint.setColor(shapeOptions.getFrontgroundColor());
    }

    @Override
    public void draw(Canvas canvas, RectF shapeBounds, RectF imageBounds) {

        if(mBackPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRect(shapeBounds, mBackPaint);

        if (mDrawable != null) {
            //if scaleType is XY, we should draw the image on the whole view
            if(mScaleType != null && mScaleType == ImageView.ScaleType.FIT_XY) {
                mDrawable.setBounds((int) imageBounds.left, (int) imageBounds.top, (int) imageBounds.right, (int) imageBounds.bottom);
                mDrawable.draw(canvas);
            }
            else {
                final int saveCount = canvas.getSaveCount();
                canvas.save();

                if (mScaleType != ImageView.ScaleType.FIT_XY)
                    canvas.concat(mMatrix);

                mDrawable.draw(canvas);
                canvas.restoreToCount(saveCount);
            }
        }

        if(mFrontPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRect(shapeBounds, mFrontPaint);
    }
}
