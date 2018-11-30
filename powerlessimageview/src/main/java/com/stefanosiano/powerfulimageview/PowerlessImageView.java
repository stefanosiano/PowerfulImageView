package com.stefanosiano.powerfullibraries.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.stefanosiano.powerfullibraries.imageview.blur.BlurOptions;
import com.stefanosiano.powerfullibraries.imageview.blur.PivBlurMode;
import com.stefanosiano.powerfullibraries.imageview.blur.algorithms.BlurManager;
import com.stefanosiano.powerfullibraries.imageview.progress.PivProgressGravity;
import com.stefanosiano.powerfullibraries.imageview.progress.PivProgressMode;
import com.stefanosiano.powerfullibraries.imageview.progress.ProgressOptions;
import com.stefanosiano.powerfullibraries.imageview.progress.drawers.ProgressDrawerManager;
import com.stefanosiano.powerfullibraries.imageview.shape.PivShapeMode;
import com.stefanosiano.powerfullibraries.imageview.shape.PivShapeScaleType;
import com.stefanosiano.powerfullibraries.imageview.shape.ShapeOptions;
import com.stefanosiano.powerfullibraries.imageview.shape.drawers.ShapeDrawerManager;

/**
 * Powerful ImageView with several added features (highly customizable):
 *     -Progress indicator: it can be circular, horizontal or disabled.
 *     -Shapes: it can be normal, circle, solid_circle, oval, solid_oval, rounded_rectangle, solid_rounded_rectangle, rectangle, square.
 *     -Blur: it can use several algorithms, like gaussian, box and stack blur
 *
 * It extends ImageView, avoiding the bloat coming with the use of AppCompat.
 * The downside is that it does not support AppCompat features, like vector drawables.
 */
public class PowerlessImageView extends ImageViewWrapper {

    //Progress initialization constants
    private static final boolean DEFAULT_PROGRESS_USE_DETERMINATE_ANIMATION = true;
    private static final int DEFAULT_PROGRESS_WIDTH = -1;
    private static final float DEFAULT_PROGRESS_WIDTH_PERCENT = 10;
    private static final int DEFAULT_PROGRESS_SIZE = -1;
    private static final float DEFAULT_PROGRESS_SIZE_PERCENT = 40;
    private static final int DEFAULT_PROGRESS_PADDING = 2;
    private static final int DEFAULT_PROGRESS_VALUE = 0;
    private static final int DEFAULT_PROGRESS_GRAVITY = PivProgressGravity.CENTER.getValue();
    private static final boolean DEFAULT_PROGRESS_DISABLE_RTL_SUPPORT = false;
    private static final boolean DEFAULT_PROGRESS_INDETERMINATE = true;
    private static final boolean DEFAULT_PROGRESS_DETERMINATE_DRAW_WEDGE = false;
    private static final boolean DEFAULT_PROGRESS_SHADOW_ENABLED = true;
    private static final boolean DEFAULT_PROGRESS_REVERSED = false;
    private static final boolean DEFAULT_PROGRESS_REMOVED_ON_CHANGE = true;
    private static final int DEFAULT_PROGRESS_MODE = PivProgressMode.NONE.getValue();
    private static final int DEFAULT_PROGRESS_SHADOW_PADDING = -1;
    private static final float DEFAULT_PROGRESS_SHADOW_PADDING_PERCENT = 10;
    private static final int DEFAULT_PROGRESS_SHADOW_BORDER_WIDTH = 1;

    //Shape initialization constants
    private static final int DEFAULT_SHAPE_MODE = PivShapeMode.NORMAL.getValue();
    private static final int DEFAULT_SHAPE_INNER_PADDING = -1;
    private static final float DEFAULT_SHAPE_INNER_PADDING_PERCENT = 0;
    private static final boolean DEFAULT_SHAPE_BORDER_OVERLAY = false;
    private static final int DEFAULT_SHAPE_BORDER_WIDTH = 0;
    private static final float DEFAULT_SHAPE_RATIO = 0;
    private static final float DEFAULT_SHAPE_RADIUS_X = 1;
    private static final float DEFAULT_SHAPE_RADIUS_Y = 1;

    //Blur initialization constants
    private static final int DEFAULT_BLUR_RADIUS = 0;
    private static final float DEFAULT_BLUR_DOWNSAMPLING_RATE = 4;
    private static final boolean DEFAULT_BLUR_USE_RENDERSCRIPT_FALLBACK = true;
    private static final int DEFAULT_BLUR_NUM_THREADS = 0;
    private static final boolean DEFAULT_BLUR_STATIC = false;
    private static final int DEFAULT_BLUR_MODE = PivBlurMode.DISABLED.getValue();

    /** Helper class to manage the progress indicator and its options */
    private final ProgressDrawerManager mProgressDrawerManager;

    /** Helper class to manage the shape of the image and its options */
    private final ShapeDrawerManager mShapeDrawerManager;

    /** Helper class to manage the blurring of the image and its options */
    private final BlurManager mBlurManager;

    /** Flag used to control if blurring bitmap should be checked */
    private boolean mCheckBlur = false;

    /** Flag used to control if I should try to remove the progress on drawable changed (used in constructor and on size change) */
    private boolean mShouldCheckRemoveProgress = true;


    public PowerlessImageView(Context context) {
        this(context, null);
    }

    public PowerlessImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PowerlessImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PowerfulImageView, defStyleAttr, 0);

        //using typed values to remove useless redundancy of attributes (dimension and percentage)
        TypedValue tvSize = new TypedValue();
        TypedValue tvBorderWidth = new TypedValue();
        TypedValue tvShadowPadding = new TypedValue();
        a.getValue(R.styleable.PowerfulImageView_piv_progress_size, tvSize);
        a.getValue(R.styleable.PowerfulImageView_piv_progress_border_width, tvBorderWidth);
        a.getValue(R.styleable.PowerfulImageView_piv_progress_shadow_padding, tvShadowPadding);

        //get all the options from xml or default constants and initialize ProgressOptions object
        ProgressOptions progressOptions = new ProgressOptions(
                a.getBoolean(R.styleable.PowerfulImageView_piv_progress_determinate_animation_enabled, DEFAULT_PROGRESS_USE_DETERMINATE_ANIMATION),
                tvBorderWidth.type == TypedValue.TYPE_DIMENSION ? (int) tvBorderWidth.getDimension(getResources().getDisplayMetrics()) : DEFAULT_PROGRESS_WIDTH,
                tvBorderWidth.type == TypedValue.TYPE_FRACTION ? tvBorderWidth.getFraction(100, 100) : DEFAULT_PROGRESS_WIDTH_PERCENT,
                tvSize.type == TypedValue.TYPE_DIMENSION ? (int) tvSize.getDimension(getResources().getDisplayMetrics()) : DEFAULT_PROGRESS_SIZE,
                tvSize.type == TypedValue.TYPE_FRACTION ? tvSize.getFraction(100, 100) : DEFAULT_PROGRESS_SIZE_PERCENT,
                a.getDimensionPixelSize(R.styleable.PowerfulImageView_piv_progress_padding, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROGRESS_PADDING, getResources().getDisplayMetrics())),
                a.getFloat(R.styleable.PowerfulImageView_piv_progress_value, DEFAULT_PROGRESS_VALUE),
                getColor(a, R.styleable.PowerfulImageView_piv_progress_front_color, R.color.piv_default_progress_front_color),
                getColor(a, R.styleable.PowerfulImageView_piv_progress_back_color, R.color.piv_default_progress_back_color),
                getColor(a, R.styleable.PowerfulImageView_piv_progress_indeterminate_color, R.color.piv_default_progress_indeterminate_color),
                a.getInteger(R.styleable.PowerfulImageView_piv_progress_gravity, DEFAULT_PROGRESS_GRAVITY),
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getLayoutDirection() == LAYOUT_DIRECTION_RTL,
                a.getBoolean(R.styleable.PowerfulImageView_piv_progress_rtl_disabled, DEFAULT_PROGRESS_DISABLE_RTL_SUPPORT),
                a.getBoolean(R.styleable.PowerfulImageView_piv_progress_indeterminate, DEFAULT_PROGRESS_INDETERMINATE),
                a.getBoolean(R.styleable.PowerfulImageView_piv_progress_draw_wedge, DEFAULT_PROGRESS_DETERMINATE_DRAW_WEDGE),
                a.getBoolean(R.styleable.PowerfulImageView_piv_progress_shadow_enabled, DEFAULT_PROGRESS_SHADOW_ENABLED),
                getColor(a, R.styleable.PowerfulImageView_piv_progress_shadow_color, R.color.piv_default_progress_shadow_color),
                tvShadowPadding.type == TypedValue.TYPE_DIMENSION ? (int) tvShadowPadding.getDimension(getResources().getDisplayMetrics()) : DEFAULT_PROGRESS_SHADOW_PADDING,
                tvShadowPadding.type == TypedValue.TYPE_FRACTION ? tvShadowPadding.getFraction(100, 100) : DEFAULT_PROGRESS_SHADOW_PADDING_PERCENT,
                a.getDimensionPixelSize(R.styleable.PowerfulImageView_piv_progress_shadow_border_width, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PROGRESS_SHADOW_BORDER_WIDTH, getResources().getDisplayMetrics())),
                getColor(a, R.styleable.PowerfulImageView_piv_progress_shadow_border_color, R.color.piv_default_progress_shadow_border_color),
                a.getBoolean(R.styleable.PowerfulImageView_piv_progress_reversed, DEFAULT_PROGRESS_REVERSED),
                a.getBoolean(R.styleable.PowerfulImageView_piv_progress_removed_on_change, DEFAULT_PROGRESS_REMOVED_ON_CHANGE)
        );

        PivProgressMode progressMode = PivProgressMode.fromValue(a.getInteger(R.styleable.PowerfulImageView_piv_progress_mode, DEFAULT_PROGRESS_MODE));


        TypedValue tvShapeInnerPadding = new TypedValue();
        a.getValue(R.styleable.PowerfulImageView_piv_shape_inner_padding, tvShapeInnerPadding);

        ShapeOptions shapeOptions = new ShapeOptions(
                getColor(a, R.styleable.PowerfulImageView_piv_shape_background_color, android.R.color.transparent),
                getColor(a, R.styleable.PowerfulImageView_piv_shape_foreground_color, android.R.color.transparent),
                tvShapeInnerPadding.type == TypedValue.TYPE_DIMENSION ? (int) tvShapeInnerPadding.getDimension(getResources().getDisplayMetrics()) : DEFAULT_SHAPE_INNER_PADDING,
                tvShapeInnerPadding.type == TypedValue.TYPE_FRACTION ? tvShapeInnerPadding.getFraction(100, 100) : DEFAULT_SHAPE_INNER_PADDING_PERCENT,
                a.getBoolean(R.styleable.PowerfulImageView_piv_shape_border_overlay, DEFAULT_SHAPE_BORDER_OVERLAY),
                getColor(a, R.styleable.PowerfulImageView_piv_shape_border_color, android.R.color.transparent),
                a.getDimensionPixelSize(R.styleable.PowerfulImageView_piv_shape_border_width, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SHAPE_BORDER_WIDTH, getResources().getDisplayMetrics())),
                a.getFloat(R.styleable.PowerfulImageView_piv_shape_ratio, DEFAULT_SHAPE_RATIO),
                a.getFloat(R.styleable.PowerfulImageView_piv_shape_radius_x, DEFAULT_SHAPE_RADIUS_X),
                a.getFloat(R.styleable.PowerfulImageView_piv_shape_radius_y, DEFAULT_SHAPE_RADIUS_Y),
                getColor(a, R.styleable.PowerfulImageView_piv_shape_solid_color, R.color.piv_default_shape_solid_color),
                a.getDrawable(R.styleable.PowerfulImageView_piv_shape_background),
                a.getDrawable(R.styleable.PowerfulImageView_piv_shape_foreground)
        );


        PivShapeMode shapeMode = PivShapeMode.fromValue(a.getInteger(R.styleable.PowerfulImageView_piv_shape_mode, DEFAULT_SHAPE_MODE));
        //I use the android scale type used as default. If a PivShapeScaleType type is passed, it overrides Android scaleType
        PivShapeScaleType scaleType = PivShapeScaleType.fromValue(a.getInteger(R.styleable.PowerfulImageView_piv_shape_scaleType, PivShapeScaleType.getFromScaleType(getScaleType()).getValue()));


        BlurOptions blurOptions = new BlurOptions(
                a.getFloat(R.styleable.PowerfulImageView_piv_blur_down_sampling_rate, DEFAULT_BLUR_DOWNSAMPLING_RATE),
                a.getBoolean(R.styleable.PowerfulImageView_piv_blur_static, DEFAULT_BLUR_STATIC),
                a.getBoolean(R.styleable.PowerfulImageView_piv_blur_use_rs_fallback, DEFAULT_BLUR_USE_RENDERSCRIPT_FALLBACK),
                a.getInteger(R.styleable.PowerfulImageView_piv_blur_num_threads, DEFAULT_BLUR_NUM_THREADS)
        );

        int blurRadius = a.getInteger(R.styleable.PowerfulImageView_piv_blur_radius, DEFAULT_BLUR_RADIUS);
        int blurModeValue = a.getInteger(R.styleable.PowerfulImageView_piv_blur_mode, DEFAULT_BLUR_MODE);
        PivBlurMode blurMode = PivBlurMode.fromValue(blurModeValue);

        a.recycle();

        this.mProgressDrawerManager = new ProgressDrawerManager(this, progressOptions);
        this.mShapeDrawerManager = new ShapeDrawerManager(this, shapeOptions);
        this.mBlurManager = new BlurManager(this, blurOptions);
        mShapeDrawerManager.setScaleType(scaleType);
        mShouldCheckRemoveProgress = false;

        //the first time it was called, mShapeDrawerManager is null, so it's skipped.
        //So i call it here, after everything else is instantiated.
        onDrawableChanged();

        setProgressMode(progressMode);
        setShapeMode(shapeMode);
        setBlurMode(blurMode, blurRadius);

        mShouldCheckRemoveProgress = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //updates progress bounds
        mProgressDrawerManager.onSizeChanged(w, h);

        mShapeDrawerManager.onSizeChanged(w, h, getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());

        mBlurManager.onSizeChanged(mShapeDrawerManager.getMeasuredWidth(), mShapeDrawerManager.getMeasuredHeight(), getDrawable() != null ? getDrawable().getCurrent() : getDrawable());

        mShouldCheckRemoveProgress = false;
        blurBitmap(false);
        mShouldCheckRemoveProgress = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //I go further only if there is a custom shape selected
        if(mShapeDrawerManager.getShapeMode() == PivShapeMode.NORMAL){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mShapeDrawerManager.onMeasure(widthSize, heightSize, widthMode, heightMode, this);

        //MUST CALL THIS
        setMeasuredDimension(mShapeDrawerManager.getMeasuredWidth(), mShapeDrawerManager.getMeasuredHeight());
    }

    /**
     * Method called when the drawable has been changed
     */
    @Override
    void onDrawableChanged() {

        //if the image comes from super methods and i need to blur it, I blur it
        if(blurBitmap(true)) {
            return;
        }

        //when initializing (in constructor) it gets called, but it is still null
        if (mShouldCheckRemoveProgress && mProgressDrawerManager != null && getDrawable() != null)
            mProgressDrawerManager.changeDrawable(getDrawable().getCurrent());

        //when initializing (in constructor) it gets called, but it is still null
        if (mShapeDrawerManager != null && getDrawable() != null)
            mShapeDrawerManager.changeDrawable(getDrawable().getCurrent());

    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        super.setScaleType(scaleType);
        if(mShapeDrawerManager != null)
            mShapeDrawerManager.setScaleType(PivShapeScaleType.getFromScaleType(scaleType));
    }

    /**
     *
     * Controls how the image should be resized or moved to match the size of this ImageView.
     * Added to provide additional custom scale types.
     * Overrides ImageView's changeScaleType(ImageView.ScaleType) method.
     *
     * @param scaleType The desired scaling mode.
     */
    public final void setShapeScaleType(PivShapeScaleType scaleType) {
        super.setScaleType(ScaleType.MATRIX);
        if(mShapeDrawerManager != null)
            mShapeDrawerManager.setScaleType(scaleType);
    }


    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        if(mShapeDrawerManager != null)
            mShapeDrawerManager.setImageMatrix(matrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //draw image shape
        mShapeDrawerManager.onDraw(canvas);

        //draw progress indicator
        mProgressDrawerManager.onDraw(canvas);
    }


    /**
     * Changes the progress mode of the indicator (e.g. passing from determinate to indeterminate).
     * It also starts animation of indeterminate progress indicator.
     *
     * @param progressMode mode to change the progress indicator into
     */
    public final void setProgressMode(PivProgressMode progressMode){
        mProgressDrawerManager.changeProgressMode(progressMode, false);
    }


    /**
     * Changes the shape of the image.
     *
     * @param shapeMode shape to change the image into
     */
    public final void setShapeMode(PivShapeMode shapeMode){
        mShapeDrawerManager.changeShapeMode(shapeMode);
    }


    /**
     * Changes the blur mode and the radius to blur the image.
     *
     * @param blurMode mode to use to blur the image
     * @param radius radius to use when blurring the image: the higher the radius, the more the blurring.
     */
    public final void setBlurMode(PivBlurMode blurMode, int radius){
        mCheckBlur = blurMode != PivBlurMode.DISABLED;
        if(mBlurManager == null)
            return;

        mBlurManager.changeMode(blurMode, radius);
        blurBitmap(false);
    }


    /**
     * Changes the blur radius to blur the image.
     *
     * @param radius radius to use when blurring the image: the higher the radius, the more but slower the blurring.
     */
    public final void setBlurRadius(int radius){
        if(mBlurManager == null)
            return;

        mCheckBlur = mBlurManager.getBlurMode() != PivBlurMode.DISABLED;

        mBlurManager.changeRadius(radius);
        blurBitmap(false);
    }

    /**
     * Blur the image, if needed.
     * @return True if the image is blurred and will be set through setBitmap()
     *         False if the image doesn't need to be blurred
     */
    private boolean blurBitmap(boolean changeDrawable){

        if(!mCheckBlur || mBlurManager == null || getDrawable() == null)
            return false;

        boolean shouldBlur = mBlurManager.shouldBlur(getDrawable().getCurrent(), changeDrawable);

        if(changeDrawable)
            mBlurManager.changeDrawable(getDrawable().getCurrent());

        Bitmap blurredBitmap = null;
        if(shouldBlur) {
            blurredBitmap = mBlurManager.getLastBlurredBitmap();

            if(blurredBitmap != null){
                mCheckBlur = false;
                setImageBitmap(blurredBitmap);
                mCheckBlur = true;
            }
        }

        return shouldBlur && blurredBitmap != null;
    }


    /**
     * @param isIndeterminate whether the progress indicator is indeterminate or not
     */
    public final void setProgressIndeterminate(boolean isIndeterminate){
        mProgressDrawerManager.getProgressOptions().setIndeterminate(isIndeterminate);
    }



    /**
     * @return The options of the progress indicator
     */
    public final ProgressOptions getProgressOptions() {
        return mProgressDrawerManager.getProgressOptions();
    }

    /**
     * @return The selected progress mode
     */
    public final PivProgressMode getProgressMode(){
        return mProgressDrawerManager.getProgressMode();
    }

    /**
     * @return The selected shape mode
     */
    public final PivShapeMode getShapeMode(){
        return mShapeDrawerManager.getShapeMode();
    }

    /**
     * @return The selected shape mode
     */
    public final PivBlurMode getBlurMode(){
        return mBlurManager.getBlurMode();
    }

    /**
     * @return The options of the shape
     */
    public final ShapeOptions getShapeOptions() {
        return mShapeDrawerManager.getShapeOptions();
    }

    /**
     * @return The options of the blur
     */
    public final BlurOptions getBlurOptions() {
        return mBlurManager.getBlurOptions();
    }

    /**
     * @return The selected radius used for blurring
     */
    public final int getBlurRadius() {
        return mBlurManager.getRadius();
    }

    /**
     * Returns the last blurred bitmap. If the bitmap was never blurred, or blur options, mode or radius
     * changed since the last blur, the bitmap will be blurred again (if static option is disabled).
     *
     * Don't use this method if you didn't enable blur!
     *
     * @return The blurred bitmap. If any problem occurs, the original bitmap (nullable) will be returned.
     */
    public final Bitmap getBlurBlurredBitmap() {
        return mBlurManager.getLastBlurredBitmap();
    }

    /**
     * Returns the original bitmap used to blur. If static blur option is enabled, this will be the
     * same as the blurred one, since the original bitmap has been released.
     *
     * Don't use this method if you didn't enable blur!
     *
     * @return The original bitmap, or the blurred one if static blur is enabled.
     */
    public final Bitmap getBlurOriginalBitmap() {
        return mBlurManager.getOriginalBitmap();
    }

    /**
     * Sets the progress of the current indicator.
     * If the drawer is indeterminate, it will change its state and make it determinate.
     *
     * @param progress Percentage value of the progress
     */
    public final void setProgressValue(float progress){
        getProgressOptions().setValue(progress);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mBlurManager.addContext(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBlurManager.removeContext(true);
    }

    /** Save the state of the view. */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super_state", super.onSaveInstanceState());
        bundle.putParcelable("progress_drawer_manager", mProgressDrawerManager.saveInstanceState());
        bundle.putParcelable("shape_drawer_manager", mShapeDrawerManager.saveInstanceState());
        bundle.putParcelable("blur_manager", mBlurManager.saveInstanceState());

        return bundle;
    }

    /** Restore the state of the view. */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {// implicit null check
            Bundle bundle = (Bundle) state;
            mProgressDrawerManager.restoreInstanceState((Bundle) bundle.getParcelable("progress_drawer_manager"));
            mShapeDrawerManager.restoreInstanceState((Bundle) bundle.getParcelable("shape_drawer_manager"));
            mBlurManager.restoreInstanceState((Bundle) bundle.getParcelable("blur_manager"));
            state = bundle.getParcelable("super_state");
        }
        super.onRestoreInstanceState(state);
        blurBitmap(true);
    }
}

