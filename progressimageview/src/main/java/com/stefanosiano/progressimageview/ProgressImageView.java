package com.stefanosiano.progressimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.stefanosiano.progressimageview.progress.PivProgressGravity;
import com.stefanosiano.progressimageview.progress.drawers.ProgressDrawer;
import com.stefanosiano.progressimageview.progress.PivProgressMode;
import com.stefanosiano.progressimageview.progress.ProgressOptions;
import com.stefanosiano.progressimageview.progress.drawers.ProgressDrawerHelper;

/**
 * Created by stefano on 10/03/17.
 */
public class ProgressImageView extends AppCompatImageView {

    //initialization constants
    private static final boolean DEFAULT_PROGRESS_USE_DETERMINATE_ANIMATION = true;
    private static final int DEFAULT_PROGRESS_WIDTH = -1;
    private static final int DEFAULT_PROGRESS_SIZE = 16;
    private static final int DEFAULT_PROGRESS_PADDING = 16;
    private static final int DEFAULT_PROGRESS_PERCENT = 0;
    private static final int DEFAULT_PROGRESS_GRAVITY = PivProgressGravity.CENTER.getValue();
    private static final float DEFAULT_PROGRESS_SIZE_PERCENT = -1;
    private static final boolean DEFAULT_PROGRESS_DISABLE_RTL_SUPPORT = false;
    private static final boolean DEFAULT_PROGRESS_DETERMINATE_DRAW_WEDGE = false;
    private static final int DEFAULT_PROGRESS_MODE = PivProgressMode.DETERMINATE.getValue();

    //progress variables
    private final RectF mProgressBounds;
    private final ProgressDrawerHelper mProgressDrawerHelper;
    private final ProgressOptions mProgressOptions;
    private ProgressDrawer mProgressDrawer;
    private PivProgressMode mProgressMode = null;


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
                a.getBoolean(R.styleable.ProgressImageView_piv_use_determinate_progress_animation, DEFAULT_PROGRESS_USE_DETERMINATE_ANIMATION),
                a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_width, DEFAULT_PROGRESS_WIDTH),
                a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_size, DEFAULT_PROGRESS_SIZE),
                a.getDimensionPixelSize(R.styleable.ProgressImageView_piv_progress_padding, DEFAULT_PROGRESS_PADDING),
                a.getFloat(R.styleable.ProgressImageView_piv_progress_size_percent, DEFAULT_PROGRESS_SIZE_PERCENT),
                a.getFloat(R.styleable.ProgressImageView_piv_progress_percent, DEFAULT_PROGRESS_PERCENT),
                a.getColor(R.styleable.ProgressImageView_piv_progress_front_color, ContextCompat.getColor(context, R.color.piv_default_progress_front_color)),
                a.getColor(R.styleable.ProgressImageView_piv_progress_back_color, ContextCompat.getColor(context, R.color.piv_default_progress_back_color)),
                a.getColor(R.styleable.ProgressImageView_piv_progress_indeterminate_color, ContextCompat.getColor(context, R.color.piv_default_indeterminate_progress_color)),
                a.getInteger(R.styleable.ProgressImageView_piv_progress_gravity, DEFAULT_PROGRESS_GRAVITY),
                ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL,
                a.getBoolean(R.styleable.ProgressImageView_piv_progress_disable_rtl_support, DEFAULT_PROGRESS_DISABLE_RTL_SUPPORT),
                a.getBoolean(R.styleable.ProgressImageView_piv_progress_determinate_draw_wedge, DEFAULT_PROGRESS_DETERMINATE_DRAW_WEDGE)
        );
        PivProgressMode mode = PivProgressMode.fromValue(a.getInteger(R.styleable.ProgressImageView_piv_progress_mode, DEFAULT_PROGRESS_MODE));

        a.recycle();

        this.mProgressBounds = new RectF();
        this.mProgressDrawerHelper = new ProgressDrawerHelper(this, this.mProgressBounds);

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
        mProgressDrawer = mProgressDrawerHelper.getDrawer(mProgressMode);
        mProgressDrawer.setup(mProgressOptions);
        mProgressDrawer.startIndeterminateAnimation();
    }


    public final void setProgressPercent(float progressPercent) {
        mProgressDrawer.setProgressPercent(progressPercent);
    }
}

