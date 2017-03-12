package com.stefanosiano.progressimageview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.stefanosiano.progressimageview.progress.DeterminateProgressDrawer;
import com.stefanosiano.progressimageview.progress.DummyProgressDrawer;
import com.stefanosiano.progressimageview.progress.IndeterminateProgressDrawer;
import com.stefanosiano.progressimageview.progress.ProgressDrawer;

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

    private final RectF progressBounds;

    private int mProgressCircleBorderWidth = DEFAULT_PROGRESS_CIRCLE_BORDER_WIDTH;
    private int mProgressCircleSize = DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE;
    private int mProgressState = PROGRESS_STATE_DISABLED;
    private int mProgressColor = 0;
    private int mRemainingProgressColor = 0;
    private int[] mIndeterminateProgressColorArray = {};

    private final DummyProgressDrawer dummyProgressDrawer;
    private final DeterminateProgressDrawer determinateProgressDrawer;
    private final IndeterminateProgressDrawer indeterminateProgressDrawer;
    private ProgressDrawer progressDrawer;

    public ProgressImageView(Context context) {
        this(context, null, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.progressBounds = new RectF();
        this.dummyProgressDrawer = new DummyProgressDrawer();
        this.determinateProgressDrawer = new DeterminateProgressDrawer(this);
        this.indeterminateProgressDrawer = new IndeterminateProgressDrawer(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressImageView, defStyleAttr, 0);

        this.mProgressCircleBorderWidth = a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_circle_border_width, DEFAULT_PROGRESS_CIRCLE_BORDER_WIDTH);
        this.mProgressCircleSize = a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_circle_size, DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE);
        this.mProgressState = a.getInteger(R.styleable.ProgressImageView_piv_progress_state, PROGRESS_STATE_DISABLED);
        this.mProgressColor = a.getColor(R.styleable.ProgressImageView_piv_progress_color,
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? getResources().getColor(R.color.piv_default_progress_color) : getResources().getColor(R.color.piv_default_progress_color, getContext().getTheme()));
        this.mRemainingProgressColor = a.getColor(R.styleable.ProgressImageView_piv_progress_remaining_color,
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M ? getResources().getColor(R.color.piv_default_remaining_progress_color) : getResources().getColor(R.color.piv_default_remaining_progress_color, getContext().getTheme()));
        int angle = (int) (a.getFloat(R.styleable.ProgressImageView_piv_progress_percent, DEFAULT_PROGRESS_PERCENT) * 3.6f);
        this.determinateProgressDrawer.setProgressAngle(angle);

        final int id = a.getResourceId(R.styleable.ProgressImageView_piv_indeterminate_progress_color_array, R.array.piv_default_indeterminate_progress_colors);

        try{
            mIndeterminateProgressColorArray = a.getResources().getIntArray(id);
            if(mIndeterminateProgressColorArray.length < 2){
                mIndeterminateProgressColorArray = new int[]{this.mRemainingProgressColor, this.mProgressColor};
            }

        }
        catch (Resources.NotFoundException e){
            e.printStackTrace();
            Log.e("ASD", "3");
            mIndeterminateProgressColorArray = new int[]{this.mRemainingProgressColor, this.mProgressColor};
        }

        a.recycle();

        changeProgressState(mProgressState);
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

        progressBounds.set(
                getWidth() - mProgressCircleSize - mProgressCircleBorderWidth - getPaddingRight(),
                getHeight() - mProgressCircleSize - mProgressCircleBorderWidth - getPaddingBottom(),
                getWidth() - mProgressCircleBorderWidth - getPaddingRight(),
                getHeight() - mProgressCircleBorderWidth - getPaddingBottom());
    }

    private void changeProgressState(int progressState){
        mProgressState = progressState;
        switch (mProgressState){
            case PROGRESS_STATE_INDETERMINATE:
                progressDrawer = indeterminateProgressDrawer;
                break;
            case PROGRESS_STATE_DETERMINATE:
                progressDrawer = determinateProgressDrawer;
                break;
            default:
            case PROGRESS_STATE_DISABLED:
                progressDrawer = dummyProgressDrawer;
                break;
        }
        progressDrawer.init(mProgressColor, mProgressCircleBorderWidth, mRemainingProgressColor, mIndeterminateProgressColorArray);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        progressDrawer.draw(canvas, progressBounds);
    }


    public final void setProgressPercent(double progressPercent) {
        changeProgressState(PROGRESS_STATE_DETERMINATE);
        int angle = (int) (progressPercent * 3.6f);
        determinateProgressDrawer.setProgressAngle(angle);
    }

    public final void disableProgress(){
        this.mProgressState = PROGRESS_STATE_DISABLED;
    }

    public final void setProgressIndeterminate(){
        this.mProgressState = PROGRESS_STATE_INDETERMINATE;
    }

    public final void setIndeterminateProgressInterpolator(Interpolator interpolator){
        indeterminateProgressDrawer.setProgressAnimationInterpolator(interpolator);
    }
}

