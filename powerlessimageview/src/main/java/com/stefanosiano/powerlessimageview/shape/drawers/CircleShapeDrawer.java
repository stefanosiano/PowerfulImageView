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
    private final Paint mBorderPaint;
    private final Paint mFrontPaint;

    private float cx, cy, radius, borderRadius;

    CircleShapeDrawer(Bitmap bitmap) {
        this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint = new Paint();
        mBackPaint = new Paint();
        mBorderPaint = new Paint();
        mFrontPaint = new Paint();
        cx = 0;
        cy = 0;
        radius = 0;
        borderRadius = 0;
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

        cx = shapeOptions.getShapeBounds().centerX();
        cy = shapeOptions.getShapeBounds().centerY();
        radius = shapeOptions.getShapeBounds().width() < shapeOptions.getShapeBounds().height() ? shapeOptions.getShapeBounds().width()/2 : shapeOptions.getShapeBounds().height()/2;
        borderRadius = shapeOptions.getBorderBounds().width() < shapeOptions.getBorderBounds().height() ? shapeOptions.getBorderBounds().width()/2 : shapeOptions.getBorderBounds().height()/2 - shapeOptions.getBorderWidth() / 2;

        mBackPaint.setColor(shapeOptions.getBackgroundColor());
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.FILL);

        mBorderPaint.setColor(shapeOptions.getBorderColor());
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(shapeOptions.getBorderWidth());

        mFrontPaint.setColor(shapeOptions.getFrontgroundColor());
        mFrontPaint.setAntiAlias(true);
        mFrontPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas, RectF shapeBounds) {
        if(mBackPaint.getColor() != Color.TRANSPARENT)
            canvas.drawCircle(cx, cy, radius, mBackPaint);
        canvas.drawCircle(cx, cy, radius, mBitmapPaint);
        if(mFrontPaint.getColor() != Color.TRANSPARENT)
            canvas.drawCircle(cx, cy, radius, mFrontPaint);
        canvas.drawCircle(cx, cy, borderRadius, mBorderPaint);
    }
}
