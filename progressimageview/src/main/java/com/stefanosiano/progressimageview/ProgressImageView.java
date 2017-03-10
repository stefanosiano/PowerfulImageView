package com.stefanosiano.progressimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by stefano on 10/03/17.
 */
public class ProgressImageView extends ImageView {

    private static final int DEFAULT_PROGRESS_CIRCLE_BORDER_WIDTH = 0;
    private static final int DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE = 0;

    private final RectF progressOval;
    private final Paint progressPaint;

    private int mProgressCircleBorderWidth = DEFAULT_PROGRESS_CIRCLE_BORDER_WIDTH;
    private int mProgressCircleSize = DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE;

    public ProgressImageView(Context context) {
        super(context);
        progressOval = new RectF();
        progressPaint = new Paint();
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        progressOval = new RectF();
        progressPaint = new Paint();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressImageView, defStyleAttr, 0);


        mProgressCircleBorderWidth = a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_circle_border_width, DEFAULT_PROGRESS_CIRCLE_BORDER_WIDTH);
        mProgressCircleSize = a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_circle_size, DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE);
        /*
        mBorderColor = a.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR);
        mBorderOverlay = a.getBoolean(R.styleable.CircleImageView_civ_border_overlay, DEFAULT_BORDER_OVERLAY);
        mFillColor = a.getColor(R.styleable.CircleImageView_civ_fill_color, DEFAULT_FILL_COLOR);
*/
        a.recycle();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }

    private void setup(){
        progressPaint.setColor(Color.RED);
        progressPaint.setStrokeWidth(mProgressCircleBorderWidth);
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);

        progressOval.set(
                getWidth() - mProgressCircleSize - progressPaint.getStrokeWidth() - getPaddingRight(),
                getHeight() - mProgressCircleSize - progressPaint.getStrokeWidth() - getPaddingBottom(),
                getWidth() - progressPaint.getStrokeWidth() - getPaddingRight(),
                getHeight() - progressPaint.getStrokeWidth() - getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float progress = 180;

        canvas.drawArc(progressOval, -90, progress, false, progressPaint);

    }

}

