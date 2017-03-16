package com.stefanosiano.progressimageview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.stefanosiano.progressimageview.progress.DeterminateProgressDrawer;
import com.stefanosiano.progressimageview.progress.DummyProgressDrawer;
import com.stefanosiano.progressimageview.progress.IndeterminateProgressDrawer;
import com.stefanosiano.progressimageview.progress.ProgressDrawer;
import com.stefanosiano.progressimageview.progress.PivProgressMode;

/**
 * Created by stefano on 10/03/17.
 */
public class ProgressImageView extends AppCompatImageView {

    private static final int DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE = 16;
    private static final int DEFAULT_PROGRESS_PERCENT = 0;

    private final RectF mProgressBounds;
    private final DummyProgressDrawer mDummyProgressDrawer;
    private final DeterminateProgressDrawer mDeterminateProgressDrawer;
    private final IndeterminateProgressDrawer mIndeterminateProgressDrawer;

    private ProgressDrawer mProgressDrawer;

    private int mProgressCircleBorderWidth = 0;
    private int mProgressCircleSize = DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE;
    private PivProgressMode mProgressMode = null;
    private int mProgressFrontColor = 0;
    private int mProgressBackColor = 0;
    private int mIndeterminateProgressColor = 0;
    private boolean mUseDeterminateProgressAnimation;


    public ProgressImageView(Context context) {
        this(context, null, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyle, int defStyleRes){
        this(context, attrs, defStyle);
    }
    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mProgressBounds = new RectF();
        this.mDummyProgressDrawer = new DummyProgressDrawer();
        this.mDeterminateProgressDrawer = new DeterminateProgressDrawer(this, mProgressBounds);
        this.mIndeterminateProgressDrawer = new IndeterminateProgressDrawer(this, mProgressBounds);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressImageView, defStyleAttr, 0);

        this.mUseDeterminateProgressAnimation = a.getBoolean(R.styleable.ProgressImageView_piv_use_determinate_progress_animation, true);
        this.mProgressCircleSize = a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_circle_size, DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE);
        this.mProgressCircleBorderWidth = a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_circle_border_width, Math.round(mProgressCircleSize / 8f));
        this.mProgressFrontColor = a.getColor(R.styleable.ProgressImageView_piv_progress_front_color, ContextCompat.getColor(context, R.color.piv_default_progress_front_color));
        this.mProgressBackColor = a.getColor(R.styleable.ProgressImageView_piv_progress_back_color, ContextCompat.getColor(context, R.color.piv_default_progress_back_color));
        this.mIndeterminateProgressColor = a.getColor(R.styleable.ProgressImageView_piv_indeterminate_progress_color, ContextCompat.getColor(context, R.color.piv_default_indeterminate_progress_color));
        this.mProgressMode = null;

        PivProgressMode progressMode = PivProgressMode.fromValue(a.getInteger(R.styleable.ProgressImageView_piv_progress_mode, PivProgressMode.PROGRESS_MODE_DETERMINATE.getValue()));
        int angle = (int) (a.getFloat(R.styleable.ProgressImageView_piv_progress_percent, DEFAULT_PROGRESS_PERCENT) * 3.6f);
        this.mDeterminateProgressDrawer.setProgressAngle(angle);
        mDeterminateProgressDrawer.setUseAnimation(mUseDeterminateProgressAnimation);
        if(this.mProgressCircleBorderWidth < 1) this.mProgressCircleBorderWidth = 1;

        a.recycle();

        changeProgressMode(progressMode);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mProgressBounds.set(
                w - mProgressCircleSize - mProgressCircleBorderWidth - getPaddingRight(),
                h - mProgressCircleSize - mProgressCircleBorderWidth - getPaddingBottom(),
                w - mProgressCircleBorderWidth - getPaddingRight(),
                h - mProgressCircleBorderWidth - getPaddingBottom());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mProgressDrawer.draw(canvas, mProgressBounds);
    }



    public void changeProgressMode(PivProgressMode progressMode){
        if(mProgressMode != null && mProgressMode == progressMode)
            return;

        if(mProgressDrawer != null)
            mProgressDrawer.clear();

        mProgressMode = progressMode;
        switch (mProgressMode){
            case PROGRESS_MODE_INDETERMINATE:
                mProgressDrawer = mIndeterminateProgressDrawer;
                break;
            case PROGRESS_MODE_DETERMINATE:
                mProgressDrawer = mDeterminateProgressDrawer;
                break;
            default:
            case PROGRESS_MODE_DISABLED:
                mProgressDrawer = mDummyProgressDrawer;
                break;
        }
        mProgressDrawer.init(mProgressFrontColor, mProgressCircleBorderWidth, mProgressBackColor, mIndeterminateProgressColor);
    }


    public final void showProgressPercent(double progressPercent) {
        changeProgressMode(PivProgressMode.PROGRESS_MODE_DETERMINATE);
        int angle = (int) (progressPercent * 3.6f);
        mDeterminateProgressDrawer.setProgressAngle(angle);
    }

    public void setUseDeterminateProgressAnimation(boolean useDeterminateProgressAnimation) {
        this.mUseDeterminateProgressAnimation = useDeterminateProgressAnimation;
        mDeterminateProgressDrawer.setUseAnimation(useDeterminateProgressAnimation);
    }

}

