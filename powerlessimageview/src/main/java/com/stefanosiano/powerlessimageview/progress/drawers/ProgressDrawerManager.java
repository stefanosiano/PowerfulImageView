package com.stefanosiano.powerlessimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

import com.stefanosiano.powerlessimageview.progress.PivProgressMode;
import com.stefanosiano.powerlessimageview.progress.ProgressOptions;

import java.lang.ref.WeakReference;

/**
 * Manager class for progress drawers. Used to initialize use the needed drawers.
 */

public final class ProgressDrawerManager implements ProgressOptions.ProgressOptionsListener {


    //Variables used to initialize drawers

    //Using a weakRefence to be sure to not leak memory
    private final WeakReference<View> mView;

    /** Bounds in which the progress indicator will be drawn */
    private final RectF mProgressBounds;

    /** Bounds in which the progress indicator shadow will be drawn */
    private final RectF mProgressShadowBorderBounds;

    /** Bounds in which the progress indicator shadow will be drawn */
    private final RectF mProgressShadowBounds;

    //Drawers
    private DummyProgressDrawer mDummyProgressDrawer;
    private DeterminateProgressDrawer mDeterminateProgressDrawer;
    private DeterminateHorizontalProgressDrawer mDeterminateHorizontalProgressDrawer;
    private IndeterminateHorizontalProgressDrawer mIndeterminateHorizontalProgressDrawer;
    private IndeterminateProgressDrawer mIndeterminateProgressDrawer;

    //Shadow Drawers
    private DummyShadowDrawer mDummyCancelDrawer;
    private RectangularShadowDrawer mRectangularShadowDrawer;
    private CircularShadowDrawer mCircularShadowDrawer;

    /** Interface used to switch between its implementations, based on the progress mode and options selected. */
    private ShadowDrawer mShadowDrawer;

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
     * @param view View to show progress indicator into
     */
    public ProgressDrawerManager(View view, final ProgressOptions progressOptions){
        this.mView = new WeakReference<>(view);
        this.mProgressBounds = new RectF();
        this.mProgressShadowBorderBounds = new RectF();
        this.mProgressShadowBounds = new RectF();
        this.mProgressOptions = progressOptions;
        this.listener = new ProgressDrawerListener() {
            @Override
            public void onRequestInvalidate() {

                if(mView.get() != null) {
                    //invalidates only the area of the progress indicator, instead of the whole view. +1 e -1 are used to be sure to invalidate the whole progress indicator
                    //It is more efficient then just postInvalidate(): if something is drawn outside the bounds, it will not be calculated again!
                    mView.get().postInvalidate((int) mProgressBounds.left - 1, (int) mProgressBounds.top - 1, (int) mProgressBounds.right + 1, (int) mProgressBounds.bottom + 1);
                }
            }
        };
        this.mProgressOptions.setListener(this);
    }


    /**
     * Updates the progress drawers to use and chooses the right one to use based on the mode.
     * If the drawer doesn't exist, it will be instantiated.
     * If the shadow drawer doesn't exist, it will be instantiated.
     *
     * @param progressMode Mode of the progress, used to choose the right drawers.
     */
    private void updateDrawers(PivProgressMode progressMode){

        switch (progressMode){

            case CIRCULAR:

                //progress drawer
                if(mProgressOptions.isIndeterminate()){
                    if(mIndeterminateProgressDrawer == null)
                        this.mIndeterminateProgressDrawer = new IndeterminateProgressDrawer();
                    mProgressDrawer = mIndeterminateProgressDrawer;
                }
                else {
                    if(mDeterminateProgressDrawer == null)
                        this.mDeterminateProgressDrawer = new DeterminateProgressDrawer();
                    mProgressDrawer = mDeterminateProgressDrawer;
                }

                //shadow drawer
                if(mProgressOptions.isShadowEnabled()) {
                    if (mCircularShadowDrawer == null)
                        mCircularShadowDrawer = new CircularShadowDrawer();
                    mShadowDrawer = mCircularShadowDrawer;
                }
                else {
                    if(mDummyCancelDrawer == null)
                        mDummyCancelDrawer = new DummyShadowDrawer();
                    mShadowDrawer = mDummyCancelDrawer;
                }

                break;

            case HORIZONTAL:

                //progress drawer
                if(mProgressOptions.isIndeterminate()){
                    if(mIndeterminateHorizontalProgressDrawer == null)
                        this.mIndeterminateHorizontalProgressDrawer = new IndeterminateHorizontalProgressDrawer();
                    mProgressDrawer = mIndeterminateHorizontalProgressDrawer;
                }
                else {
                    if(mDeterminateHorizontalProgressDrawer == null)
                        this.mDeterminateHorizontalProgressDrawer = new DeterminateHorizontalProgressDrawer();
                    mProgressDrawer = mDeterminateHorizontalProgressDrawer;
                }

                //shadow drawer
                if(mProgressOptions.isShadowEnabled()) {
                    if(mRectangularShadowDrawer == null)
                        mRectangularShadowDrawer = new RectangularShadowDrawer();
                    mShadowDrawer = mRectangularShadowDrawer;
                }
                else {
                    if(mDummyCancelDrawer == null)
                        mDummyCancelDrawer = new DummyShadowDrawer();
                    mShadowDrawer = mDummyCancelDrawer;
                }

                break;

            default:
            case NONE:
                if(mDummyProgressDrawer == null)
                    this.mDummyProgressDrawer = new DummyProgressDrawer();
                mProgressDrawer = mDummyProgressDrawer;

                if(mDummyCancelDrawer == null)
                    mDummyCancelDrawer = new DummyShadowDrawer();
                mShadowDrawer = mDummyCancelDrawer;

                break;
        }
        mProgressDrawer.setListener(listener);
    }


    /**
     * It calculates the bounds of the progress indicator.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     */
    public final void onSizeChanged(int w, int h) {
        mProgressOptions.calculateBounds(w, h, mProgressMode);

        onSizeUpdated(mProgressOptions);
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
        updateDrawers(mProgressMode);
        mProgressDrawer.setup(mProgressOptions);
        mShadowDrawer.setup(mProgressOptions);
        mProgressDrawer.startIndeterminateAnimation();
    }


    /** Draws the progress indicator */
    public final void onDraw(Canvas canvas) {
        mShadowDrawer.draw(canvas, mProgressShadowBorderBounds, mProgressShadowBounds);
        mProgressDrawer.draw(canvas, mProgressBounds);
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
        mShadowDrawer.setup(options);
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

        mProgressShadowBorderBounds.set(
                mProgressOptions.getShadowBorderLeft(),
                mProgressOptions.getShadowBorderTop(),
                mProgressOptions.getShadowBorderRight(),
                mProgressOptions.getShadowBorderBottom());

        mProgressShadowBounds.set(
                mProgressOptions.getShadowLeft(),
                mProgressOptions.getShadowTop(),
                mProgressOptions.getShadowRight(),
                mProgressOptions.getShadowBottom());

        mProgressDrawer.setup(mProgressOptions);
        mShadowDrawer.setup(mProgressOptions);
    }

    /**
     * Called when the progress mode changes.
     * The drawer is updated and the right one is used.
     */
    @Override
    public void onModeUpdated(ProgressOptions options) {
        mProgressOptions = options;
        changeProgressMode(mProgressMode);
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
