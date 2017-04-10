package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.stefanosiano.powerlessimageview.shape.PivShapeMode;
import com.stefanosiano.powerlessimageview.shape.ShapeOptions;

/**
 * Manager class for shape drawers. Used to initialize use the needed drawers.
 */

public class ShapeDrawerManager implements ShapeOptions.ShapeOptionsListener {

    private final Paint paint = new Paint();

    /** Bounds in which the progress indicator will be drawn */
    private final RectF mShapeBounds;



    public void changeBitmap(Drawable drawable){
        BitmapShader shader = new BitmapShader(getBitmapFromDrawable(drawable), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
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




    //Variables used to initialize drawers

    //Drawers
    private CircularShapeDrawer mCircularShadowDrawer;


    /** Interface used to switch between its implementations, based on the shape and options selected. */
    private ShapeDrawer mShapeDrawer;

    /** Shape of the image */
    private PivShapeMode mShapeMode = null;

    /** Options used by shape drawers */
    private ShapeOptions mShapeOptions;


    /**
     * Manager class for shape drawers. Used to initialize and get the instances of the needed drawers.
     */
    public ShapeDrawerManager(final ShapeOptions shapeOptions){
        this.mShapeBounds = new RectF();
        this.mShapeOptions = shapeOptions;
        this.mShapeOptions.setListener(this);
    }


    /**
     * Updates the drawers to use and chooses the right one to use based on the mode.
     * If the drawer doesn't exist, it will be instantiated.
     *
     * @param shapeMode Mode of the shape, used to choose the right drawers.
     */
    private void updateDrawers(PivShapeMode shapeMode){

        switch (shapeMode){

            case CIRCLE:

                //shape drawer
                if(mCircularShadowDrawer == null){
                    mCircularShadowDrawer = new CircularShapeDrawer();
                    mShapeDrawer = mCircularShadowDrawer;
                }

                //front/back drawer
                break;

            default:
            case NORMAL:
                break;
        }
        //mShapeDrawer.setListener(listener);
    }


    /**
     * It calculates the bounds of the image.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     */
    public final void onSizeChanged(int w, int h) {
        mShapeOptions.calculateBounds(w, h, mShapeMode);

        onSizeUpdated(mShapeOptions);
    }


    /**
     * Changes the shape mode of the image.
     *
     * @param shapeMode mode to change the image into
     */
    public final void changeShapeMode(PivShapeMode shapeMode){
        if(mShapeMode != null && mShapeMode == shapeMode)
            return;

        mShapeMode = shapeMode;
        updateDrawers(mShapeMode);
        //mShapeDrawer.setup(mProgressOptions);
    }


    /** Draws the image */
    public final void onDraw(Canvas canvas) {
        canvas.drawOval(mShapeBounds, paint);
        //mShapeDrawer.draw(canvas, mProgressShadowBorderBounds, mProgressShadowBounds);
    }

    /**
     * @return The options of the shape
     */
    public final ShapeOptions getShapeOptions() {
        return mShapeOptions;
    }



    /**
     * Called when an option is updated. It propagates the update to the shape drawers.
     */
    @Override
    public void onOptionsUpdated(ShapeOptions options) {
        mShapeOptions = options;
        //mShapeDrawer.setup(options);
    }

    /**
     * Called when an option that changes the size of the shape is updated.
     * The bounds are calculated again, and it propagates the update to the shape drawers.
     */
    @Override
    public void onSizeUpdated(ShapeOptions options) {

        mShapeOptions = options;
        //set calculated bounds to our progress bounds
        mShapeBounds.set(0f,
                0f,
                100,
                100);
        /*
        mProgressBounds.set(
                mProgressOptions.getLeft(),
                mProgressOptions.getTop(),
                mProgressOptions.getRight(),
                mProgressOptions.getBottom());
*/
        //mShapeDrawer.setup(mProgressOptions);
    }


    /**
     * Called when the shape mode changes.
     * The drawer is updated and the right one is used.
     */
    @Override
    public void onModeUpdated(ShapeOptions options) {
        mShapeOptions = options;
        changeShapeMode(mShapeMode);
    }

    /** Saves state into a bundle. */
    public Bundle saveInstanceState() {
        Bundle bundle = new Bundle();
        //bundle.putParcelable("shape_options", mShapeOptions);
        bundle.putInt("shape_mode", mShapeMode.getValue());

        return bundle;
    }

    /** Restores state from a bundle. */
    public void restoreInstanceState(Bundle state) {
        if (state == null)
            return;

        //mShapeOptions.setOptions((ShapeOptions) state.getParcelable("shape_options"));
        PivShapeMode shapeMode = PivShapeMode.fromValue(state.getInt("shape_mode"));
        onSizeUpdated(mShapeOptions);
        changeShapeMode(shapeMode);
    }
}
