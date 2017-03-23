package com.stefanosiano.progressimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.stefanosiano.progressimageview.ProgressImageView;
import com.stefanosiano.progressimageview.progress.PivProgressMode;
import com.stefanosiano.progressimageview.progress.ProgressOptions;

/**
 * Manager class for progress drawers. Used to initialize and get the instances of the needed drawers.
 */

public final class ProgressDrawerManager {
    //Variables used to initialize drawers
    private final ProgressImageView mPiv;

    /** Bounds in which the progress indicator will be drawn */
    private final RectF mProgressBounds;

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



    /**
     * Manager class for progress drawers. Used to initialize and get the instances of the needed drawers.
     *
     * @param piv View to show progress indicator into
     */
    public ProgressDrawerManager(ProgressImageView piv, final ProgressOptions progressOptions){
        this.mPiv = piv;
        this.mProgressBounds = new RectF();
        this.mProgressOptions = progressOptions;
        this.mProgressOptions.setListener(new ProgressOptions.ProgressOptionsListener() {
            @Override
            public void onOptionsUpdated(ProgressOptions options) {
                mProgressDrawer.setup(options);
                mProgressOptions = options;
            }

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
        });
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
                    this.mIndeterminateProgressDrawer = new IndeterminateProgressDrawer(mPiv, mProgressBounds);
                return mIndeterminateProgressDrawer;
            case DETERMINATE:
                if(mDeterminateProgressDrawer == null)
                    this.mDeterminateProgressDrawer = new DeterminateProgressDrawer(mPiv, mProgressBounds);
                return mDeterminateProgressDrawer;
            case HORIZONTAL_DETERMINATE:
                if(mDeterminateHorizontalProgressDrawer == null)
                    this.mDeterminateHorizontalProgressDrawer = new DeterminateHorizontalProgressDrawer(mPiv, mProgressBounds);
                return mDeterminateHorizontalProgressDrawer;
            case HORIZONTAL_INDETERMINATE:
                if(mIndeterminateHorizontalProgressDrawer == null)
                    this.mIndeterminateHorizontalProgressDrawer = new IndeterminateHorizontalProgressDrawer(mPiv, mProgressBounds);
                return mIndeterminateHorizontalProgressDrawer;
            default:
            case NONE:
                if(mDummyProgressDrawer == null)
                    this.mDummyProgressDrawer = new DummyProgressDrawer();
                return mDummyProgressDrawer;
        }
    }

    /**
     * It calculates the bounds of the progress indicator.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    public final void onSizeChanged(int w, int h, int oldw, int oldh) {
        mProgressOptions.calculateBounds(w, h, mProgressMode);
        //set calculated bounds to our progress bounds
        mProgressBounds.set(
                mProgressOptions.getLeft(),
                mProgressOptions.getTop(),
                mProgressOptions.getRight(),
                mProgressOptions.getBottom());

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
        mProgressDrawer.draw(canvas, mProgressBounds);
    }

    /**
     * @return The options of the progress indicator
     */
    public final ProgressOptions getProgressOptions() {
        return mProgressOptions;
    }

    /**
     * Called when an option is updated. It propagates the update to the progress drawers.
     */
    public final void onOptionsUpdate(){
        mProgressDrawer.setup(mProgressOptions);
    }


}
