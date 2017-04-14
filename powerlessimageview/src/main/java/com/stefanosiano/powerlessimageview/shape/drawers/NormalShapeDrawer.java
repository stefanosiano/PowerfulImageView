package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.stefanosiano.powerlessimageview.shape.ShapeOptions;

/**
 * Created by stefano on 14/04/17.
 */

public class NormalShapeDrawer implements ShapeDrawer {

    private BitmapShader mBitmapShader;
    private Matrix mMatrix;
    private Drawable mDrawable;
    private Bitmap mBitmap;

    NormalShapeDrawer(Drawable drawable, Bitmap bitmap) {
        this.mDrawable = drawable;
        this.mBitmap = bitmap;
        this.mMatrix = new Matrix();
    }

    @Override
    public void changeBitmap(Drawable drawable, Bitmap bitmap) {
        this.mDrawable = drawable;
        this.mBitmap = bitmap;
/*
        this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setShader(mBitmapShader);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);*/
    }

    @Override
    public void setMatrix(Matrix matrix) {
        this.mMatrix = matrix;
    }

    @Override
    public void setup(ShapeOptions shapeOptions) {

    }

    @Override
    public void draw(Canvas canvas, RectF shapeBounds) {
        if(mBitmap != null && mDrawable != null)
        canvas.drawBitmap(mBitmap, mDrawable.getBounds(), shapeBounds, null);
        //mDrawable.draw(canvas);
    }
}
