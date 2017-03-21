package com.stefanosiano.progressimageview.progress.drawers;

import android.graphics.RectF;

import com.stefanosiano.progressimageview.ProgressImageView;
import com.stefanosiano.progressimageview.progress.PivProgressMode;

/**
 * Created by stefano on 21/03/17.
 */

public final class ProgressDrawerHelper {
    private final ProgressImageView mPiv;
    private final RectF mProgressBounds;

    private DummyProgressDrawer mDummyProgressDrawer;
    private DeterminateProgressDrawer mDeterminateProgressDrawer;
    private DeterminateHorizontalProgressDrawer mDeterminateHorizontalProgressDrawer;
    private IndeterminateHorizontalProgressDrawer mIndeterminateHorizontalProgressDrawer;
    private IndeterminateProgressDrawer mIndeterminateProgressDrawer;

    public ProgressDrawerHelper(ProgressImageView piv, RectF progressBounds){
        this.mPiv = piv;
        this.mProgressBounds = progressBounds;
    }

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
