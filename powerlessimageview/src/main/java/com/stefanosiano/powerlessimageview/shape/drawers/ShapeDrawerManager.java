package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

/**
 * Manager class for shape drawers. Used to initialize use the needed drawers.
 */

public class ShapeDrawerManager {
    private final Paint paint;
    private BitmapShader shader;

    private float width, height;

    public ShapeDrawerManager() {
        paint = new Paint();
    }


    public void changeBitmap(Drawable drawable){
        shader = new BitmapShader(getBitmapFromDrawable(drawable), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
    }


    public void onSizeChanged(int width, int height){
        this.width = width;
        this.height = height;
    }


    /** Draws the image through the progress indicator */
    public final void onDraw(Canvas canvas) {
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
    }





    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
