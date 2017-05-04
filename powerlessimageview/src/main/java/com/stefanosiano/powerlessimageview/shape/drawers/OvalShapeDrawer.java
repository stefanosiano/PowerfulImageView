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
import android.widget.ImageView;

import com.stefanosiano.powerlessimageview.shape.PivShapeScaleType;
import com.stefanosiano.powerlessimageview.shape.ShapeOptions;

/**
 * ShapeDrawer that draws an oval as shape.
 */

final class OvalShapeDrawer implements ShapeDrawer {

    /** Shader to efficiently draw the shape */
    private BitmapShader mBitmapShader;

    /** Paint used to draw the image */
    private final Paint mBitmapPaint;

    /** Paint used to draw the shape background */
    private final Paint mBackPaint;

    /** Paint used to draw the shape border */
    private final Paint mBorderPaint;

    /** Paint used to draw the shape frontground */
    private final Paint mFrontPaint;


    /**
     * ShapeDrawer that draws an oval as shape.
     */
    OvalShapeDrawer(Bitmap bitmap) {
        this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint = new Paint();
        mBackPaint = new Paint();
        mBorderPaint = new Paint();
        mFrontPaint = new Paint();
    }

    @Override
    public void changeBitmap(Drawable drawable, Bitmap bitmap) {

        this.mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setShader(mBitmapShader);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void setMatrix(PivShapeScaleType scaleType, Matrix matrix) {
        mBitmapShader.setLocalMatrix(matrix);
    }

    @Override
    public void setup(ShapeOptions shapeOptions) {

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
    public void draw(Canvas canvas, RectF borderBounds, RectF shapeBounds, RectF imageBounds) {

        //background
        if(mBackPaint.getColor() != Color.TRANSPARENT)
            canvas.drawOval(shapeBounds, mBackPaint);

        //image
        canvas.drawOval(imageBounds, mBitmapPaint);

        //frontground
        if(mFrontPaint.getColor() != Color.TRANSPARENT)
            canvas.drawOval(shapeBounds, mFrontPaint);

        //border
        if(mBorderPaint.getStrokeWidth() > 0 && mBorderPaint.getColor() != Color.TRANSPARENT)
            canvas.drawOval(borderBounds, mBorderPaint);
    }
}
