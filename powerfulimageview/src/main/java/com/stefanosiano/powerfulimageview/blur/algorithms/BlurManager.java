package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.widget.ImageView;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;
import com.stefanosiano.powerfulimageview.blur.PivBlurMode;

import java.lang.ref.WeakReference;

/**
 * Manager class for blurring. Used to manage and blur the image.
 */

public final class BlurManager implements BlurOptions.BlurOptionsListener {

    /** Drawable of the imageview to blur */
    private Drawable mDrawable;

    /** Original bitmap, downsampled if needed */
    private Bitmap mOriginalBitmap;

    /** Last blurred bitmap */
    private Bitmap mBlurredBitmap;

    /** Options to use to blur bitmap */
    private BlurOptions mBlurOptions;

    /** Mode to use to blur the image */
    private PivBlurMode mMode;

    /** Strength of the blur */
    private int mRadius;

    /** Whether the renderscript context is managed: if I added this view's context to the RenderscriptManager */
    private boolean mIsRenderscriptManaged;

    /** Whether the bitmap has been already blurred. On static blur, it will only  blur once! */
    private boolean mIsAlreadyBlurred;

    //Algorithms
    private GaussianFastBlurAlgorithm mGaussianFastBlurAlgorithm;
    private GaussianRenderscriptBlurAlgorithm mGaussianRenderscriptBlurAlgorithm;
    private DummyBlurAlgorithm mDummyBlurAlgorithm;

    /** Selected algorithm to blur the image */
    private BlurAlgorithm mBlurAlgorithm;

    /** Width of the view. Used to calculate the original bitmap */
    private int mWidth;

    /** Height of the view. Used to calculate the original bitmap */
    private int mHeight;

    /** Last width of the calculated bitmap. Used to calculate the original bitmap */
    private int mLastSizeX;

    /** Last height of the calculated bitmap. Used to calculate the original bitmap */
    private int mLastSizeY;

    /** Last radius used to blur the image. Used to avoid blurring twice again the same image with the same radius */
    private int mLastRadius;

    //Using a weakRefence to be sure to not leak memory
    private final WeakReference<ImageView> mView;

    /**
     * Manager class for blur. Used to initialize and blur the image with the right algorithm.
     *
     * @param view View to show the blurred image into
     * @param blurOptions Options of the blur
     */
    public BlurManager(ImageView view, final BlurOptions blurOptions){
        mView = new WeakReference<>(view);
        mBlurOptions = blurOptions;
        mMode = PivBlurMode.DISABLED;
        mLastRadius = -1;
        mRadius = 0;
        mWidth = 0;
        mHeight = 0;
        mLastSizeX = 0;
        mLastSizeY = 0;
        mIsRenderscriptManaged = false;
        mIsAlreadyBlurred = false;
    }

    /**
     * Method that updates the drawable and bitmap to show
     *
     * @param drawable drawable to show
     */
    public void changeDrawable(Drawable drawable) {
        Drawable mLastDrawable = mDrawable;
        Bitmap lastOriginalBitmap = mOriginalBitmap;
        this.mDrawable = drawable;
        this.mOriginalBitmap = getOriginalBitmapFromDrawable(mLastDrawable, drawable);

        if(lastOriginalBitmap != mOriginalBitmap) {
            mIsAlreadyBlurred = false;
            if(lastOriginalBitmap != null)
                lastOriginalBitmap.recycle();
            mLastRadius = -1;
        }
    }

    /**
     * Changes the blur mode of the image.
     *
     * @param blurMode mode to use to blur the image
     */
    public final void changeBlurMode(PivBlurMode blurMode, int radius){

        //If there's no change, I don't do anything
        if(blurMode == mMode && radius == mRadius)
            return;

        //updating renderscript context if needed
        if(mView.get() != null)
            removeContext(true);
        mMode = blurMode;
        if(mView.get() != null)
            addContext(mView.get().getContext(), true);

        //otherwise i need to blur the image again
        updateAlgorithms(blurMode);
        mLastRadius = -1;
        mRadius = radius;
    }


    /**
     * Blurs the image, if not already blurred.
     * To get the image call getLastBlurredBitmap()!
     *
     * @param radius Strength of the blurring
     */
    public void blur(int radius){
        mRadius = radius;

        if(mOriginalBitmap == null || mOriginalBitmap.isRecycled())
            return;

        //if I already blurred the image with this radius, I don't do anything
        if(mLastRadius == radius && mBlurredBitmap != null){
            return;
        }

        if(mIsAlreadyBlurred && mBlurOptions.isStaticBlur())
            return;

        this.mLastRadius = radius;

        if(mBlurredBitmap != null && mOriginalBitmap != mBlurredBitmap)
            mBlurredBitmap.recycle();

        Bitmap bitmap = null;
        try {
            if (!mIsRenderscriptManaged && mView.get() != null) {
                addContext(mView.get().getContext(), false);
                updateAlgorithms(mMode);
            }
            bitmap = mBlurAlgorithm.blur(mOriginalBitmap, mRadius, mBlurOptions);
            mIsAlreadyBlurred = true;

        } catch (RenderscriptException e){
            //Something wrong occurred with renderscript: fallback to java or nothing, based on option...

            //changing mode to fallback one
            mMode = getFallbackMode(mMode);

            Log.w(BlurManager.class.getSimpleName(), e.getLocalizedMessage());
            Log.w(BlurManager.class.getSimpleName(), "Falling back to another blurring method: " + mMode.name());

            updateAlgorithms(mMode);

            try {
                bitmap = mBlurAlgorithm.blur(mOriginalBitmap, mRadius, mBlurOptions);
                mIsAlreadyBlurred = true;
            } catch (RenderscriptException e1){
                bitmap = null;
            }

        }
        finally {
            if (mIsRenderscriptManaged)
                removeContext(false);
        }
        if (mBlurOptions.isStaticBlur()) {
            mOriginalBitmap = bitmap == null ? mOriginalBitmap : bitmap;
        }
        mBlurredBitmap = bitmap == null ? mBlurredBitmap : bitmap;

    }

    /** Returns the fallback mode for renderscript method, if anything goes wrong */
    private PivBlurMode getFallbackMode(PivBlurMode mode){
        if(!mBlurOptions.isUseRsFallback())
            return PivBlurMode.DISABLED;

        switch (mode){
            case GAUSSIAN_RS:
                return PivBlurMode.GAUSSIAN;
            default:
                return mode;
        }
    }


    /** Updates the saved width and height, used to calculate the blurred bitmap */
    public void onSizeChanged(int width, int height, Drawable drawable){
        this.mWidth = width;
        this.mHeight = height;
        if(drawable != null)
            changeDrawable(drawable);
    }


    /**
     * Updates the algorithm used to blur the image
     *
     * @param blurMode Algorithm to use
     */
    private void updateAlgorithms(PivBlurMode blurMode){
        RenderScript renderScript = RenderscriptManager.getRenderScript();

        switch (blurMode){

            case GAUSSIAN_RS:
                if(renderScript != null) {
                    if (mGaussianRenderscriptBlurAlgorithm == null)
                        mGaussianRenderscriptBlurAlgorithm = new GaussianRenderscriptBlurAlgorithm();
                    mBlurAlgorithm = mGaussianRenderscriptBlurAlgorithm;
                }
                //if renderscript is null, there was a problem getting it: let's use java or dummy
                else if(mBlurOptions.isUseRsFallback()){
                    if (mGaussianFastBlurAlgorithm == null)
                        mGaussianFastBlurAlgorithm = new GaussianFastBlurAlgorithm();
                    mBlurAlgorithm = mGaussianFastBlurAlgorithm;
                }
                else {
                    if(mDummyBlurAlgorithm == null)
                        mDummyBlurAlgorithm = new DummyBlurAlgorithm();
                    mBlurAlgorithm = mDummyBlurAlgorithm;
                }
                break;

            case GAUSSIAN:
                if(mGaussianFastBlurAlgorithm == null)
                    mGaussianFastBlurAlgorithm = new GaussianFastBlurAlgorithm();
                mBlurAlgorithm = mGaussianFastBlurAlgorithm;
                break;
            default:

            case DISABLED:
                if(mDummyBlurAlgorithm == null)
                    mDummyBlurAlgorithm = new DummyBlurAlgorithm();
                mBlurAlgorithm = mDummyBlurAlgorithm;
                break;
        }
        mBlurAlgorithm.setRenderscript(renderScript);
    }

    /**
     * Check if the current options require the bitmap to be blurred
     *
     * @return True if the bitmap should be blurred, false otherwise
     */
    public boolean shouldBlur(Drawable drawable, boolean checkDrawable){
        return mMode != PivBlurMode.DISABLED && mRadius > 0 &&
                (mLastRadius != mRadius || (checkDrawable && mDrawable != drawable));
    }

    /**
     * @return The blurred bitmap. If any problem occurs, the original bitmap (nullable) will be returned.
     */
    public Bitmap getLastBlurredBitmap(){
        blur(mRadius);
        return mBlurredBitmap != null ? mBlurredBitmap : mOriginalBitmap;
    }

    /**
     * @return Returns the bitmap of the drawable, downsampled if needed
     */
    private Bitmap getOriginalBitmapFromDrawable(Drawable mLastDrawable, Drawable drawable) {
        Bitmap bitmap;

        if (drawable == null || mWidth <= 0 || mHeight <= 0) {
            return null;
        }

        //bitmap size should not be bigger than the view size
        float ratio = (float) drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
        int sizeX;
        int sizeY;
        int maxWidth = (int) (Math.max(mWidth, mHeight * ratio) / mBlurOptions.getDownSamplingRate());
        int maxHeight = (int) (Math.max(mHeight, mWidth / ratio) / mBlurOptions.getDownSamplingRate());

        if (drawable.getIntrinsicWidth() > maxWidth && maxWidth > 0 && drawable.getIntrinsicHeight() > maxHeight && maxHeight > 0) {
            sizeX = maxWidth;
            sizeY = maxHeight;
        } else {
            sizeX = drawable.getIntrinsicWidth();
            sizeY = drawable.getIntrinsicHeight();
        }

        //todo improve this check: mLastDrawable == drawable  - The drawable changes always when i blur the image!
        //if i already decoded the bitmap i reuse it
        if (sizeX > 0 && sizeY > 0 && mOriginalBitmap != null && !mOriginalBitmap.isRecycled() && mLastSizeX == sizeX && mLastSizeY == sizeY && mLastDrawable == drawable)
            return mOriginalBitmap;

        mLastSizeX = sizeX;
        mLastSizeY = sizeY;


        try {

            if (drawable instanceof BitmapDrawable) {
                bitmap = Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888);
            } else if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            } else {

                bitmap = Bitmap.createBitmap(sizeX, sizeY, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return mOriginalBitmap;
        }

    }


    /**
     * If the blur is static renderscript context is managed by the manager itself, to release it as soon as possible.
     * If the blur is not static renderscript context is managed by the view itself, to keep it as long as it needs.
     *
     * @param context context to get the renderscript from
     * @param fromView If the function was called by the view
     */
    public void addContext(Context context, boolean fromView){
        if(context != null && mBlurOptions.isStaticBlur() != fromView) {
            switch (mMode) {
                case GAUSSIAN_RS:
                    mIsRenderscriptManaged = true;
                    RenderscriptManager.addContext(context.getApplicationContext());
                    updateAlgorithms(mMode);
                    break;

                case GAUSSIAN:
                case DISABLED:
                default:
                    break;
            }
        }
    }

    /**
     * If the blur is static renderscript context is managed by the manager itself, to release it as soon as possible.
     * If the blur is not static renderscript context is managed by the view itself, to keep it as long as it needs.
     *
     * @param fromView If the function was called by the view
     */
    public void removeContext(boolean fromView){
        if(mBlurOptions.isStaticBlur() != fromView) {
            switch (mMode) {
                case GAUSSIAN_RS:
                    mIsRenderscriptManaged = false;
                    RenderscriptManager.removeContext();
                    updateAlgorithms(mMode);
                    break;

                case GAUSSIAN:
                case DISABLED:
                default:
                    break;
            }
        }
    }

    @Override
    public void onStaticBlurChanged() {
        //If staticBlur is true, i release original bitmap and swap it with the blurred one, if it exists
        if(mBlurOptions.isStaticBlur()) {
            if (mBlurredBitmap != null && mBlurredBitmap != mOriginalBitmap) {
                mOriginalBitmap.recycle();
                mOriginalBitmap = mBlurredBitmap;
                mBlurredBitmap = null;
            }
            if (mIsRenderscriptManaged) {
                removeContext(false);
            }
        }
        else {
            if(mIsRenderscriptManaged && mView.get() != null) {
                addContext(mView.get().getContext(), true);
            }
        }
    }

    @Override
    public void onDownsamplingRateChanged() {
        //if downSampling rate changes, i reload the bitmap and blur it
        changeDrawable(mDrawable);
        blur(mRadius);
        if(mView.get() != null) {
            Bitmap bitmap = getLastBlurredBitmap();
            if(bitmap != null)
                mView.get().setImageBitmap(getLastBlurredBitmap());
        }
    }
}
