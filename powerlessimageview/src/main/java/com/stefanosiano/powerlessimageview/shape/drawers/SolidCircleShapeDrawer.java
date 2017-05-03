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
 * ShapeDrawer that draws the drawable directly into the shape and then draws a solid color over it.
 */

final class SolidCircleShapeDrawer implements ShapeDrawer {

    /** Paint used to draw the shape background */
    private final Paint mBackPaint;

    /** Paint used to draw the shape frontground */
    private final Paint mFrontPaint;

    /** Paint used to draw the shape border */
    private final Paint mBorderPaint;

    /** Paint used to draw the shape border */
    private final RectF mDrawRect;

    /** Matrix used to modify the canvas and draw */
    private Matrix mMatrix;

    /** Drawable to draw in the shape */
    private Drawable mDrawable;

    /** Scale type selected */
    private ImageView.ScaleType mScaleType;

    /** Paint used to draw the solid color */
    private final Paint mSolidPaint;

    /** Variables used to draw the border and the solid color */
    private float mCx, mCy, mRadius, mBorderRadius;

    /**
     * ShapeDrawer that draws the drawable directly into the shape and then draws a solid color over it.
     */
    SolidCircleShapeDrawer(Drawable drawable) {
        this.mDrawable = drawable;
        this.mBackPaint = new Paint();
        this.mFrontPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mDrawRect = new RectF();
        this.mMatrix = new Matrix();
        this.mSolidPaint = new Paint();
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

        mDrawRect.set(shapeOptions.getShapeBounds());
        mDrawRect.inset(-shapeOptions.getBorderWidth(), -shapeOptions.getBorderWidth());

        mBackPaint.setColor(shapeOptions.getBackgroundColor());
        mFrontPaint.setColor(shapeOptions.getFrontgroundColor());

        mBorderPaint.setColor(shapeOptions.getBorderColor());
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(shapeOptions.getBorderWidth());

        mSolidPaint.setColor(shapeOptions.getSolidColor());
        mSolidPaint.setAntiAlias(true);
        mSolidPaint.setStyle(Paint.Style.STROKE);

        mCx = shapeOptions.getBorderBounds().centerX();
        mCy = shapeOptions.getBorderBounds().centerY();

        //I must be sure to fill the whole view -> the maximum distance of the rectangle of the view
        //that is the hypotenuse of the triangle built over half width and half height of the rectangle.
        //I could use Pythagoras formula, but using triangles maths, we know that width+height > hypotenuse
        //Finally i subtract the shape radius, since it will
        float width = (shapeOptions.getViewBounds().width() + shapeOptions.getViewBounds().height() - shapeOptions.getBorderBounds().width()) / 2;

        mRadius = (shapeOptions.getBorderBounds().width() + width + shapeOptions.getBorderWidth()) / 2;
        mSolidPaint.setStrokeWidth(width);

        mBorderRadius = shapeOptions.getBorderBounds().width() < shapeOptions.getBorderBounds().height() ? shapeOptions.getBorderBounds().width()/2 : shapeOptions.getBorderBounds().height()/2;
    }

    @Override
    public void draw(Canvas canvas, RectF borderBounds, RectF shapeBounds, RectF imageBounds) {

        //background
        if(mBackPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRect(mDrawRect, mBackPaint);

        //image
        if (mDrawable != null) {
            //if scaleType is XY, we should draw the image on the whole view
            if(mScaleType != null && mScaleType == ImageView.ScaleType.FIT_XY) {
                mDrawable.setBounds((int) imageBounds.left, (int) imageBounds.top, (int) imageBounds.right, (int) imageBounds.bottom);
                mDrawable.draw(canvas);
            }
            else {
                //I save the state, apply the matrix and restore the state of the canvas
                final int saveCount = canvas.getSaveCount();
                canvas.save();

                if (mScaleType != ImageView.ScaleType.FIT_XY)
                    canvas.concat(mMatrix);

                mDrawable.draw(canvas);
                canvas.restoreToCount(saveCount);
            }
        }

        //frontground
        if(mFrontPaint.getColor() != Color.TRANSPARENT)
            canvas.drawRect(mDrawRect, mFrontPaint);

        //border
        if(mBorderPaint.getStrokeWidth() > 0 && mBackPaint.getColor() != Color.TRANSPARENT)
            canvas.drawCircle(mCx, mCy, mBorderRadius, mBorderPaint);

        //solid color
        canvas.drawCircle(mCx, mCy, mRadius, mSolidPaint);
    }
}
