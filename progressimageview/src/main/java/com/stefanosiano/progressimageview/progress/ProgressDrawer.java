package com.stefanosiano.progressimageview.progress;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by stefano on 3/12/17.
 */

public interface ProgressDrawer {
    void setup(ProgressOptions progressOptions);
    void draw(Canvas canvas, RectF progressBounds);
    void startIndeterminateAnimation();
    void stopIndeterminateAnimation();
    void setProgressPercent(float progressPercent);
}
