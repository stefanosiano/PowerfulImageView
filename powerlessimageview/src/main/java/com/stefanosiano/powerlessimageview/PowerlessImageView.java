package com.stefanosiano.powerlessimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.stefanosiano.powerlessimageview.progress.PivProgressGravity;
import com.stefanosiano.powerlessimageview.progress.PivProgressMode;
import com.stefanosiano.powerlessimageview.progress.ProgressOptions;
import com.stefanosiano.powerlessimageview.progress.drawers.ProgressDrawerManager;

/**
 * Powerful ImageView with several added features (highly customizable):
 *     -Progress indicator: it can be determinate, indeterminate, circular or horizontal.
 *
 * It extends AppCompatImageView, allowing the use of VectorDrawables and all AppCompat stuff.
 * The downside is that it needs the Android appcompat-v7 library.
 */
public class PowerlessImageView extends ImageView {

    //Progress initialization constants
    private static final boolean DEFAULT_PROGRESS_USE_DETERMINATE_ANIMATION = true;
    private static final int DEFAULT_PROGRESS_WIDTH = -1;
    private static final float DEFAULT_PROGRESS_WIDTH_PERCENT = 8;
    private static final int DEFAULT_PROGRESS_SIZE = -1;
    private static final float DEFAULT_PROGRESS_SIZE_PERCENT = 40;
    private static final int DEFAULT_PROGRESS_PADDING = 2;
    private static final int DEFAULT_PROGRESS_PERCENT = 0;
    private static final int DEFAULT_PROGRESS_GRAVITY = PivProgressGravity.CENTER.getValue();
    private static final boolean DEFAULT_PROGRESS_DISABLE_RTL_SUPPORT = false;
    private static final boolean DEFAULT_PROGRESS_DETERMINATE_DRAW_WEDGE = false;
    private static final boolean DEFAULT_PROGRESS_SHADOW_ENABLED = true;
    private static final int DEFAULT_PROGRESS_MODE = PivProgressMode.NONE.getValue();
    private static final int DEFAULT_PROGRESS_SHADOW_PADDING = -1;
    private static final float DEFAULT_PROGRESS_SHADOW_PADDING_PERCENT = 8;

    /** Helper class to manage the progress indicator and its options */
    private final ProgressDrawerManager mProgressDrawerManager;


    public PowerlessImageView(Context context) {
        this(context, null, 0);
    }

    public PowerlessImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PowerlessImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PowerlessImageView, defStyleAttr, 0);

        //get all the options from xml or default constants and initialize ProgressOptions object
        ProgressOptions progressOptions = new ProgressOptions(
                a.getBoolean(R.styleable.PowerlessImageView_piv_progress_determinate_animation_enabled, DEFAULT_PROGRESS_USE_DETERMINATE_ANIMATION),
                a.getDimensionPixelSize(R.styleable.PowerlessImageView_piv_progress_border_width, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROGRESS_WIDTH, getResources().getDisplayMetrics())),
                a.getFloat(R.styleable.PowerlessImageView_piv_progress_border_width_percent, DEFAULT_PROGRESS_WIDTH_PERCENT),
                a.getDimensionPixelSize(R.styleable.PowerlessImageView_piv_progress_size, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROGRESS_SIZE, getResources().getDisplayMetrics())),
                a.getDimensionPixelSize(R.styleable.PowerlessImageView_piv_progress_padding, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROGRESS_PADDING, getResources().getDisplayMetrics())),
                a.getFloat(R.styleable.PowerlessImageView_piv_progress_size_percent, DEFAULT_PROGRESS_SIZE_PERCENT),
                a.getFloat(R.styleable.PowerlessImageView_piv_progress_value_percent, DEFAULT_PROGRESS_PERCENT),
                a.getColor(R.styleable.PowerlessImageView_piv_progress_front_color,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? getResources().getColor(R.color.piv_default_progress_front_color, getContext().getTheme()) : getResources().getColor(R.color.piv_default_progress_front_color)),
                a.getColor(R.styleable.PowerlessImageView_piv_progress_back_color,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? getResources().getColor(R.color.piv_default_progress_back_color, getContext().getTheme()) : getResources().getColor(R.color.piv_default_progress_back_color)),
                a.getColor(R.styleable.PowerlessImageView_piv_progress_indeterminate_color,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? getResources().getColor(R.color.piv_default_progress_indeterminate_color, getContext().getTheme()) : getResources().getColor(R.color.piv_default_progress_indeterminate_color)),
                a.getInteger(R.styleable.PowerlessImageView_piv_progress_gravity, DEFAULT_PROGRESS_GRAVITY),
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getLayoutDirection() == LAYOUT_DIRECTION_RTL,
                a.getBoolean(R.styleable.PowerlessImageView_piv_progress_rtl_disabled, DEFAULT_PROGRESS_DISABLE_RTL_SUPPORT),
                a.getBoolean(R.styleable.PowerlessImageView_piv_progress_draw_wedge, DEFAULT_PROGRESS_DETERMINATE_DRAW_WEDGE),
                a.getBoolean(R.styleable.PowerlessImageView_piv_progress_shadow_enabled, DEFAULT_PROGRESS_SHADOW_ENABLED),
                a.getColor(R.styleable.PowerlessImageView_piv_progress_shadow_color,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? getResources().getColor(R.color.piv_default_progress_shadow_color, getContext().getTheme()) : getResources().getColor(R.color.piv_default_progress_shadow_color)),
                a.getDimensionPixelSize(R.styleable.PowerlessImageView_piv_progress_shadow_padding, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROGRESS_SHADOW_PADDING, getResources().getDisplayMetrics())),
                a.getFloat(R.styleable.PowerlessImageView_piv_progress_shadow_padding_percent, DEFAULT_PROGRESS_SHADOW_PADDING_PERCENT)
        );
        PivProgressMode mode = PivProgressMode.fromValue(a.getInteger(R.styleable.PowerlessImageView_piv_progress_mode, DEFAULT_PROGRESS_MODE));

        a.recycle();

        this.mProgressDrawerManager = new ProgressDrawerManager(this, progressOptions);

        changeProgressMode(mode);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //updates progress bounds
        mProgressDrawerManager.onSizeChanged(w, h);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //draw progress indicator
        mProgressDrawerManager.onDraw(canvas);
    }


    /**
     * Changes the progress mode of the indicator (e.g. passing from determinate to indeterminate).
     * It also starts animation of indeterminate progress indicator.
     *
     * @param progressMode mode to change the progress indicator into
     */
    public final void changeProgressMode(PivProgressMode progressMode){
        mProgressDrawerManager.changeProgressMode(progressMode);
    }



    /**
     * @return The options of the progress indicator
     */
    public final ProgressOptions getProgressOptions() {
        return mProgressDrawerManager.getProgressOptions();
    }










    /** Save the state of the view. */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super_state", super.onSaveInstanceState());
        bundle.putParcelable("progress_drawer_manager", mProgressDrawerManager.saveInstanceState());

        return bundle;
    }

    /** Restore the state of the view. */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {// implicit null check
            Bundle bundle = (Bundle) state;
            mProgressDrawerManager.restoreInstanceState((Bundle) bundle.getParcelable("progress_options"));
            state = bundle.getParcelable("super_state");
        }
        super.onRestoreInstanceState(state);
    }
}

