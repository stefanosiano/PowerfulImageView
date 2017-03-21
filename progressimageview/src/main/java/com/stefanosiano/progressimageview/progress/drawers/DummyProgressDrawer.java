package com.stefanosiano.progressimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.stefanosiano.progressimageview.progress.ProgressOptions;

/**
 * Created by stefano on 3/12/17.
 */

final class DummyProgressDrawer implements ProgressDrawer {

    DummyProgressDrawer() {}

    @Override
    public void setup(ProgressOptions progressOptions) {

    }

    @Override
    public void draw(Canvas canvas, RectF progressBounds) {

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
}
