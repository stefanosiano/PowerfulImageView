package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.stefanosiano.powerlessimageview.shape.PivShapeMode;
import com.stefanosiano.powerlessimageview.shape.ShapeOptions;

import java.lang.ref.WeakReference;

/**
 * Manager class for shape drawers. Used to initialize use the needed drawers.
 */

public class ShapeDrawerManager implements ShapeOptions.ShapeOptionsListener {

    //Using a weakRefence to be sure to not leak memory
    private final WeakReference<View> mView;

    private final Paint paint = new Paint();

    /** Bounds in which the progress indicator will be drawn */
    private final RectF mShapeBounds;

    private BitmapShader shader;
    private final Matrix mShaderMatrix;
    private ImageView.ScaleType mScaleType;

    private Drawable drawable;

    private float mMeasuredWidth;
    private float mMeasuredHeight;


    //Variables used to initialize drawers
    private ShapeDrawerListener listener;

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
     *
     * @param view View to show the image into
     */
    public ShapeDrawerManager(View view, final ShapeOptions shapeOptions){
        this.mView = new WeakReference<>(view);
        this.mShapeBounds = new RectF();
        this.mShapeOptions = shapeOptions;
        this.mShapeOptions.setListener(this);
        this.mShaderMatrix = new Matrix();
        this.mShaderMatrix.reset();
        this.listener = new ShapeDrawerListener() {
            @Override
            public void onRequestInvalidate() {

                if(mView.get() != null) {
                    //invalidates only the area of the progress indicator, instead of the whole view. +1 e -1 are used to be sure to invalidate the whole progress indicator
                    //It is more efficient then just postInvalidate(): if something is drawn outside the bounds, it will not be calculated again!
                    mView.get().postInvalidate((int) mShapeBounds.left - 1, (int) mShapeBounds.top - 1, (int) mShapeBounds.right + 1, (int) mShapeBounds.bottom + 1);
                }
            }
        };
    }


    public void changeBitmap(Drawable drawable, Bitmap bitmap){
        this.drawable = drawable;
        shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
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


    public void onMeasure(float w, float h, int wMode, int hMode, View view){

        // EXACTLY: size value was set to a specific value. This can also get triggered when match_parent
        // is used, to set the size exactly to the parent view (this is layout dependent).

        // AT_MOST: size value was set to match_parent or wrap_content where a maximum size is needed
        // (this is layout dependent). You should not be any larger than this size.

        // UNSPECIFIED: size value was set to wrap_content with no restrictions. You can be whatever
        // size you would like. Some layouts also use this callback to figure out your desired size
        // before determine what specs to actually pass you again in a second measure request.

        //Drawable width and height calculated if a drawable has been set. Used in calculations
        float drawableWidth = drawable == null ? 0 : drawable.getIntrinsicWidth() + view.getPaddingLeft() + view.getPaddingRight();
        float drawableHeight = drawable == null ? 0 : drawable.getIntrinsicHeight() + view.getPaddingTop() + view.getPaddingBottom();

        float usedRatio = 1f;


        switch (wMode){

            //Must be this size
            case View.MeasureSpec.EXACTLY:
                mMeasuredWidth = w;
                mMeasuredHeight = w / usedRatio;

                if (hMode == View.MeasureSpec.EXACTLY) {
                    mMeasuredHeight = h;
                }

                if (hMode == View.MeasureSpec.AT_MOST){
                    mMeasuredHeight = Math.min(w / usedRatio, h);
                }

                if(hMode == View.MeasureSpec.UNSPECIFIED){
                    mMeasuredHeight = w / usedRatio;
                }

                break;

            //Can't be bigger than...
            case View.MeasureSpec.AT_MOST:

                mMeasuredWidth = w;
                mMeasuredHeight = w / usedRatio;

                if (hMode == View.MeasureSpec.EXACTLY) {
                    mMeasuredWidth = Math.min(h * usedRatio, w);
                    mMeasuredHeight = h;
                }

                if (hMode == View.MeasureSpec.AT_MOST) {
                    //if both are wrap_content, size should be drawable size
                    w = drawableWidth > 0 ? Math.min(drawableWidth, w) : w;
                    h = drawableHeight > 0 ? Math.min(drawableHeight, h) : h;
                    mMeasuredWidth = Math.min(h * usedRatio, w);
                    mMeasuredHeight = Math.min(h, w / usedRatio);
                }

                if(hMode == View.MeasureSpec.UNSPECIFIED) {
                    w = drawableWidth > 0 ? Math.min(drawableWidth, w) : w;
                    mMeasuredWidth = w;
                    mMeasuredHeight = w / usedRatio;
                }

                break;

            //Be whatever you want
            default:
            case View.MeasureSpec.UNSPECIFIED:
                w = drawableWidth > 0 ? drawableWidth : w;
                mMeasuredWidth = w;
                mMeasuredHeight = w / usedRatio;

                if (hMode == View.MeasureSpec.EXACTLY) {
                    mMeasuredWidth = h * usedRatio;
                    mMeasuredHeight = h;
                }

                if (hMode == View.MeasureSpec.AT_MOST) {
                    h = drawableHeight > 0 ? Math.min(drawableHeight, h) : h;
                    mMeasuredWidth = h * usedRatio;
                    mMeasuredHeight = h;
                }

                if(hMode == View.MeasureSpec.UNSPECIFIED) {
                    w = drawableWidth > 0 ? drawableWidth : w;
                    mMeasuredWidth = w;
                    mMeasuredHeight = w / usedRatio;
                }

                break;
        }

    }


    public void setScaleType(ImageView.ScaleType scaleType){

        mScaleType = scaleType;

        if(mShapeBounds == null || drawable == null || scaleType == null)
            return;

        mShaderMatrix.reset();

        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();

        float scale = 0;
        float dx = 0;
        float dy = 0;

        switch (scaleType) {

            case CENTER_CROP:
                if (dWidth * mShapeBounds.height() > mShapeBounds.width() * dHeight) {
                    scale = mShapeBounds.height() / (float) dHeight;
                    dx = (mShapeBounds.width() - dWidth * scale) * 0.5f;
                    dy = 0;

                } else {
                    scale = mShapeBounds.width() / (float) dWidth;
                    dy = (mShapeBounds.height() - dHeight * scale) * 0.5f;
                    dx = 0;
                }

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate(dx + mShapeBounds.left,
                        dy + mShapeBounds.top);
                break;

            case CENTER_INSIDE:
                if (dWidth <= mShapeBounds.width() && dHeight <= mShapeBounds.height()) {
                    scale = 1.0f;
                } else {
                    scale = Math.min(mShapeBounds.width() / (float) dWidth,
                            mShapeBounds.height() / (float) dHeight);
                }

                dx = (mShapeBounds.width() - dWidth * scale) * 0.5f;
                dy = (mShapeBounds.height() - dHeight * scale) * 0.5f;

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate(dx + mShapeBounds.left,
                        dy + mShapeBounds.top);
                break;

            case FIT_CENTER:
                mShaderMatrix.setRectToRect(new RectF(drawable.getBounds()), mShapeBounds, Matrix.ScaleToFit.CENTER);
                break;

            case FIT_END:
                mShaderMatrix.setRectToRect(new RectF(drawable.getBounds()), mShapeBounds, Matrix.ScaleToFit.END);
                break;

            case FIT_START:
                mShaderMatrix.setRectToRect(new RectF(drawable.getBounds()), mShapeBounds, Matrix.ScaleToFit.START);
                break;

            case FIT_XY:
                mShaderMatrix.setRectToRect(new RectF(drawable.getBounds()), mShapeBounds, Matrix.ScaleToFit.FILL);
                break;

            default:
            case CENTER:
                mShaderMatrix.setTranslate(
                        ((mShapeBounds.width() - dWidth) * 0.5f + mShapeBounds.left),
                        ((mShapeBounds.height() - dHeight) * 0.5f + mShapeBounds.top));
                break;
        }

        shader.setLocalMatrix(mShaderMatrix);
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



    interface ShapeDrawerListener{
        /** Request to invalidate the image bounds */
        void onRequestInvalidate();
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
        mShapeBounds.set(
                mShapeOptions.getLeft(),
                mShapeOptions.getTop(),
                mShapeOptions.getRight(),
                mShapeOptions.getBottom());

        setScaleType(mScaleType);

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


    public int getMeasuredHeight() {
        return (int) mMeasuredHeight;
    }

    public int getMeasuredWidth() {
        return (int) mMeasuredWidth;
    }

    /**
     * @return The shape selected mode
     */
    public PivShapeMode getShapeMode() {
        return mShapeMode;
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
