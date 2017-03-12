package com.stefanosiano.progressimageview.progress;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by stefano on 3/12/17.
 */

public interface ProgressDrawer {
    void init(int progressColor, int progressCircleBorderWidth, int remainingProgressColor, int[] indeterminateProgressColorArray);
    void draw(Canvas canvas, RectF progressBounds);
}
