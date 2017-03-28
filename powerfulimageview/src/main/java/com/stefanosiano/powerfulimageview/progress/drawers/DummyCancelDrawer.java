package com.stefanosiano.powerfulimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.stefanosiano.powerfulimageview.progress.ProgressOptions;

/**
 * Dummy progress drawer that doesn't do anything.
 * Used when cancel progress is disabled, so functions can be called without checks with no problem.
 */

final class DummyCancelDrawer implements CancelDrawer {

    /**
     * Dummy progress drawer that doesn't do anything.
     * Used when cancel progress is disabled, so functions can be called without checks with no problem.
     */
    DummyCancelDrawer() {
    }

    @Override
    public void setup(ProgressOptions progressOptions) {

    }

    @Override
    public void draw(Canvas canvas, RectF cancelBounds, RectF cancelIconBounds) {

    }
}
