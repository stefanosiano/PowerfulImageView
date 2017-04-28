package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.stefanosiano.powerlessimageview.shape.ShapeOptions;

/**
 * Created by stefano on 05/04/17.
 */

final class CircleShapeDrawer implements ShapeDrawer {

    private BitmapShader mBitmapShader;
    private final Paint mBitmapPaint;
    private final Paint mBackPaint;

    private float cx, cy, radius;

    CircleShapeDrawer(Bitmap bitmap) {
        this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint = new Paint();
        mBackPaint = new Paint();
        cx = 0;
        cy = 0;
        radius = 0;
    }

    @Override
    public void changeBitmap(Drawable drawable, Bitmap bitmap) {

        this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setShader(mBitmapShader);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void setMatrix(Matrix matrix) {
        mBitmapShader.setLocalMatrix(matrix);
    }

    @Override
    public void setup(ShapeOptions shapeOptions) {

        cx = shapeOptions.getRect().left + shapeOptions.getRect().width() / 2;
        cy = shapeOptions.getRect().top + shapeOptions.getRect().height() / 2;

        mBackPaint.setColor(shapeOptions.getBackgroundColor());
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.FILL);

        radius = cx < cy ? cx : cy;
    }

    @Override
    public void draw(Canvas canvas, RectF shapeBounds) {
        if(mBackPaint.getColor() != Color.TRANSPARENT)
            canvas.drawCircle(cx, cy, radius, mBackPaint);
        canvas.drawCircle(cx, cy, radius, mBitmapPaint);
    }
}
