package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.Log;

/**
 * Created by stefano on 05/04/17.
 */

public class ShapeDrawerManager {
    BitmapShader shader;
    Paint paint;

    public ShapeDrawerManager() {
        shader = new BitmapShader(null, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setShader(shader);
    }


    /** Draws the image through the progress indicator */
    public final void onDraw(Canvas canvas) {
        canvas.drawLine(1, 1, 400, 400, paint);
        canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, canvas.getWidth()/2, paint);
        canvas.drawCircle(2, 100, 100, paint);
        Log.e("ASD", canvas.getWidth() +"asd");
        //mShadowDrawer.draw(canvas, mProgressShadowBounds);
    }
}
