package com.stefanosiano.powerfulimageview.progress;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import java.lang.ref.WeakReference;

/**
 * Class that helps managing the options that will be used by the progress drawers.
 */

public final class ProgressOptions implements Parcelable {

    //Options used directly by drawers

    /** If the determinate drawer should update its progress with an animation */
    private boolean mDeterminateAnimationEnabled;

    /** Width of the progress indicator */
    private int mBorderWidth;

    /** Width of the progress indicator as percentage of the progress indicator size */
    private float mBorderWidthPercent;

    /** Percentage value of the progress indicator, used by determinate drawers */
    private float mValuePercent;

    /** Front color of the indicator, used by determinate drawers */
    private int mFrontColor;

    /** Back color of the indicator, used by determinate drawers */
    private int mBackColor;

    /** Color of the indicator, used by indeterminate drawers */
    private int mIndeterminateColor;

    /** If should show a wedge, used by circular determinate drawer */
    private boolean mDrawWedge;

    /** If should show a shadow */
    private boolean mShadowEnabled;

    /** Shadow color of the indicator */
    private int mShadowColor;

    /** Padding of the indicator relative to its shadow */
    private int mShadowPadding;

    /** Padding of the indicator relative to its shadow, as a percentage of the whole shadow */
    private float mShadowPaddingPercent;

    /** Width of the progress indicator shadow border */
    private float mShadowBorderWidth;

    /** Width of the progress indicator shadow border after calculations */
    private float mCalculatedShadowBorderWidth;

    /** Color of the progress indicator shadow border */
    private int mShadowBorderColor;


    //variables used to calculate bounds

    /** Size of the indicator */
    private int mSize;

    /** Padding of the indicator */
    private int mPadding;

    /** Size of the indicator, as a percentage of the whole View */
    private float mSizePercent;

    /** Gravity of the indicator */
    private PivProgressGravity mGravity;

    /** Whether the progress should be reversed */
    private boolean mIsProgressReversed;

    /** Whether the view is using right to left layout (used for gravity option and progress direction) */
    private boolean mIsRtl;

    /** Whether the view should use or ignore right to left layout (used for gravity option and progress direction) */
    private boolean mIsRtlDisabled;

    /** Whether the progress indicator is indeterminate or not */
    private boolean mIsIndeterminate;




    // ************** Calculated fields *****************

    /** Calculated size of the indicator, base on mSize, mSizePercent and View size */
    private float mCalculatedSize;

    /** Calculated padding of the indicator shadow */
    private int mCalculatedShadowPadding;

    /** Calculated width of the progress indicator, base on mBorderWidth, mBorderWidthPercent and mSize */
    private int mCalculatedBorderWidth;


    //bounds of the progress and shadow indicator
    /** Calculated progress bounds calculated */
    private final RectF mRect;

    /** Shadow bounds calculated */
    private final RectF mShadowRect;

    /** Shadow border bounds calculated */
    private final RectF mShadowBorderRect;


    //last calculated width and height
    /** Last width calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastW;

    /** Last height calculated. Used when changing programmatically the options, so bounds can be calculated directly */
    private int mCalculatedLastH;

    /** Last progress mode used. Used when changing programmatically the options, so bounds can be calculated directly */
    private PivProgressMode mCalculatedLastMode;

    /** Listener that will update the progress drawers on changes, with a weak reference to be sure to not leak memory */
    private WeakReference<ProgressOptionsListener> listener;

    /**
     * Creates the object that will be used by progress drawers:
     *
     * @param determinateAnimationEnabled If the determinate drawer should update its progress with an animation
     * @param borderWidth Width of the progress indicator. If it's 0 or more, it applies and overrides "borderWidthPercent" parameter
     * @param borderWidthPercent Width of the progress indicator as a percentage of the progress indicator size
     * @param size Size of the progress indicator. If it's 0 or more, it applies and overrides "sizePercent" parameter
     * @param padding Padding of the progress indicator
     * @param sizePercent Size of the progress indicator as a percentage of the whole View
     * @param valuePercent Percentage value of the progress indicator, used by determinate drawers
     * @param frontColor Front color of the indicator, used by determinate drawers
     * @param backColor Back color of the indicator, used by determinate drawers
     * @param indeterminateColor Color of the indicator, used by indeterminate drawers
     * @param gravity Gravity of the indicator
     * @param rtl Whether the view should use right to left layout (used for gravity option)
     * @param disableRtlSupport If true, rtl attribute will be ignored (start will always be treated as left)
     * @param isIndeterminate If true, indeterminate progress is drawn
     * @param drawWedge If should show a wedge, used by circular determinate drawer
     * @param shadowEnabled If should show a shadow under progress indicator
     * @param shadowColor Color of the shadow
     * @param shadowPadding Padding of the progress indicator, relative to its shadow. If it's 0 or more, it applies and overrides "shadowPaddingPercent" parameter
     * @param shadowPaddingPercent Padding of the progress indicator, relative to its shadow, as a percentage of the shadow
     * @param shadowBorderWidth Width of the progress indicator shadow border
     * @param shadowBorderColor Color of the progress indicator shadow border
     * @param isProgressReversed Whether the progress should be reversed
     */
    public ProgressOptions(boolean determinateAnimationEnabled, int borderWidth, float borderWidthPercent, int size, int padding, float sizePercent, float valuePercent,
                           int frontColor, int backColor, int indeterminateColor, int gravity, boolean rtl, boolean disableRtlSupport, boolean isIndeterminate, boolean drawWedge,
                           boolean shadowEnabled, int shadowColor, int shadowPadding, float shadowPaddingPercent, float shadowBorderWidth, int shadowBorderColor, boolean isProgressReversed) {
        this.mDeterminateAnimationEnabled = determinateAnimationEnabled;
        this.mBorderWidth = borderWidth;
        this.mBorderWidthPercent = borderWidthPercent;
        if(this.mBorderWidthPercent > 100)
            this.mBorderWidthPercent = this.mBorderWidthPercent % 100;
        this.mSize = size;
        this.mPadding = padding;
        this.mSizePercent = sizePercent;
        this.mValuePercent = valuePercent;
        this.mFrontColor = frontColor;
        this.mBackColor = backColor;
        this.mIndeterminateColor = indeterminateColor;
        this.mGravity = PivProgressGravity.fromValue(gravity);
        this.mIsRtl = rtl;
        this.mIsRtlDisabled = disableRtlSupport;
        this.mIsIndeterminate = isIndeterminate;
        this.mDrawWedge = drawWedge;
        this.mShadowEnabled = shadowEnabled;
        this.mShadowColor = shadowColor;
        this.mShadowPadding = shadowPadding;
        this.mShadowPaddingPercent = shadowPaddingPercent;
        this.mShadowBorderWidth = shadowBorderWidth;
        this.mShadowBorderColor = shadowBorderColor;
        this.mIsProgressReversed = isProgressReversed;

        //initialization of private fields used for calculations
        this.mCalculatedSize = 0;
        this.mCalculatedShadowPadding = 0;
        this.mCalculatedBorderWidth = 0;
        this.mCalculatedLastW = 0;
        this.mCalculatedLastH = 0;
        this.mRect = new RectF(0,0,0,0);
        this.mShadowRect = new RectF(0,0,0,0);
        this.mShadowBorderRect = new RectF(0,0,0,0);
        this.mCalculatedLastMode = PivProgressMode.NONE;
    }

    public void setOptions (ProgressOptions other) {
        this.mDeterminateAnimationEnabled = other.mDeterminateAnimationEnabled;
        this.mBorderWidth = other.mBorderWidth;
        this.mBorderWidthPercent = other.mBorderWidthPercent;
        this.mValuePercent = other.mValuePercent;
        this.mFrontColor = other.mFrontColor;
        this.mBackColor = other.mBackColor;
        this.mIndeterminateColor = other.mIndeterminateColor;
        this.mDrawWedge = other.mDrawWedge;
        this.mShadowEnabled = other.mShadowEnabled;
        this.mShadowColor = other.mShadowColor;
        this.mShadowPadding = other.mShadowPadding;
        this.mShadowPaddingPercent = other.mShadowPaddingPercent;
        this.mShadowBorderWidth = other.mShadowBorderWidth;
        this.mCalculatedShadowBorderWidth = other.mCalculatedShadowBorderWidth;
        this.mShadowBorderColor = other.mShadowBorderColor;
        this.mSize = other.mSize;
        this.mPadding = other.mPadding;
        this.mSizePercent = other.mSizePercent;
        this.mGravity = other.mGravity;
        this.mIsRtl = other.mIsRtl;
        this.mIsRtlDisabled = other.mIsRtlDisabled;
        this.mIsIndeterminate = other.mIsIndeterminate;
        this.mCalculatedSize = other.mCalculatedSize;
        this.mCalculatedShadowPadding = other.mCalculatedShadowPadding;
        this.mCalculatedBorderWidth = other.mCalculatedBorderWidth;
        this.mRect.set(other.mRect);
        this.mShadowRect.set(mShadowRect);
        this.mShadowBorderRect.set(mShadowBorderRect);
        this.mCalculatedLastW = other.mCalculatedLastW;
        this.mCalculatedLastH = other.mCalculatedLastH;
        this.mCalculatedLastMode = other.mCalculatedLastMode;
        this.mIsProgressReversed = other.mIsProgressReversed;
        this.listener = other.listener;
    }


    /**
     * Calculates the bounds of the progress indicator, based on progress options and mode.
     * Calculated bounds are accessible after this call through getLeft(), getTop(), getRight() and getBottom() methods.
     *
     * @param w Width of the View
     * @param h Height of the View
     * @param mode Mode of the progress indicator
     */
    public final void calculateBounds(int w, int h, PivProgressMode mode){

        //saving last width and height, so i can later call this function from this class
        mCalculatedLastW = w;
        mCalculatedLastH = h;
        mCalculatedLastMode = mode;

        //if there's no shadow, no border of the shadow should be considered
        mCalculatedShadowBorderWidth = mShadowEnabled ? mShadowBorderWidth : 0;

        if(mode == PivProgressMode.NONE){
            this.mRect.set(0,0,0,0);
            this.mShadowRect.set(0,0,0,0);
            this.mShadowBorderRect.set(0,0,0,0);
            return;
        }

        //calculate the maximum possible size of the progress indicator
        int maxSize = w < h ? w : h;

        switch(mode) {

            //calculation of circular bounds
            case CIRCULAR:
                maxSize = w < h ? w : h;
                break;
            case HORIZONTAL:
                maxSize = w;
                break;
            case NONE:
            default:
                mSize = 0;
                break;
        }

        float calculatedShadowBorderWidthHalf = mCalculatedShadowBorderWidth / 2;


        //********** SIZE ***********
        mCalculatedSize = maxSize * mSizePercent / 100;
        //if mSize is 0 or more, it overrides mSizePercent parameter
        if(mSize >= 0) mCalculatedSize = mSize;

        //the progress indicator cannot be bigger than the view (minus padding)
        if(mCalculatedSize > maxSize - mPadding - mPadding) mCalculatedSize = maxSize - mPadding - mPadding;



        //********** SHADOW PADDING ***********
        mCalculatedShadowPadding = (int) ((mCalculatedSize - mCalculatedShadowBorderWidth*2) * mShadowPaddingPercent / 100);
        //if mShadowPadding is 0 or more, it overrides mShadowPaddingPercent parameter
        if(mShadowPadding >= 0) mCalculatedShadowPadding = mShadowPadding;

        //if shadow is not enabled, shadow padding is set to 0
        if(!mShadowEnabled) mCalculatedShadowPadding = 0;



        //********** BORDER WIDTH ***********
        mCalculatedBorderWidth = Math.round((mCalculatedSize - mCalculatedShadowBorderWidth*2) * mBorderWidthPercent/100);
        //if mBorderWidth is 0 or more, it overrides mBorderWidthPercent paramenter
        if(mBorderWidth >= 0) mCalculatedBorderWidth = mBorderWidth;

        //width of the border should be at least 1 px
        if(mCalculatedBorderWidth < 1) mCalculatedBorderWidth = 1;



        float left, top;
        //********** BOUNDS ***********
        switch(mode){

            //calculation of circular bounds
            case CIRCULAR:

                //horizontal gravity
                if(mGravity.isGravityLeft(mIsRtl && !mIsRtlDisabled)){
                    left = mPadding;
                } else if(mGravity.isGravityRight(mIsRtl && !mIsRtlDisabled)){
                    left = w - mCalculatedSize - mPadding;
                } else {
                    left = (w - mCalculatedSize) /2;
                }

                //vertical gravity
                if(mGravity.isGravityTop()) {
                    top = mPadding;
                } else if (mGravity.isGravityBottom()) {
                    top = h - mCalculatedSize - mPadding;
                } else {
                    top = (h - mCalculatedSize) / 2;
                }

                this.mShadowBorderRect.set(
                        left + calculatedShadowBorderWidthHalf,
                        top + calculatedShadowBorderWidthHalf,
                        left + mCalculatedSize - mCalculatedShadowBorderWidth,
                        top + mCalculatedSize - mCalculatedShadowBorderWidth);

                this.mShadowRect.set(mShadowBorderRect);
                this.mShadowRect.inset(calculatedShadowBorderWidthHalf, calculatedShadowBorderWidthHalf);

                this.mRect.set(mShadowRect);
                this.mRect.inset(mCalculatedShadowPadding + mCalculatedBorderWidth / 2, mCalculatedShadowPadding + mCalculatedBorderWidth / 2);

                break;

            //calculation of horizontal bounds
            case HORIZONTAL:

                //horizontal gravity
                if(mGravity.isGravityLeft(mIsRtl && !mIsRtlDisabled)){
                    left = mPadding;
                } else if(mGravity.isGravityRight(mIsRtl && !mIsRtlDisabled)){
                    left = w - mCalculatedSize - mPadding;
                } else {
                    left = (w - mCalculatedSize)/2;
                }

                //vertical gravity
                if(mGravity.isGravityTop()) {
                    top = mPadding;
                } else if (mGravity.isGravityBottom()) {
                    top = h - mCalculatedBorderWidth -  mPadding;
                } else {
                    top = (h - mCalculatedBorderWidth -  mPadding)/2;
                }

                this.mShadowBorderRect.set(
                        left + calculatedShadowBorderWidthHalf,
                        top + calculatedShadowBorderWidthHalf,
                        left + mCalculatedSize - mCalculatedShadowBorderWidth,
                        top + mCalculatedBorderWidth - calculatedShadowBorderWidthHalf);

                this.mShadowRect.set(mShadowBorderRect);
                this.mShadowRect.inset(calculatedShadowBorderWidthHalf, calculatedShadowBorderWidthHalf);

                this.mRect.set(mShadowRect);
                this.mRect.inset(mCalculatedShadowPadding, mCalculatedShadowPadding);

                break;

            //if everything goes right, it should never come here. Just a precaution
            case NONE:
            default:
                this.mRect.set(0,0,0,0);
                this.mShadowRect.set(0,0,0,0);
                this.mShadowBorderRect.set(0,0,0,0);
                break;
        }
    }

    /** Returns the progress calculated bounds. Be sure to call calculateBounds() before this!
     * Don't change directly its values! If you want to change them, create a copy! */
    public final RectF getRect() {
        return mRect;
    }

    /** Returns the progress shadow calculated bounds. Be sure to call calculateBounds() before this!
     * Don't change directly its values! If you want to change them, create a copy! */
    public final RectF getShadowRect() {
        return mShadowRect;
    }

    /** Returns the progress shadow border calculated bounds. Be sure to call calculateBounds() before this!
     * Don't change directly its values! If you want to change them, create a copy! */
    public final RectF getShadowBorderRect() {
        return mShadowBorderRect;
    }




    /**
     * Set whether the determinate drawer should update its progress with an animation.
     * If the drawer is not determinate or horizontal_determinate it's ignored.
     *
     * @param determinateAnimationEnabled If true it updates its progress with an animation, otherwise it will update instantly
     */
    public void setDeterminateAnimationEnabled(boolean determinateAnimationEnabled) {
        this.mDeterminateAnimationEnabled = determinateAnimationEnabled;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /**
     * Width of the progress indicator.
     * Overrides border width set through setBorderWidthPercent().
     * If it's lower than 0, it is ignored.
     * If you want to use dp, set value using TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, getResources().getDisplayMetrics())
     *
     * @param borderWidth Width of the progress indicator
     */
    public void setBorderWidth(int borderWidth) {
        this.mBorderWidth = borderWidth;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Width of the progress indicator as percentage of the progress indicator size.
     * It's used only if borderWidth is less than 0.
     * If the percentage is higher than 100, it is treated as (value % 100).
     *
     * @param borderWidthPercent Percentage of the progress indicator size, as a float from 0 to 100
     */
    public void setBorderWidthPercent(float borderWidthPercent) {
        if(borderWidthPercent > 100)
            borderWidthPercent = borderWidthPercent % 100;
        this.mBorderWidthPercent = borderWidthPercent;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Percentage value of the progress indicator, used by determinate drawers.
     * If the drawer is indeterminate, it will change its state and make it determinate.
     * If the percentage is higher than 100, it is treated as (value % 100).
     * If the percentage is lower than 0, it is treated as 0.
     * If the drawer is not determinate or horizontal_determinate it's ignored.
     * Note: multiplies of 100 (e.g. 200, 300, ...) will be treated as 0!
     *
     * @param valuePercent Percentage of the progress indicator, as a float from 0 to 100
     */
    public void setValuePercent(float valuePercent) {
        if(valuePercent > 100)
            valuePercent = valuePercent % 100;
        if(valuePercent < 0)
            valuePercent = 0;
        this.mValuePercent = valuePercent;

        //if it's indeterminate, I change it to determinate and the mode changes, otherwise I just update current drawer
        boolean modeChanged = mIsIndeterminate;

        this.mIsIndeterminate = false;

        if(listener.get() != null) {
            if(modeChanged)
                listener.get().onModeUpdated(this);
            else
                listener.get().onOptionsUpdated(this);
        }
    }

    /**
     * Set the front color of the indicator, used by determinate drawers.
     * If the drawer is not determinate or horizontal_determinate it's ignored.
     *
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.
     *
     * @param frontColor Color to use.
     */
    public void setFrontColor(int frontColor) {
        this.mFrontColor = frontColor;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }


    /**
     * Set the back color of the indicator, used by determinate drawers.
     * If the drawer is not determinate or horizontal_determinate it's ignored.
     *
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.
     *
     * @param backColor Color to use.
     */
    public void setBackColor(int backColor) {
        this.mBackColor = backColor;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }


    /**
     * Set the front color of the indicator, used by indeterminate drawers.
     * If the drawer is not indeterminate or horizontal_indeterminate it's ignored.
     *
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.
     *
     * @param indeterminateColor Color to use.
     */
    public void setIndeterminateColor(int indeterminateColor) {
        this.mIndeterminateColor = indeterminateColor;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /**
     * Set whether to show a wedge or a circle, used by circular determinate drawer.
     * If the drawer is not determinate it's ignored.
     *
     * @param mDrawWedge If true, a wedge is drawn, otherwise a circle will be drawn
     */
    public void setDrawWedge(boolean mDrawWedge) {
        this.mDrawWedge = mDrawWedge;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /**
     * Size of the progress indicator.
     *
     * Overrides size set through setSizePercent().
     * It's less than 0, it is ignored.
     * Note that it may be different from the actual size used to draw the progress, since it is
     *      calculated based on the View size, on the sizePercent option and on the padding option.
     * If you want to use dp, set value using TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, getResources().getDisplayMetrics())
     *
     * @param size Size of the progress indicator
     */
    public void setSize(int size) {
        this.mSize = size;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }


    /**
     * Set the padding of the progress indicator.
     *
     * If you want to use dp, set value using TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, getResources().getDisplayMetrics())
     *
     * @param padding Padding of the progress indicator
     */
    public void setPadding(int padding) {
        this.mPadding = padding;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set the size of the progress indicator.
     *
     * It's used only if progressSize is less than 0.
     * If the percentage is higher than 100, it is treated as (value % 100).
     *
     * @param sizePercent Progress indicator size as a percentage of the whole View, as a float from 0 to 100
     */
    public void setSizePercent(float sizePercent) {
        if(sizePercent > 100)
            sizePercent = sizePercent % 100;
        this.mSizePercent = sizePercent;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set the gravity of the indicator.
     * It will follow the right to left layout (on api 17+), if not disabled.
     *
     * @param mGravity Gravity of the indicator
     */
    public void setGravity(PivProgressGravity mGravity) {
        this.mGravity = mGravity;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set whether the view should use right to left layout (used for gravity option)
     *
     * @param rtlDisabled If true, start will always be treated as left and end as right.
     *                      If false, on api 17+, gravity will be treated accordingly to rtl rules.
     */
    public void setRtlDisabled(boolean rtlDisabled) {
        this.mIsRtlDisabled = rtlDisabled;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set whether the view should use right to left layout (used for gravity option)
     *
     * @param progressReversed If true, progress will be reversed. It gets adjusted by rtl rules (if rtl is not disabled)
     */
    public void setProgressReversed(boolean progressReversed) {
        this.mIsProgressReversed = progressReversed;

        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /**
     * Set whether the view should use right to left layout (used for gravity option)
     *
     * @param isIndeterminate If true, indeterminate progress is drawn.
     *                      If false, determinate is drawn.
     */
    public void setIndeterminate(boolean isIndeterminate) {
        //if it's indeterminate, I change it to determinate and the mode changes, otherwise I just update current drawer
        boolean modeChanged = mIsIndeterminate != isIndeterminate;
        this.mIsIndeterminate = isIndeterminate;

        if(listener.get() != null) {
            if(modeChanged)
                listener.get().onModeUpdated(this);
            else
                listener.get().onOptionsUpdated(this);
        }
    }

    /**
     * Set the shadow color of the indicator, used by drawers.
     *
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.
     *
     * @param shadowColor Color to use.
     */
    public void setShadowColor(int shadowColor) {
        this.mShadowColor = shadowColor;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }

    /**
     * Set whether to show a progress shadow.
     *
     * @param shadowEnabled If true, the shadow is drawn
     */
    public void setShadowEnabled(boolean shadowEnabled) {
        this.mShadowEnabled = shadowEnabled;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set the padding of the progress indicator relative to its shadow.
     * If it's lower than 0, it is ignored.
     * Overrides shadow padding set through setShadowPaddingPercent().
     *
     * @param padding Padding of the progress indicator shadow
     */
    public void setShadowPadding(int padding) {
        this.mShadowPadding = padding;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Set the padding of the progress indicator relative to its shadow.
     * It's used only if shadowPadding is less than 0.
     * If the percentage is higher than 100, it is treated as (value % 100).
     *
     * @param paddingPercent Progress indicator shadow padding as a percentage of the whole shadow, as a float from 0 to 100
     */
    public void setShadowPaddingPercent(float paddingPercent) {
        if(paddingPercent > 100)
            mShadowPaddingPercent = paddingPercent % 100;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }


    /**
     * Set the color of the progress indicator shadow border.
     *
     * Note that the color is an int containing alpha as well as r,g,b. This 32bit value is not
     * premultiplied, meaning that its alpha can be any value, regardless of the values of r,g,b.
     * See the Color class for more details.
     *
     * @param shadowBorderColor Color of the progress indicator shadow border
     */
    public void setShadowBorderColor(int shadowBorderColor) {
        this.mShadowBorderColor = shadowBorderColor;
        if(listener.get() != null)
            listener.get().onOptionsUpdated(this);
    }


    /**
     * Set the width of the progress indicator shadow border.
     * If you want to use dp, set value using TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderWidth, getResources().getDisplayMetrics())
     *
     * @param shadowBorderWidth Width of the progress indicator shadow border
     */
    public void setShadowBorderWidth(float shadowBorderWidth) {
        this.mShadowBorderWidth = shadowBorderWidth;
        calculateBounds(mCalculatedLastW, mCalculatedLastH, mCalculatedLastMode);
        if(listener.get() != null)
            listener.get().onSizeUpdated(this);
    }

    /**
     * Returns the width percentage of the progress indicator size.
     * If you want to get the real width used to show the progress, call getCalculatedBorderWidth().
     * @return Border width percentage of the progress indicator size
     */
    public float getBorderWidthPercent() {
        return mBorderWidthPercent;
    }

    /**
     * Returns the width of the progress indicator
     * If you want to get the real width used to show the progress, call getCalculatedBorderWidth().
     * @return Width of the progress indicator
     */
    public final int getBorderWidth() {
        return mBorderWidth;
    }

    /**
     * Returns the size of the progress indicator.
     * If you want to get the real size used to show the progress, call getCalculatedSize().
     * @return Returns the size of the progress indicator
     */
    public int getSize() {
        return mSize;
    }

    /**
     * Returns the size percentage of the whole View size
     * If you want to get the real size used to show the progress, call getCalculatedSize().
     * @return Size percentage of the whole View size
     */
    public float getSizePercent() {
        return mSizePercent;
    }

    /**
     * @return Padding of the progress indicator
     */
    public int getPadding() {
        return mPadding;
    }

    /**
     * @return Gravity of the progress indicator size
     */
    public PivProgressGravity getGravity() {
        return mGravity;
    }

    /**
     * @return Wheter rtl support is disabled
     */
    public boolean isRtlDisabled() {
        return mIsRtlDisabled;
    }

    /**
     * @return Wheter the progress is indeterminate
     */
    public boolean isIndeterminate() {
        return mIsIndeterminate;
    }

    /**
     * Padding of the progress indicator shadow.
     * If you want to get the real padding used to show the shadow, call getCalculatedShadowPadding().
     *
     * @return Padding of the progress indicator shadow
     */
    public int getShadowPadding() {
        return this.mShadowPadding;
    }

    /**
     * Padding of the progress indicator shadow, as a percentage of the whole shadow.
     * If you want to get the real padding used to show the shadow, call getCalculatedShadowPadding().
     *
     * @return Padding of the progress indicator shadow, as a percentage of the whole shadow
     */
    public float getShadowPaddingPercent() {
        return this.mShadowPaddingPercent;
    }

    /**
     * The size of the progress indicator, after calculations.
     * This will return the real value used by the progress indicator to show.
     *
     * @return The size of the progress indicator, after calculations
     */
    public float getCalculatedSize() {
        return mCalculatedSize;
    }

    /**
     * Padding of the progress indicator shadow, after calculations.
     * This will return the real value used by the progress shadow to show.
     *
     * @return Padding of the progress indicator shadow, after calculations
     */
    public float getCalculatedShadowPadding() {
        return this.mCalculatedShadowPadding;
    }

    /**
     * @return Whether the progress should be reversed
     */
    public boolean isProgressReversed() {
        //if view is rtl, and rtl is not disabled, I change the direction
        return (mIsRtl && !mIsRtlDisabled) ? !mIsProgressReversed : mIsProgressReversed;
    }


    // *************** Fields used by drawers ****************

    /**
     * If the determinate drawer should update its progress with an animation
     *
     * @return true to use animation, false otherwise
     */
    public final boolean isDeterminateAnimationEnabled() {
        return mDeterminateAnimationEnabled;
    }

    /**
     * @return Percentage value of the progress indicator of determinate drawers
     */
    public final float getValuePercent() {
        return mValuePercent;
    }

    /**
     * The border width of the progress indicator, after calculations.
     * This will return the real value used by the progress indicator to show.
     *
     * @return The border width of the progress indicator, after calculations.
     */
    public int getCalculatedBorderWidth() {
        return mCalculatedBorderWidth;
    }

    /**
     * @return Front color of the indicator of determinate drawers
     */
    public final int getFrontColor() {
        return mFrontColor;
    }

    /**
     * @return  Back color of the indicator of determinate drawers
     */
    public final int getBackColor() {
        return mBackColor;
    }

    /**
     * @return Color of the indicator of indeterminate drawers
     */
    public final int getIndeterminateColor() {
        return mIndeterminateColor;
    }

    /** If should show a wedge on the circular determinate drawer
     * @return If true shows a wedge, otherwise shows a circle
     */
    public final boolean isDrawWedge() {
        return mDrawWedge;
    }

    /**
     * Set the listener that will update the progress drawers on changes
     *
     * @param listener Listener that will update the progress drawers on changes
     */
    public void setListener(ProgressOptionsListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    /**
     * @return Color of the indicator shadow
     */
    public int getShadowColor() {
        return this.mShadowColor;
    }

    /**
     * @return Color of the indicator shadow border
     */
    public int getShadowBorderColor() {
        return mShadowBorderColor;
    }

    /**
     * @return Indicator shadow border width
     */
    public float getShadowBorderWidth() {
        return mCalculatedShadowBorderWidth;
    }

    /**
     * @return whether to show a progress shadow
     */
    public boolean isShadowEnabled() {
        return this.mShadowEnabled;
    }








    //Parcelable stuff

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mDeterminateAnimationEnabled ? 1 : 0));
        dest.writeInt(mBorderWidth);
        dest.writeFloat(mBorderWidthPercent);
        dest.writeFloat(mValuePercent);
        dest.writeInt(mFrontColor);
        dest.writeInt(mBackColor);
        dest.writeInt(mIndeterminateColor);
        dest.writeByte((byte) (mDrawWedge ? 1 : 0));
        dest.writeByte((byte) (mShadowEnabled ? 1 : 0));
        dest.writeInt(mShadowColor);
        dest.writeInt(mShadowPadding);
        dest.writeFloat(mShadowPaddingPercent);
        dest.writeFloat(mShadowBorderWidth);
        dest.writeFloat(mCalculatedShadowBorderWidth);
        dest.writeInt(mShadowBorderColor);
        dest.writeInt(mSize);
        dest.writeInt(mPadding);
        dest.writeFloat(mSizePercent);
        dest.writeByte((byte) (mIsRtl ? 1 : 0));
        dest.writeByte((byte) (mIsRtlDisabled ? 1 : 0));
        dest.writeByte((byte) (mIsIndeterminate ? 1 : 0));
        dest.writeByte((byte) (mIsProgressReversed ? 1 : 0));
        dest.writeFloat(mCalculatedSize);
        dest.writeInt(mCalculatedShadowPadding);
        dest.writeInt(mCalculatedBorderWidth);
        dest.writeParcelable(mRect, flags);
        dest.writeParcelable(mShadowRect, flags);
        dest.writeParcelable(mShadowBorderRect, flags);
        dest.writeInt(mCalculatedLastW);
        dest.writeInt(mCalculatedLastH);
    }

    protected ProgressOptions(Parcel in) {
        mDeterminateAnimationEnabled = in.readByte() != 0;
        mBorderWidth = in.readInt();
        mBorderWidthPercent = in.readFloat();
        mValuePercent = in.readFloat();
        mFrontColor = in.readInt();
        mBackColor = in.readInt();
        mIndeterminateColor = in.readInt();
        mDrawWedge = in.readByte() != 0;
        mShadowEnabled = in.readByte() != 0;
        mShadowColor = in.readInt();
        mShadowPadding = in.readInt();
        mShadowPaddingPercent = in.readFloat();
        mShadowBorderWidth = in.readFloat();
        mCalculatedShadowBorderWidth = in.readFloat();
        mShadowBorderColor = in.readInt();
        mSize = in.readInt();
        mPadding = in.readInt();
        mSizePercent = in.readFloat();
        mIsRtl = in.readByte() != 0;
        mIsRtlDisabled = in.readByte() != 0;
        mIsIndeterminate = in.readByte() != 0;
        mIsProgressReversed = in.readByte() != 0;
        mCalculatedSize = in.readFloat();
        mCalculatedShadowPadding = in.readInt();
        mCalculatedBorderWidth = in.readInt();
        mRect = in.readParcelable(RectF.class.getClassLoader());
        mShadowRect = in.readParcelable(RectF.class.getClassLoader());
        mShadowBorderRect = in.readParcelable(RectF.class.getClassLoader());
        mCalculatedLastW = in.readInt();
        mCalculatedLastH = in.readInt();
    }

    public static final Creator<ProgressOptions> CREATOR = new Creator<ProgressOptions>() {
        @Override
        public ProgressOptions createFromParcel(Parcel in) {
            return new ProgressOptions(in);
        }

        @Override
        public ProgressOptions[] newArray(int size) {
            return new ProgressOptions[size];
        }
    };







    public interface ProgressOptionsListener{
        void onOptionsUpdated(ProgressOptions options);
        void onSizeUpdated(ProgressOptions options);
        void onModeUpdated(ProgressOptions options);
    }

}
