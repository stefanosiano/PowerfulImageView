package com.stefanosiano.powerfulimageview.progress.drawers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;

import com.stefanosiano.powerfulimageview.PowerfulImageView;
import com.stefanosiano.powerfulimageview.R;
import com.stefanosiano.powerfulimageview.progress.PivProgressMode;
import com.stefanosiano.powerfulimageview.progress.ProgressOptions;

import java.lang.ref.WeakReference;

/**
 * Manager class for progress drawers. Used to initialize and get the instances of the needed drawers.
 */

public final class ProgressDrawerManager implements ProgressOptions.ProgressOptionsListener {


    //Variables used to initialize drawers

    //Using a weakRefence to be sure to not leak memory
    private final WeakReference<PowerfulImageView> mPiv;

    /** Bounds in which the progress indicator will be drawn */
    private final RectF mProgressBounds;

    /** Bounds in which the progress indicator will be drawn */
    private final RectF mProgressCancelBounds;

    //Drawers
    private DummyProgressDrawer mDummyProgressDrawer;
    private DeterminateProgressDrawer mDeterminateProgressDrawer;
    private DeterminateHorizontalProgressDrawer mDeterminateHorizontalProgressDrawer;
    private IndeterminateHorizontalProgressDrawer mIndeterminateHorizontalProgressDrawer;
    private IndeterminateProgressDrawer mIndeterminateProgressDrawer;

    /** Interface used to switch between its implementations, based on the progress mode selected. */
    private ProgressDrawer mProgressDrawer;

    /** Mode of the progress drawer */
    private PivProgressMode mProgressMode = null;

    /** Options used by progress drawers */
    private ProgressOptions mProgressOptions;

    /** Listener to handle things from drawers */
    private ProgressDrawerListener listener;


    /**
     * Manager class for progress drawers. Used to initialize and get the instances of the needed drawers.
     *
     * @param piv View to show progress indicator into
     */
    public ProgressDrawerManager(PowerfulImageView piv, final ProgressOptions progressOptions){
        this.mPiv = new WeakReference<>(piv);
        this.mProgressBounds = new RectF();
        this.mProgressCancelBounds = new RectF();
        this.mProgressOptions = progressOptions;
        this.listener = new ProgressDrawerListener() {
            @Override
            public void onRequestInvalidate() {

                if(mPiv.get() != null) {
                    //invalidates only the area of the progress indicator, instead of the whole view. +1 e -1 are used to be sure to invalidate the whole progress indicator
                    //It is more efficient then just postInvalidate(): if something is drawn outside the bounds, it will not be calculated again!
                    mPiv.get().postInvalidate((int) mProgressBounds.left - 1, (int) mProgressBounds.top - 1, (int) mProgressBounds.right + 1, (int) mProgressBounds.bottom + 1);
                }
            }
        };
        this.mProgressOptions.setListener(this);
    }


    /**
     * Gets the instance of the progress drawer to use.
     * If the drawer doesn't exist, it will be instantiated and returned.
     *
     * @param progressMode Mode of the progress, used to choose the right drawer.
     * @return A ProgressDrawer chosen based on the mode. It will never return null.
     */
    private ProgressDrawer getDrawer(PivProgressMode progressMode){
        switch (progressMode){

            case INDETERMINATE:
                if(mIndeterminateProgressDrawer == null)
                    this.mIndeterminateProgressDrawer = new IndeterminateProgressDrawer();
                mProgressDrawer = mIndeterminateProgressDrawer;
                break;

            case DETERMINATE:
                Bitmap bitmap;
                try {
                    bitmap = getBitmapFromDrawable(mPiv.get().getContext(), R.drawable.ic_clear_white);
                    /*
                    Drawable vectorDrawable = ContextCompat.getDrawable(mPiv.get().getContext().getApplicationContext(), R.drawable.ic_clear_white);
                    bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                            vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    vectorDrawable.draw(canvas);
*/
                } catch (OutOfMemoryError e) {
                    // Handle the error
                    bitmap = null;
                }
                if(mDeterminateProgressDrawer == null)
                    this.mDeterminateProgressDrawer = new DeterminateProgressDrawer(bitmap);
                mProgressDrawer = mDeterminateProgressDrawer;
                break;

            case HORIZONTAL_DETERMINATE:
                if(mDeterminateHorizontalProgressDrawer == null)
                    this.mDeterminateHorizontalProgressDrawer = new DeterminateHorizontalProgressDrawer();
                mProgressDrawer = mDeterminateHorizontalProgressDrawer;
                break;

            case HORIZONTAL_INDETERMINATE:
                if(mIndeterminateHorizontalProgressDrawer == null)
                    this.mIndeterminateHorizontalProgressDrawer = new IndeterminateHorizontalProgressDrawer();
                mProgressDrawer = mIndeterminateHorizontalProgressDrawer;
                break;

            default:
            case NONE:
                if(mDummyProgressDrawer == null)
                    this.mDummyProgressDrawer = new DummyProgressDrawer();
                mProgressDrawer = mDummyProgressDrawer;
                break;
        }
        mProgressDrawer.setListener(listener);
        return mProgressDrawer;
    }

    public Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    /**
     * It calculates the bounds of the progress indicator.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     */
    public final void onSizeChanged(int w, int h) {
        mProgressOptions.calculateBounds(w, h, mProgressMode);
        //set calculated bounds to our progress bounds
        mProgressBounds.set(
                mProgressOptions.getLeft(),
                mProgressOptions.getTop(),
                mProgressOptions.getRight(),
                mProgressOptions.getBottom());

        mProgressCancelBounds.set(
                mProgressOptions.getLeft() - 20,
                mProgressOptions.getTop() - 20,
                mProgressOptions.getRight() + 20,
                mProgressOptions.getBottom() + 20);
        mProgressDrawer.setup(mProgressOptions);
    }


    /**
     * Changes the progress mode of the indicator (e.g. passing from determinate to indeterminate).
     * It also starts animation of indeterminate progress indicator.
     *
     * @param progressMode mode to change the progress indicator into
     */
    public final void changeProgressMode(PivProgressMode progressMode){
        if(mProgressMode != null && mProgressMode == progressMode)
            return;

        if(mProgressDrawer != null)
            mProgressDrawer.stopIndeterminateAnimation();

        mProgressMode = progressMode;
        mProgressDrawer = getDrawer(mProgressMode);
        mProgressDrawer.setup(mProgressOptions);
        mProgressDrawer.startIndeterminateAnimation();
    }


    /** Draws the progress indicator */
    public final void onDraw(Canvas canvas) {
        mProgressDrawer.draw(canvas, mProgressBounds, mProgressCancelBounds);
    }

    /**
     * @return The options of the progress indicator
     */
    public final ProgressOptions getProgressOptions() {
        return mProgressOptions;
    }


    interface ProgressDrawerListener{
        /** Request to invalidate the progress indicator bounds */
        void onRequestInvalidate();
    }


    /**
     * Called when an option is updated. It propagates the update to the progress drawers.
     */
    @Override
    public void onOptionsUpdated(ProgressOptions options) {
        mProgressDrawer.setup(options);
        mProgressOptions = options;
    }

    /**
     * Called when an option that changes the size of the progress indicator is updated.
     * The bounds are calculated again, and it propagates the update to the progress drawers.
     */
    @Override
    public void onSizeUpdated(ProgressOptions options) {
        mProgressOptions = options;
        //set calculated bounds to our progress bounds
        mProgressBounds.set(
                mProgressOptions.getLeft(),
                mProgressOptions.getTop(),
                mProgressOptions.getRight(),
                mProgressOptions.getBottom());

        mProgressDrawer.setup(mProgressOptions);
    }





    /** Saves state into a bundle. */
    public Bundle saveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("progress_options", mProgressOptions);
        bundle.putInt("progress_mode", mProgressMode.getValue());

        return bundle;
    }

    /** Restores state from a bundle. */
    public void restoreInstanceState(Bundle state) {
        if (state == null)
            return;

        mProgressOptions.setOptions((ProgressOptions) state.getParcelable("progress_options"));
        PivProgressMode progressMode = PivProgressMode.fromValue(state.getInt("progress_mode"));
        onSizeUpdated(mProgressOptions);
        changeProgressMode(progressMode);
    }
}
