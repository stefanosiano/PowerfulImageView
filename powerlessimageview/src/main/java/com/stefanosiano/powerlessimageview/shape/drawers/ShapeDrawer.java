package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.stefanosiano.powerlessimageview.shape.ShapeOptions;

/**
 * Created by stefano on 05/04/17.
 */

public interface ShapeDrawer {

    void changeBitmap(Drawable drawable, Bitmap bitmap);
    void setMatrix(ImageView.ScaleType scaleType, Matrix matrix);
    void setup(ShapeOptions shapeOptions);
    void draw(Canvas canvas, RectF imageBounds);
}
