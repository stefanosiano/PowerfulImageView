package com.stefanosiano.progressimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.stefanosiano.progressimageview.progress.DeterminateHorizontalProgressDrawer;
import com.stefanosiano.progressimageview.progress.DeterminateProgressDrawer;
import com.stefanosiano.progressimageview.progress.DummyProgressDrawer;
import com.stefanosiano.progressimageview.progress.IndeterminateHorizontalProgressDrawer;
import com.stefanosiano.progressimageview.progress.IndeterminateProgressDrawer;
import com.stefanosiano.progressimageview.progress.PivProgressGravity;
import com.stefanosiano.progressimageview.progress.ProgressDrawer;
import com.stefanosiano.progressimageview.progress.PivProgressMode;
import com.stefanosiano.progressimageview.progress.ProgressOptions;

/**
 * Created by stefano on 10/03/17.
 */
public class ProgressImageView extends AppCompatImageView {

    private static final int DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE = 16;
    private static final int DEFAULT_PROGRESS_PERCENT = 0;
    private static final float DEFAULT_PROGRESS_CIRCLE_SIZE_PERCENT = -1;

    private final RectF mProgressBounds;
    private final DummyProgressDrawer mDummyProgressDrawer;
    private final DeterminateProgressDrawer mDeterminateProgressDrawer;
    private final DeterminateHorizontalProgressDrawer mDeterminateHorizontalProgressDrawer;
    private final IndeterminateHorizontalProgressDrawer mIndeterminateHorizontalProgressDrawer;
    private final IndeterminateProgressDrawer mIndeterminateProgressDrawer;

    private ProgressDrawer mProgressDrawer;

    private PivProgressMode mProgressMode = null;
    private ProgressOptions mProgressOptions;


    public ProgressImageView(Context context) {
        this(context, null, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressImageView, defStyleAttr, 0);

        mProgressOptions = new ProgressOptions(
                a.getBoolean(R.styleable.ProgressImageView_piv_use_determinate_progress_animation, true),
                a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_width, -1),
                a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_size, DEFAULT_PROGRESS_CIRCLE_BORDER_SIZE),
                a.getFloat(R.styleable.ProgressImageView_piv_progress_size_percent, DEFAULT_PROGRESS_CIRCLE_SIZE_PERCENT),
                a.getFloat(R.styleable.ProgressImageView_piv_progress_percent, DEFAULT_PROGRESS_PERCENT),
                a.getColor(R.styleable.ProgressImageView_piv_progress_front_color, ContextCompat.getColor(context, R.color.piv_default_progress_front_color)),
                a.getColor(R.styleable.ProgressImageView_piv_progress_back_color, ContextCompat.getColor(context, R.color.piv_default_progress_back_color)),
                a.getColor(R.styleable.ProgressImageView_piv_progress_indeterminate_color, ContextCompat.getColor(context, R.color.piv_default_indeterminate_progress_color)),
                a.getInteger(R.styleable.ProgressImageView_piv_progress_gravity, PivProgressGravity.CENTER.getValue()),
                ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL,
                a.getBoolean(R.styleable.ProgressImageView_piv_progress_disable_rtl_support, false)
        );
        PivProgressMode mode = PivProgressMode.fromValue(a.getInteger(R.styleable.ProgressImageView_piv_progress_mode, PivProgressMode.DETERMINATE.getValue()));

        this.mProgressBounds = new RectF();
        this.mDummyProgressDrawer = new DummyProgressDrawer();
        this.mDeterminateProgressDrawer = new DeterminateProgressDrawer(this, mProgressBounds);
        this.mDeterminateHorizontalProgressDrawer = new DeterminateHorizontalProgressDrawer(this, mProgressBounds);
        this.mIndeterminateHorizontalProgressDrawer = new IndeterminateHorizontalProgressDrawer(this, mProgressBounds);
        this.mIndeterminateProgressDrawer = new IndeterminateProgressDrawer(this, mProgressBounds);

        a.recycle();

        changeProgressMode(mode);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mProgressOptions.calculateBounds(w, h, mProgressMode);
        mProgressBounds.set(
                mProgressOptions.getLeft(),
                mProgressOptions.getTop(),
                mProgressOptions.getRight(),
                mProgressOptions.getBottom());

        mProgressDrawer.setup(mProgressOptions);
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
            mProgressDrawer.stopIndeterminateAnimation();

        mProgressMode = progressMode;
        switch (mProgressMode){
            case INDETERMINATE:
                mProgressDrawer = mIndeterminateProgressDrawer;
                break;
            case DETERMINATE:
                mProgressDrawer = mDeterminateProgressDrawer;
                break;
            case HORIZONTAL_DETERMINATE:
                mProgressDrawer = mDeterminateHorizontalProgressDrawer;
                break;
            case HORIZONTAL_INDETERMINATE:
                mProgressDrawer = mIndeterminateHorizontalProgressDrawer;
                break;
            default:
            case NONE:
                mProgressDrawer = mDummyProgressDrawer;
                break;
        }
        mProgressDrawer.setup(mProgressOptions);
        mProgressDrawer.startIndeterminateAnimation();
    }


    public final void setProgressPercent(float progressPercent) {
        mProgressDrawer.setProgressPercent(progressPercent);
    }
}

