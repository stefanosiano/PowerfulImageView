package com.stefanosiano.powerlessimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
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

    /** Bounds of the shape */
    private final RectF mShapeBounds;

    /** Bounds of the image */
    private final RectF mImageBounds;

    /** Matrix used by the drawers to scale the image */
    private final Matrix mShaderMatrix;

    /** Custom matrix used with MATRIX scale type */
    private Matrix mImageMatrix;

    /** Scale type of the image */
    private ImageView.ScaleType mScaleType;

    /** Bitmap to be drawn */
    private Bitmap mBitmap;

    /** Drawable of the view */
    private Drawable mDrawable;

    /** Measured width, based on mode */
    private float mMeasuredWidth;

    /** Measured height, based on mode */
    private float mMeasuredHeight;


    //Drawers
    private CircleShapeDrawer mCircleShapeDrawer;
    private NormalShapeDrawer mNormalShapeDrawer;


    /** Interface used to switch between its implementations, based on the shape and options selected. */
    private ShapeDrawer mShapeDrawer;

    /** Shape mode of the image */
    private PivShapeMode mShapeMode = null;

    /** Options used by shape drawers */
    private ShapeOptions mShapeOptions;



    /**
     * Manager class for shape drawers. Used to initialize and get the instances of the needed drawers.
     *
     * @param view View to show the image into
     * @param shapeOptions Options of the shape
     */
    public ShapeDrawerManager(View view, final ShapeOptions shapeOptions){
        this.mView = new WeakReference<>(view);
        this.mShapeBounds = new RectF();
        this.mImageBounds = new RectF();
        this.mShapeOptions = shapeOptions;
        this.mShapeOptions.setListener(this);
        this.mShaderMatrix = new Matrix();
        this.mShaderMatrix.reset();
        this.mBitmap = null;
        this.mShapeDrawer = new NormalShapeDrawer(null);
    }


    /**
     * Method that updates the drawable and bitmap to show
     *
     * @param drawable drawable to show on normal, square and rectangle shapes
     * @param bitmap bitmap to show on rounded, circle and oval shapes
     */
    public void changeBitmap(Drawable drawable, Bitmap bitmap){
        this.mDrawable = drawable;
        this.mBitmap = bitmap;
        mShapeDrawer.changeBitmap(drawable, bitmap);
    }


    /**
     * Updates the drawers to use and chooses the right one to use based on the mode.
     * If the drawer doesn't exist, it will be instantiated.
     *
     * @param shapeMode Mode of the shape, used to choose the right drawers.
     */
    private void updateDrawers(PivShapeMode shapeMode){

        //If there's no mode, i set it as normal
        if(shapeMode == null)
            shapeMode = PivShapeMode.NORMAL;

        switch (shapeMode){

            case CIRCLE:

                if(mCircleShapeDrawer == null)
                    mCircleShapeDrawer = new CircleShapeDrawer(mBitmap);
                mShapeDrawer = mCircleShapeDrawer;
                break;

            case SQUARE:

                if(mNormalShapeDrawer == null)
                    mNormalShapeDrawer = new NormalShapeDrawer(mDrawable);
                mShapeDrawer = mNormalShapeDrawer;
                break;

            case RECTANGLE:

                if(mNormalShapeDrawer == null)
                    mNormalShapeDrawer = new NormalShapeDrawer(mDrawable);
                mShapeDrawer = mNormalShapeDrawer;
                break;

            case OVAL:

                if(mNormalShapeDrawer == null)
                    mNormalShapeDrawer = new NormalShapeDrawer(mDrawable);
                mShapeDrawer = mNormalShapeDrawer;
                break;

            case ROUNDED_RECTANGLE:

                if(mNormalShapeDrawer == null)
                    mNormalShapeDrawer = new NormalShapeDrawer(mDrawable);
                mShapeDrawer = mNormalShapeDrawer;
                break;

            case SOLID_CIRCLE:

                if(mNormalShapeDrawer == null)
                    mNormalShapeDrawer = new NormalShapeDrawer(mDrawable);
                mShapeDrawer = mNormalShapeDrawer;
                break;

            case SOLID_OVAL:

                if(mNormalShapeDrawer == null)
                    mNormalShapeDrawer = new NormalShapeDrawer(mDrawable);
                mShapeDrawer = mNormalShapeDrawer;
                break;

            case SOLID_ROUNDED_RECTANGLE:

                if(mNormalShapeDrawer == null)
                    mNormalShapeDrawer = new NormalShapeDrawer(mDrawable);
                mShapeDrawer = mNormalShapeDrawer;
                break;

            default:
            case NORMAL:

                if(mNormalShapeDrawer == null)
                    mNormalShapeDrawer = new NormalShapeDrawer(mDrawable);
                mShapeDrawer = mNormalShapeDrawer;
                break;
        }
    }


    /**
     * It calculates the bounds of the image.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     */
    public final void onSizeChanged(int w, int h, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        mShapeOptions.calculateBounds(w, h, paddingLeft, paddingTop, paddingRight, paddingBottom, mShapeMode);

        onSizeUpdated(mShapeOptions);
    }


    /**
     * Measure the view and its content to determine the measured width and the measured height
     */
    public void onMeasure(float w, float h, int wMode, int hMode, View view){

        // EXACTLY: size value was set to a specific value. This can also get triggered when match_parent
        // is used, to set the size exactly to the parent view (this is layout dependent).

        // AT_MOST: size value was set to match_parent or wrap_content where a maximum size is needed
        // (this is layout dependent). You should not be any larger than this size.

        // UNSPECIFIED: size value was set to wrap_content with no restrictions. You can be whatever
        // size you would like. Some layouts also use this callback to figure out your desired size
        // before determine what specs to actually pass you again in a second measure request.

        //Drawable width and height calculated if a mDrawable has been set. Used in calculations
        float drawableWidth = mDrawable == null ? 0 : mDrawable.getIntrinsicWidth() + view.getPaddingLeft() + view.getPaddingRight();
        float drawableHeight = mDrawable == null ? 0 : mDrawable.getIntrinsicHeight() + view.getPaddingTop() + view.getPaddingBottom();

        float usedRatio;
        switch (mShapeMode){
            case CIRCLE:
            case SQUARE:
            case SOLID_CIRCLE:
                usedRatio = 1f;
                break;
            case RECTANGLE:
            case ROUNDED_RECTANGLE:
            case SOLID_ROUNDED_RECTANGLE:
            case OVAL:
            case SOLID_OVAL:
                default:
                usedRatio = mShapeOptions.getRatio();
        }


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
                    //if both are wrap_content, size should be mDrawable size
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

    /**
     * Sets the custom matrix to be used with the MATRIX scale type
     *
     * @param matrix Custom matrix to be used with the MATRIX scale type
     */
    public void setImageMatrix(Matrix matrix){
        this.mImageMatrix = matrix;
        setScaleType(ImageView.ScaleType.MATRIX);
    }

    /**
     * Sets the scale type used to draw the image
     *
     * @param scaleType Scale type used to draw the image
     */
    public void setScaleType(ImageView.ScaleType scaleType){

        mScaleType = scaleType;

        if(mImageBounds == null || mDrawable == null || scaleType == null)
            return;

        mShaderMatrix.reset();

        int dWidth = mDrawable.getIntrinsicWidth();
        int dHeight = mDrawable.getIntrinsicHeight();
        float vWidth = mImageBounds.width();
        float vHeight = mImageBounds.height();
        Rect padding = new Rect(0, 0, 0, 0);

        if(mView.get() != null){
            padding.set(
                    mView.get().getPaddingLeft(),
                    mView.get().getPaddingBottom(),
                    mView.get().getPaddingRight(),
                    mView.get().getPaddingTop());
        }

        float scale, dx, dy;

        switch (scaleType) {

            case CENTER_CROP:
                if (dWidth * vHeight > vWidth * dHeight) {
                    scale = vHeight / (float) dHeight;
                    dx = (vWidth - dWidth * scale) * 0.5f;
                    dy = 0;

                } else {
                    scale = vWidth / (float) dWidth;
                    dy = (vHeight - dHeight * scale) * 0.5f;
                    dx = 0;
                }

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate(dx + mImageBounds.left,
                        dy + mImageBounds.top);
                break;

            case CENTER_INSIDE:
                if (dWidth <= vWidth && dHeight <= vHeight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min(vWidth / (float) dWidth,
                            vHeight / (float) dHeight);
                }

                dx = (vWidth - dWidth * scale) * 0.5f;
                dy = (vHeight - dHeight * scale) * 0.5f;

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate(dx + mImageBounds.left,
                        dy + mImageBounds.top);
                break;

            case FIT_CENTER:
                mShaderMatrix.setRectToRect(new RectF(0, 0, dWidth, dHeight), mImageBounds, Matrix.ScaleToFit.CENTER);
                break;

            case FIT_END:
                mShaderMatrix.setRectToRect(new RectF(0, 0, dWidth, dHeight), mImageBounds, Matrix.ScaleToFit.END);
                break;

            case FIT_START:
                mShaderMatrix.setRectToRect(new RectF(0, 0, dWidth, dHeight), mImageBounds, Matrix.ScaleToFit.START);
                break;

            case FIT_XY:
                mShaderMatrix.setRectToRect(new RectF(0, 0, dWidth, dHeight), mImageBounds, Matrix.ScaleToFit.FILL);
                break;

            case MATRIX:
                if(mImageMatrix != null)
                    mShaderMatrix.set(mImageMatrix);

                mShaderMatrix.postTranslate(mImageBounds.left,
                        mImageBounds.top);
                break;

            default:
            case CENTER:

                mShaderMatrix.setTranslate(
                        ((vWidth - dWidth) * 0.5f + mImageBounds.left),
                        ((vHeight - dHeight) * 0.5f + mImageBounds.top));
                break;
        }

        mShapeDrawer.setMatrix(scaleType, mShaderMatrix);
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
        mShapeDrawer.setup(mShapeOptions);
    }

    /** Draws the image */
    public final void onDraw(Canvas canvas) {
        mShapeDrawer.draw(canvas, mShapeBounds, mImageBounds);
    }

    /**
     * @return The options of the shape
     */
    public final ShapeOptions getShapeOptions() {
        return mShapeOptions;
    }


    /**
     * Called when an option that requires onMeasure() call is updated.
     */
    @Override
    public void onRequestMeasure(ShapeOptions options) {
        mShapeOptions = options;
        if(mView.get() != null)
            mView.get().requestLayout();
        mShapeDrawer.setup(options);
    }

    /**
     * Called when an option is updated. It propagates the update to the shape drawers.
     */
    @Override
    public void onOptionsUpdated(ShapeOptions options) {
        mShapeOptions = options;
        mShapeDrawer.setup(options);
    }

    /**
     * Called when an option that changes the size of the shape is updated.
     * The bounds are calculated again, and it propagates the update to the shape drawers.
     */
    @Override
    public void onSizeUpdated(ShapeOptions options) {

        mShapeOptions = options;
        //set calculated bounds to our progress bounds
        mShapeBounds.set(mShapeOptions.getShapeBounds());
        mImageBounds.set(mShapeOptions.getImageBounds());

        setScaleType(mScaleType);

        mShapeDrawer.setup(mShapeOptions);
    }


    /**
     * Returns the measured height to be used in onMeasure() method of the view.
     * It is calculated based on the shape of the image and its size.
     *
     * @return Measured height to be used in onMeasure() method of the view.
     */
    public int getMeasuredHeight() {
        return (int) mMeasuredHeight;
    }

    /**
     * Returns the measured width to be used in onMeasure() method of the view.
     * It is calculated based on the shape of the image and its size.
     *
     * @return Measured width to be used in onMeasure() method of the view.
     */
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
        bundle.putParcelable("shape_options", mShapeOptions);
        bundle.putInt("shape_mode", mShapeMode.getValue());

        return bundle;
    }

    /** Restores state from a bundle. */
    public void restoreInstanceState(Bundle state) {
        if (state == null)
            return;

        mShapeOptions.setOptions((ShapeOptions) state.getParcelable("shape_options"));
        PivShapeMode shapeMode = PivShapeMode.fromValue(state.getInt("shape_mode"));
        onSizeUpdated(mShapeOptions);
        changeShapeMode(shapeMode);
    }
}
