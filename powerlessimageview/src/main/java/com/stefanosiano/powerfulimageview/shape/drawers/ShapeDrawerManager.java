package com.stefanosiano.powerfulimageview.shape.drawers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.stefanosiano.powerfulimageview.shape.PivShapeMode;
import com.stefanosiano.powerfulimageview.shape.PivShapeScaleType;
import com.stefanosiano.powerfulimageview.shape.ShapeOptions;

import java.lang.ref.WeakReference;

/**
 * Manager class for shape drawers. Used to initialize use the needed drawers.
 */

public class ShapeDrawerManager implements ShapeOptions.ShapeOptionsListener {

    //Using a weakRefence to be sure to not leak memory
    private final WeakReference<View> mView;

    /** Bounds of the shape */
    private final RectF mShapeBounds;

    /** Bounds of the shape border */
    private final RectF mBorderBounds;

    /** Bounds of the image */
    private final RectF mImageBounds;

    /** Matrix used by the drawers to scale the image */
    private final Matrix mShaderMatrix;

    /** Custom matrix used with MATRIX scale type */
    private Matrix mImageMatrix;

    /** Scale type of the image */
    private PivShapeScaleType mScaleType;

    /** Drawable of the view */
    private Drawable mDrawable;

    /** Last bitmap calculated (i reuse this, so I don't decode it again) */
    private Bitmap mLastBitmap;

    /** Measured width, based on mode */
    private float mMeasuredWidth;

    /** Measured height, based on mode */
    private float mMeasuredHeight;


    //Drawers
    private CircleShapeDrawer mCircleShapeDrawer;
    private NormalShapeDrawer mNormalShapeDrawer;
    private OvalShapeDrawer mOvalShapeDrawer;
    private SolidCircleShapeDrawer mSolidCircleShapeDrawer;
    private RoundedRectangleShapeDrawer mRoundedRectangleShapeDrawer;
    private SolidOvalShapeDrawer mSolidOvalShapeDrawer;
    private SolidRoundedRectangleShapeDrawer mSolidRoundedRectangleShapeDrawer;


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
        this.mBorderBounds = new RectF();
        this.mImageBounds = new RectF();
        this.mShapeOptions = shapeOptions;
        this.mShapeOptions.setListener(this);
        this.mShaderMatrix = new Matrix();
        this.mShaderMatrix.reset();
        this.mShapeDrawer = new NormalShapeDrawer(null);
    }


    /**
     * Method that updates the drawable and bitmap to show
     *
     * @param drawable drawable to show
     */
    public void changeDrawable(Drawable drawable){
        Drawable mLastDrawable = mDrawable;
        this.mDrawable = drawable;
        mShapeDrawer.changeDrawable(drawable);
        if(mShapeDrawer.requireBitmap()) {
            mLastBitmap = getBitmapFromDrawable(mLastDrawable, drawable);
            mShapeDrawer.changeBitmap(mLastBitmap);
        }
        else
            mLastBitmap = null;
        setScaleType(mScaleType);
    }

    /**
     * @return Returns the bitmap of the drawable
     */
    private Bitmap getBitmapFromDrawable(Drawable mLastDrawable, Drawable drawable) {
        if (drawable == null || mMeasuredWidth <= 0 || mMeasuredHeight <= 0) {
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
                //bitmap size should not be bigger than the view size
                float ratio = (float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
                int sizeX;
                int sizeY;
                int maxWidth = (int) Math.max(mMeasuredWidth, mMeasuredHeight * ratio);
                int maxHeight = (int) Math.max(mMeasuredHeight, mMeasuredWidth / ratio);

                if(drawable.getIntrinsicWidth() > maxWidth && maxWidth > 0 && drawable.getIntrinsicHeight() > maxHeight && maxHeight > 0){
                    sizeX = maxWidth;
                    sizeY = maxHeight;
                }
                else {
                    sizeX = drawable.getIntrinsicWidth();
                    sizeY = drawable.getIntrinsicHeight();
                }

                //vector drawables should always display at max resolution
                if(drawable.getClass().getName().equals("android.graphics.drawable.VectorDrawable")){
                    sizeX = maxWidth;
                    sizeY = maxHeight;
                }

                //if i already decoded the bitmap i reuse it
                if(sizeX > 0 && sizeY > 0 && mLastBitmap != null && mLastDrawable == mDrawable)
                    return mLastBitmap;

                //otherwise I free its memory
                if(mLastBitmap != null)
                    mLastBitmap.recycle();

                bitmap = Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
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
                    mCircleShapeDrawer = new CircleShapeDrawer(getBitmapFromDrawable(mDrawable, mDrawable));
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

                if(mOvalShapeDrawer == null)
                    mOvalShapeDrawer = new OvalShapeDrawer(getBitmapFromDrawable(mDrawable, mDrawable));
                mShapeDrawer = mOvalShapeDrawer;
                break;

            case ROUNDED_RECTANGLE:

                if(mRoundedRectangleShapeDrawer == null)
                    mRoundedRectangleShapeDrawer = new RoundedRectangleShapeDrawer(getBitmapFromDrawable(mDrawable, mDrawable));
                mShapeDrawer = mRoundedRectangleShapeDrawer;
                break;

            case SOLID_CIRCLE:

                if(mSolidCircleShapeDrawer == null)
                    mSolidCircleShapeDrawer = new SolidCircleShapeDrawer(mDrawable);
                mShapeDrawer = mSolidCircleShapeDrawer;
                break;

            case SOLID_OVAL:

                if(mSolidOvalShapeDrawer == null)
                    mSolidOvalShapeDrawer = new SolidOvalShapeDrawer(mDrawable);
                mShapeDrawer = mSolidOvalShapeDrawer;
                break;

            case SOLID_ROUNDED_RECTANGLE:

                if(mSolidRoundedRectangleShapeDrawer == null)
                    mSolidRoundedRectangleShapeDrawer = new SolidRoundedRectangleShapeDrawer(mDrawable);
                mShapeDrawer = mSolidRoundedRectangleShapeDrawer;
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
                usedRatio = mShapeOptions.getRatio() <= 0 ? w / h : mShapeOptions.getRatio();
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
        setScaleType(PivShapeScaleType.MATRIX);
    }

    /**
     * Sets the scale type used to draw the image
     *
     * @param scaleType Scale type used to draw the image
     */
    public void setScaleType(PivShapeScaleType scaleType){

        mScaleType = scaleType;

        if(mImageBounds == null || mDrawable == null || scaleType == null)
            return;

        mShaderMatrix.reset();

        //scale used for vector drawable fix (other bitmaps have dwidth = bitmap width)
        float scaleX = 1;
        float scaleY = 1;
        int dWidth = mDrawable.getIntrinsicWidth();
        int dHeight = mDrawable.getIntrinsicHeight();

        if(mLastBitmap != null){
            scaleX = (float) dWidth / (float) mLastBitmap.getWidth();
            scaleY = (float) dHeight / (float) mLastBitmap.getHeight();
        }

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

                mShaderMatrix.setScale(scale * scaleX, scale * scaleY);
                mShaderMatrix.postTranslate(dx + mImageBounds.left,
                        dy + mImageBounds.top);
                break;

            case TOP_CROP:
                if (dWidth * vHeight > vWidth * dHeight) {
                    scale = vHeight / (float) dHeight;
                    dx = (vWidth - dWidth * scale) * 0.5f;
                    dy = 0;

                } else {
                    scale = vWidth / (float) dWidth;
                    dy = 0;
                    dx = 0;
                }

                mShaderMatrix.setScale(scale * scaleX, scale * scaleY);
                mShaderMatrix.postTranslate(dx + mImageBounds.left,
                        dy + mImageBounds.top);
                break;

            case BOTTOM_CROP:
                if (dWidth * vHeight > vWidth * dHeight) {
                    scale = vHeight / (float) dHeight;
                    dx = (vWidth - dWidth * scale) * 0.5f;
                    dy = 0;

                } else {
                    scale = vWidth / (float) dWidth;
                    dy = vHeight - dHeight * scale;
                    dx = 0;
                }

                mShaderMatrix.setScale(scale * scaleX, scale * scaleY);
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

                mShaderMatrix.setScale(scale * scaleX, scale * scaleY);

                mShaderMatrix.postTranslate(dx + mImageBounds.left,
                        dy + mImageBounds.top);
                break;

            case FIT_CENTER:

                if(mLastBitmap != null)
                    mShaderMatrix.setRectToRect(new RectF(0, 0, mLastBitmap.getWidth(), mLastBitmap.getHeight()), mImageBounds, Matrix.ScaleToFit.CENTER);
                else
                    mShaderMatrix.setRectToRect(new RectF(0, 0, dWidth, dHeight), mImageBounds, Matrix.ScaleToFit.CENTER);
                break;

            case FIT_END:

                if(mLastBitmap != null)
                    mShaderMatrix.setRectToRect(new RectF(0, 0, mLastBitmap.getWidth(), mLastBitmap.getHeight()), mImageBounds, Matrix.ScaleToFit.END);
                else
                    mShaderMatrix.setRectToRect(new RectF(0, 0, dWidth, dHeight), mImageBounds, Matrix.ScaleToFit.END);
                break;

            case FIT_START:

                if(mLastBitmap != null)
                    mShaderMatrix.setRectToRect(new RectF(0, 0, mLastBitmap.getWidth(), mLastBitmap.getHeight()), mImageBounds, Matrix.ScaleToFit.START);
                else
                    mShaderMatrix.setRectToRect(new RectF(0, 0, dWidth, dHeight), mImageBounds, Matrix.ScaleToFit.START);
                break;

            case FIT_XY:

                if(mLastBitmap != null)
                    mShaderMatrix.setRectToRect(new RectF(0, 0, mLastBitmap.getWidth(), mLastBitmap.getHeight()), mImageBounds, Matrix.ScaleToFit.FILL);
                else
                    mShaderMatrix.setRectToRect(new RectF(0, 0, dWidth, dHeight), mImageBounds, Matrix.ScaleToFit.FILL);
                break;

            case MATRIX:
                mShaderMatrix.preScale(scaleX, scaleY);

                if(mImageMatrix != null)
                    mShaderMatrix.set(mImageMatrix);

                mShaderMatrix.postTranslate(mImageBounds.left,
                        mImageBounds.top);
                break;

            default:
            case CENTER:

                mShaderMatrix.setScale(scaleX, scaleY);
                mShaderMatrix.postTranslate(
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
        if(mView.get() != null)
            mView.get().postInvalidate();
    }

    /** Draws the image */
    public final void onDraw(Canvas canvas) {
        mShapeDrawer.draw(canvas, mBorderBounds, mShapeBounds, mImageBounds);
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
        if(mView.get() != null)
            mView.get().postInvalidate();
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
        mBorderBounds.set(mShapeOptions.getBorderBounds());
        mImageBounds.set(mShapeOptions.getImageBounds());

        changeDrawable(mDrawable);

        mShapeDrawer.setup(mShapeOptions);
        if(mView.get() != null)
            mView.get().postInvalidate();
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
