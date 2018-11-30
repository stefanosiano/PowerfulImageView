package com.stefanosiano.powerfullibraries.imageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.stefanosiano.powerfullibraries.imageview.shape.PivShapeScaleType;
import com.stefanosiano.powerfullibraries.imageview.shape.ShapeOptions;

/**
 * ShapeDrawer that draws the drawable directly into the shape.
 */

final class NormalShapeDrawer implements ShapeDrawer {

    /** Paint used to draw the shape background */
    private final Paint mBackPaint;

    /** Paint used to draw the shape foreground */
    private final Paint mFrontPaint;

    /** Paint used to draw the shape border */
    private final Paint mBorderPaint;

    /** Matrix used to modify the canvas and draw */
    private Matrix mMatrix;

    /** Drawable to draw in the shape */
    private Drawable mDrawable;

    /** Background drawable to draw under the shape */
    private Drawable mBackgroundDrawable;

    /** Foreground drawable to draw over the shape */
    private Drawable mForegroundDrawable;

    /** Scale type selected */
    private PivShapeScaleType mScaleType;


    /**
     * ShapeDrawer that draws the drawable directly into the shape.
     */
    NormalShapeDrawer(Drawable drawable) {
        this.mDrawable = drawable;
        this.mBackPaint = new Paint();
        this.mFrontPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mMatrix = new Matrix();
    }

    @Override
    public void changeDrawable(Drawable drawable) {
        this.mDrawable = drawable;
    }

    @Override
    public boolean requireBitmap() {return false;}

    @Override
    public void changeBitmap(Bitmap bitmap) {}

    @Override
    public void setMatrix(PivShapeScaleType scaleType, Matrix matrix) {
        this.mScaleType = scaleType;
        this.mMatrix = matrix;

    }

    @Override
    public void setup(ShapeOptions shapeOptions) {

        mBackPaint.setColor(shapeOptions.getBackgroundColor());
        mFrontPaint.setColor(shapeOptions.getForegroundColor());
        mForegroundDrawable = shapeOptions.getForegroundDrawable();
        mBackgroundDrawable = shapeOptions.getBackgroundDrawable();

        mBorderPaint.setColor(shapeOptions.getBorderColor());
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(shapeOptions.getBorderWidth());
    }

    @Override
    public void draw(Canvas canvas, RectF borderBounds, RectF shapeBounds, RectF imageBounds) {

        //background
        if(mBackPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRect(shapeBounds, mBackPaint);

        //image
        if (mDrawable != null) {
            //if scaleType is XY, we should draw the image on the whole view
            if(mScaleType != null && mScaleType == PivShapeScaleType.FIT_XY) {
                mDrawable.setBounds((int) imageBounds.left, (int) imageBounds.top, (int) imageBounds.right, (int) imageBounds.bottom);

                if(mBackgroundDrawable != null){
                    mBackgroundDrawable.setBounds(mDrawable.getBounds());
                    mBackgroundDrawable.draw(canvas);
                }
                mDrawable.draw(canvas);
                if(mForegroundDrawable != null){
                    mForegroundDrawable.setBounds(mDrawable.getBounds());
                    mForegroundDrawable.draw(canvas);
                }
            }
            else {
                //I save the state, apply the matrix and restore the state of the canvas
                final int saveCount = canvas.getSaveCount();
                canvas.save();

                if (mScaleType != PivShapeScaleType.FIT_XY)
                    canvas.concat(mMatrix);

                if(mBackgroundDrawable != null){
                    mBackgroundDrawable.setBounds(mDrawable.getBounds());
                    mBackgroundDrawable.draw(canvas);
                }
                mDrawable.draw(canvas);
                if(mForegroundDrawable != null){
                    mForegroundDrawable.setBounds(mDrawable.getBounds());
                    mForegroundDrawable.draw(canvas);
                }

                canvas.restoreToCount(saveCount);
            }
        }

        //foreground
        if(mFrontPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRect(shapeBounds, mFrontPaint);

        //border
        if(mBorderPaint.getStrokeWidth() > 0 && mBorderPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRect(borderBounds, mBorderPaint);
    }
}
