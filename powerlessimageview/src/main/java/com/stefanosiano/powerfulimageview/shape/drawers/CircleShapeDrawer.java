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
 * ShapeDrawer that draws a circle as shape.
 */

final class CircleShapeDrawer implements ShapeDrawer {

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

    /** Variables used to draw the circle */
    private float mCx, mCy, mRadius, mBorderRadius;


    /**
     * ShapeDrawer that draws a circle as shape.
     */
    CircleShapeDrawer(Bitmap bitmap) {
        if(bitmap != null) {
            this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapShader.setLocalMatrix(mMatrix);
        }
        mBitmapPaint = new Paint();
        mBackPaint = new Paint();
        mBorderPaint = new Paint();
        mFrontPaint = new Paint();
        mCx = 0;
        mCy = 0;
        mRadius = 0;
        mBorderRadius = 0;
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

        mForegroundDrawable = shapeOptions.getForegroundDrawable();
        mBackgroundDrawable = shapeOptions.getBackgroundDrawable();

        mCx = shapeOptions.getShapeBounds().centerX();
        mCy = shapeOptions.getShapeBounds().centerY();
        mRadius = shapeOptions.getShapeBounds().width() < shapeOptions.getShapeBounds().height() ? shapeOptions.getShapeBounds().width()/2 : shapeOptions.getShapeBounds().height()/2;
        mBorderRadius = shapeOptions.getBorderBounds().width() < shapeOptions.getBorderBounds().height() ? shapeOptions.getBorderBounds().width()/2 : shapeOptions.getBorderBounds().height()/2;

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
            canvas.drawCircle(mCx, mCy, mRadius, mBackPaint);

        if(mBackgroundDrawable != null){
            mBackgroundDrawable.setBounds((int) imageBounds.left, (int) imageBounds.top, (int) imageBounds.right, (int) imageBounds.bottom);
            mBackgroundDrawable.draw(canvas);
        }

        //image
        canvas.drawCircle(mCx, mCy, mRadius, mBitmapPaint);

        if(mForegroundDrawable != null){
            mForegroundDrawable.setBounds((int) imageBounds.left, (int) imageBounds.top, (int) imageBounds.right, (int) imageBounds.bottom);
            mForegroundDrawable.draw(canvas);
        }

        //foreground
        if(mFrontPaint.getColor() != Color.TRANSPARENT)
            canvas.drawCircle(mCx, mCy, mRadius, mFrontPaint);

        //border
        if(mBorderPaint.getStrokeWidth() > 0 && mBorderPaint.getColor() != Color.TRANSPARENT)
            canvas.drawCircle(mCx, mCy, mBorderRadius, mBorderPaint);
    }
}
