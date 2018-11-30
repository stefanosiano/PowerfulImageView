package com.stefanosiano.powerfullibraries.imageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.stefanosiano.powerfullibraries.imageview.shape.PivShapeScaleType;
import com.stefanosiano.powerfullibraries.imageview.shape.ShapeOptions;

/**
 * ShapeDrawer that draws a rounded rectangle as shape.
 */

final class RoundedRectangleShapeDrawer implements ShapeDrawer {

    /** Shader to efficiently draw the shape */
    private BitmapShader mBitmapShader;

    /** Paint used to draw the image */
    private final Paint mBitmapPaint;

    /** Paint used to draw the shape background */
    private final Paint mBackPaint;

    /** Paint used to draw the shape border */
    private final Paint mBorderPaint;

    /** Paint used to draw the shape foreground */
    private final Paint mFrontPaint;

    /** Background drawable to draw under the shape */
    private Drawable mBackgroundDrawable;

    /** Foreground drawable to draw over the shape */
    private Drawable mForegroundDrawable;

    /** Matrix used to draw the shape */
    private Matrix mMatrix;

    /** Variables used to draw the rounded rectangle */
    private float mRadiusX, mRadiusY;


    /**
     * ShapeDrawer that draws a rounded rectangle as shape.
     */
    RoundedRectangleShapeDrawer(Bitmap bitmap) {
        if(bitmap != null) {
            this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapShader.setLocalMatrix(mMatrix);
        }
        mBitmapPaint = new Paint();
        mBackPaint = new Paint();
        mBorderPaint = new Paint();
        mFrontPaint = new Paint();
    }

    @Override
    public void changeDrawable(Drawable drawable) {}

    @Override
    public boolean requireBitmap() {return true;}

    @Override
    public void changeBitmap(Bitmap bitmap) {
        this.mBitmapShader = null;
        if(bitmap != null) {
            this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapShader.setLocalMatrix(mMatrix);
        }

        mBitmapPaint.setShader(mBitmapShader);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void setMatrix(PivShapeScaleType scaleType, Matrix matrix) {
        this.mMatrix = matrix;
        if(mBitmapShader != null)
            mBitmapShader.setLocalMatrix(matrix);
    }

    @Override
    public void setup(ShapeOptions shapeOptions) {

        mRadiusX = shapeOptions.getRadiusX();
        mRadiusY = shapeOptions.getRadiusY();
        mForegroundDrawable = shapeOptions.getForegroundDrawable();
        mBackgroundDrawable = shapeOptions.getBackgroundDrawable();

        mBackPaint.setColor(shapeOptions.getBackgroundColor());
        mBackPaint.setAntiAlias(true);
        mBackPaint.setStyle(Paint.Style.FILL);

        mBorderPaint.setColor(shapeOptions.getBorderColor());
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(shapeOptions.getBorderWidth());

        mFrontPaint.setColor(shapeOptions.getForegroundColor());
        mFrontPaint.setAntiAlias(true);
        mFrontPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas, RectF borderBounds, RectF shapeBounds, RectF imageBounds) {

        //background
        if(mBackPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRoundRect(shapeBounds, mRadiusX, mRadiusY, mBackPaint);

        if(mBackgroundDrawable != null){
            mBackgroundDrawable.setBounds((int) imageBounds.left, (int) imageBounds.top, (int) imageBounds.right, (int) imageBounds.bottom);
            mBackgroundDrawable.draw(canvas);
        }

        //image
        canvas.drawRoundRect(imageBounds, mRadiusX, mRadiusY, mBitmapPaint);

        if(mForegroundDrawable != null){
            mForegroundDrawable.setBounds((int) imageBounds.left, (int) imageBounds.top, (int) imageBounds.right, (int) imageBounds.bottom);
            mForegroundDrawable.draw(canvas);
        }

        //foreground
        if(mFrontPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRoundRect(shapeBounds, mRadiusX, mRadiusY, mFrontPaint);

        //border
        if(mBorderPaint.getStrokeWidth() > 0 && mBorderPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRoundRect(borderBounds, mRadiusX * borderBounds.height() / imageBounds.height(), mRadiusY * borderBounds.width() / imageBounds.width(), mBorderPaint);
    }
}
