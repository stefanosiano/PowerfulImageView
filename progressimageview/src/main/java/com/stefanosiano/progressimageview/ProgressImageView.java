package com.stefanosiano.progressimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Created by stefano on 10/03/17.
 */
public class ProgressImageView extends ImageView {

    private static final int PROGRESS_STATE_DISABLED = 0;
    private static final int PROGRESS_STATE_INDETERMINATE = 1;
    private static final int PROGRESS_STATE_DETERMINATE = 2;

    private static final int DEFAULT_PROGRESS_CIRCLE_BORDER_WIDTH = 2;
    private static final int DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE = 16;
    private static final int DEFAULT_PROGRESS_PERCENT = 0;
    private static final int DEFAULT_PROGRESS_COLOR = Color.parseColor("#00A000");
    private static final int DEFAULT_REMAINING_PROGRESS_COLOR = Color.LTGRAY;

    private final RectF progressBounds;
    private final Paint progressPaint;
    private final Paint progressRemainingPaint;

    private int mProgressCircleBorderWidth = DEFAULT_PROGRESS_CIRCLE_BORDER_WIDTH;
    private int mProgressCircleSize = DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE;
    private int mProgressState = PROGRESS_STATE_DISABLED;
    private int mProgressAngle = 0;
    private int mProgressColor = 0;
    private int mProgressProgressColor = 0;

    public ProgressImageView(Context context) {
        this(context, null, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        progressBounds = new RectF();
        progressPaint = new Paint();
        progressRemainingPaint = new Paint();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressImageView, defStyleAttr, 0);

        mProgressCircleBorderWidth = a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_circle_border_width, DEFAULT_PROGRESS_CIRCLE_BORDER_WIDTH);
        mProgressCircleSize = a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_circle_size, DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE);
        mProgressState = a.getInteger(R.styleable.ProgressImageView_piv_progress_state, PROGRESS_STATE_DISABLED);
        mProgressColor = a.getColor(R.styleable.ProgressImageView_piv_progress_color, DEFAULT_PROGRESS_COLOR);
        mProgressProgressColor = a.getColor(R.styleable.ProgressImageView_piv_progress_remaining_color, DEFAULT_REMAINING_PROGRESS_COLOR);
        mProgressAngle = (int) Math.floor(a.getFloat(R.styleable.ProgressImageView_piv_progress_percent, DEFAULT_PROGRESS_PERCENT) * 3.6f);
        mProgressAngle = mProgressAngle % 360;

        a.recycle();

        ProgressCircleAnimation animation = new ProgressCircleAnimation(this);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.INFINITE);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                progressRemainingPaint.setColor(progressPaint.getColor());
                progressPaint.setColor(progressPaint.getColor()+100);
            }
        });

        if(mProgressState == PROGRESS_STATE_INDETERMINATE) {
            startAnimation(animation);
        }
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
        progressPaint.setColor(mProgressColor);
        progressPaint.setStrokeWidth(mProgressCircleBorderWidth);
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressRemainingPaint.setColor(mProgressProgressColor);
        progressRemainingPaint.setStrokeWidth(mProgressCircleBorderWidth);
        progressRemainingPaint.setAntiAlias(true);
        progressRemainingPaint.setStyle(Paint.Style.STROKE);

        progressBounds.set(
                getWidth() - mProgressCircleSize - progressPaint.getStrokeWidth() - getPaddingRight(),
                getHeight() - mProgressCircleSize - progressPaint.getStrokeWidth() - getPaddingBottom(),
                getWidth() - progressPaint.getStrokeWidth() - getPaddingRight(),
                getHeight() - progressPaint.getStrokeWidth() - getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (mProgressState){
            case PROGRESS_STATE_INDETERMINATE:
                canvas.drawArc(progressBounds, -90 + mProgressAngle, 360 - mProgressAngle, false, progressRemainingPaint);
                canvas.drawArc(progressBounds, -90, mProgressAngle, false, progressPaint);
                break;
            case PROGRESS_STATE_DETERMINATE:
                canvas.drawArc(progressBounds, -90 + mProgressAngle, 360 - mProgressAngle, false, progressRemainingPaint);
                canvas.drawArc(progressBounds, -90, mProgressAngle, false, progressPaint);
                break;
            case PROGRESS_STATE_DISABLED:
            default:
                break;
        }

    }


    public void setProgressPercent(double progressPercent) {
        this.mProgressState = PROGRESS_STATE_DETERMINATE;
        this.mProgressAngle = (int) Math.floor(progressPercent * 3.6d);
        this.mProgressAngle = mProgressAngle % 360;
        postInvalidate();
    }

    public void setProgressAngleForAnimation(int progressAngle) {
        this.mProgressAngle = progressAngle;
        this.mProgressAngle = mProgressAngle % 360;
        postInvalidate();
    }

    public void disableProgress(){
        this.mProgressState = PROGRESS_STATE_DISABLED;
    }

    public void setProgressIndeterminate(){
        this.mProgressState = PROGRESS_STATE_INDETERMINATE;
    }
}

