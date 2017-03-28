package com.stefanosiano.powerfulimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.stefanosiano.powerfulimageview.progress.ProgressOptions;

/**
 * Dummy progress drawer that doesn't do anything.
 * Used when progress is disabled, so functions can be called without checks with no problem.
 */

final class DummyProgressDrawer implements ProgressDrawer {

    /**
     * Dummy progress drawer that doesn't do anything.
     * Used when progress is disabled, so functions can be called without checks with no problem.
     */
    DummyProgressDrawer() {}

    @Override
    public void setup(ProgressOptions progressOptions) {

    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds, RectF progressCancelBounds) {

    }

    @Override
    public void startIndeterminateAnimation() {

    }

    @Override
    public void stopIndeterminateAnimation() {

    }

    @Override
    public void setProgressPercent(float progressPercent) {

    }

    @Override
    public void setAnimationEnabled(boolean enabled) {

    }

    @Override
    public void setAnimationDuration(long millis) {

    }

    @Override
    public void setListener(ProgressDrawerManager.ProgressDrawerListener listener) {

    }
}
