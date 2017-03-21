package com.stefanosiano.progressimageview.progress.drawers;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.stefanosiano.progressimageview.progress.ProgressOptions;

/**
 * Created by stefano on 3/12/17.
 */

public interface ProgressDrawer {
    void setup(ProgressOptions progressOptions);
    void draw(Canvas canvas, RectF progressBounds);
    void startIndeterminateAnimation();
    void stopIndeterminateAnimation();
    void setProgressPercent(float progressPercent);
    void setAnimationEnabled(boolean enabled);
    void setAnimationDuration(long millis);
}
