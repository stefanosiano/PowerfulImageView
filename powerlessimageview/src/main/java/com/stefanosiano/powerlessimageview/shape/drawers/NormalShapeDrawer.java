package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.stefanosiano.powerlessimageview.shape.ShapeOptions;

/**
 * Created by stefano on 14/04/17.
 */

final class NormalShapeDrawer implements ShapeDrawer {

    private Matrix mMatrix;
    private Drawable mDrawable;
    private Bitmap mBitmap;
    private int mBackground;

    NormalShapeDrawer(Drawable drawable, Bitmap bitmap) {
        this.mDrawable = drawable;
        this.mBitmap = bitmap;
        this.mMatrix = new Matrix();
    }

    @Override
    public void changeBitmap(Drawable drawable, Bitmap bitmap) {
        this.mDrawable = drawable;
        this.mBitmap = bitmap;
    }

    @Override
    public void setMatrix(Matrix matrix) {
        this.mMatrix = matrix;

    }

    @Override
    public void setup(ShapeOptions shapeOptions) {
        mBackground = shapeOptions.getBackgroundColor();
    }

    @Override
    public void draw(Canvas canvas, RectF shapeBounds) {
        if(mBackground != android.R.color.transparent)
            canvas.drawColor(mBackground);
/*
        if(mDrawable != null && mMatrix != null){
            int state = canvas.save();
            canvas.concat(mMatrix);
            mDrawable.draw(canvas);
            canvas.restoreToCount(state);
        }*/

        if(mBitmap != null && mMatrix != null)
            canvas.drawBitmap(mBitmap, mMatrix, null);
    }
}
