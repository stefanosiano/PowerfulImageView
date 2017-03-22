package com.stefanosiano.progressimageview.progress.drawers;

import android.graphics.RectF;

import com.stefanosiano.progressimageview.ProgressImageView;
import com.stefanosiano.progressimageview.progress.PivProgressMode;

/**
 * Manager class for progress drawers. Used to initialize and get the instances of the needed drawers.
 */

public final class ProgressDrawerManager {
    //Variables used to initialize drawers
    private final ProgressImageView mPiv;
    private final RectF mProgressBounds;

    //Drawers
    private DummyProgressDrawer mDummyProgressDrawer;
    private DeterminateProgressDrawer mDeterminateProgressDrawer;
    private DeterminateHorizontalProgressDrawer mDeterminateHorizontalProgressDrawer;
    private IndeterminateHorizontalProgressDrawer mIndeterminateHorizontalProgressDrawer;
    private IndeterminateProgressDrawer mIndeterminateProgressDrawer;

    /**
     * Manager class for progress drawers. Used to initialize and get the instances of the needed drawers.
     *
     * @param piv View to show progress indicator into
     * @param progressBounds Bounds of the progress indicator
     */
    public ProgressDrawerManager(ProgressImageView piv, RectF progressBounds){
        this.mPiv = piv;
        this.mProgressBounds = progressBounds;
    }


    /**
     * Gets the instance of the progress drawer to use.
     * If the drawer doesn't exist, it will be instantiated and returned.
     *
     * @param progressMode Mode of the progress, used to choose the right drawer.
     * @return A ProgressDrawer chosen based on the mode. It will never return null.
     */
    public ProgressDrawer getDrawer(PivProgressMode progressMode){
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
}
