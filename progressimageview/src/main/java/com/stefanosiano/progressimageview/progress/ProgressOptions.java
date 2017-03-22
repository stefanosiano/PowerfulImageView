package com.stefanosiano.progressimageview.progress;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.TypedValue;

/**
 * Class that helps managing the options that will be used by the progress drawers.
 */

public final class ProgressOptions implements Parcelable {

    //Options used directly by drawers
    
    /** If the determinate drawer should update its progress with an animation */
    private boolean mIsDeterminateAnimationEnabled;

    /** Width of the progress indicator */
    private int mBorderWidth;

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

    
    //variables used to calculate bounds

    /** Size of the indicator */
    private int mSize;

    /** Padding of the indicator */
    private int mPadding;

    /** Size of the indicator, as a percentage of the whole View */
    private float mSizePercent;

    /** Gravity of the indicator */
    private PivProgressGravity mGravity;

    /** Whether the border width has been defined. If it is false, it will be calculated based on the size */
    private boolean mIsCircleBorderWidthFixed;

    /** Whether the view should use right to left layout (used for gravity option) */
    private boolean mIsRtl;
    
    //bounds of the progress indicator
    private float mLeft, mTop, mRight, mBottom;


    /**
     * Creates the object that will be used by progress drawers:
     *
     * @param isDeterminateAnimationEnabled If the determinate drawer should update its progress with an animation
     * @param borderWidth Width of the progress indicator. If it's 0 or negative, it will be automatically adjusted based on the size
     * @param size Size of the progress indicator
     * @param padding Padding of the progress indicator
     * @param sizePercent Size of the progress indicator as a percentage of the whole View. If it's 0 or more, it applies and overrides "size" parameter
     * @param valuePercent Percentage value of the progress indicator, used by determinate drawers
     * @param frontColor Front color of the indicator, used by determinate drawers
     * @param backColor Back color of the indicator, used by determinate drawers
     * @param indeterminateColor Color of the indicator, used by indeterminate drawers
     * @param gravity Gravity of the indicator
     * @param rtl Whether the view should use right to left layout (used for gravity option)
     * @param disableRtlSupport If true, rtl attribute will be ignored (start will always be treated as left)              
     * @param drawWedge If should show a wedge, used by circular determinate drawer
     */
    public ProgressOptions(boolean isDeterminateAnimationEnabled, int borderWidth, int size, int padding, float sizePercent, float valuePercent,
                           int frontColor, int backColor, int indeterminateColor, int gravity, boolean rtl, boolean disableRtlSupport, boolean drawWedge) {
        this.mIsDeterminateAnimationEnabled = isDeterminateAnimationEnabled;
        this.mBorderWidth = borderWidth;
        this.mIsCircleBorderWidthFixed = borderWidth > 0;
        this.mSize = size;
        this.mPadding = padding;
        this.mSizePercent = sizePercent;
        this.mValuePercent = valuePercent;
        this.mFrontColor = frontColor;
        this.mBackColor = backColor;
        this.mIndeterminateColor = indeterminateColor;
        this.mGravity = PivProgressGravity.fromValue(gravity);
        this.mIsRtl = rtl && !disableRtlSupport;
        this.mDrawWedge = drawWedge;
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
        if(mode == PivProgressMode.NONE){
            mLeft = 0;
            mRight = 0;
            mTop = 0;
            mBottom = 0;
            return;
        }

        //calculate the maximum possible size of the progress indicator
        int maxSize = w < h ? w : h;
        maxSize = maxSize - mPadding - mPadding;

        //if mSizePercent is 0 or more, it overrides mSize parameter
        if(mSizePercent >= 0){
            mSize = (int) (maxSize * (double) mSizePercent / 100);
        }
        //the progress indicator cannot be bigger then the view (minus padding)
        if(mSize > maxSize)
            mSize = maxSize;
        //if border width was not been defined, it gets calculated based on the size of the indicator
        if(!mIsCircleBorderWidthFixed){
            mBorderWidth = Math.round(mSize /10);
        }
        //width of the border should be at least 1 px
        if(mBorderWidth < 1)
            mBorderWidth = 1;

        //calculation of bounds
        switch(mode){

            //calculation of circular bounds
            case DETERMINATE:
            case INDETERMINATE:
                switch (mGravity){
                    case START:
                    case BOTTOM_START:
                    case TOP_START:
                        if(mIsRtl){
                            //it's at right
                            mLeft = w - mSize + mBorderWidth/2 - mPadding;
                            mRight = w - mBorderWidth/2 - mPadding;
                        }
                        else {
                            //it's at left
                            mLeft = mBorderWidth/2 + mPadding;
                            mRight = mSize - mBorderWidth/2 + mPadding;
                        }
                        break;
                    case END:
                    case BOTTOM_END:
                    case TOP_END:
                        if(mIsRtl){
                            //it's at left
                            mLeft = mBorderWidth/2 + mPadding;
                            mRight = mSize - mBorderWidth/2 + mPadding;
                        }
                        else {
                            //it's at right
                            mLeft = w - mSize + mBorderWidth/2 - mPadding;
                            mRight = w - mBorderWidth/2 - mPadding;
                        }
                        break;
                    case TOP:
                    case BOTTOM:
                    case CENTER:
                        //it's in center
                        mLeft = (w - mSize + mBorderWidth) /2;
                        mRight = (w + mSize - mBorderWidth) /2;
                        break;
                }
                switch (mGravity){
                    case TOP_START:
                    case TOP_END:
                    case TOP:
                        //it's on top
                        mTop = mBorderWidth/2 + mPadding;
                        mBottom = mSize - mBorderWidth/2 + mPadding;
                        break;
                    case BOTTOM:
                    case BOTTOM_START:
                    case BOTTOM_END:
                        //it's on bottom
                        mTop = h - mSize + mBorderWidth/2 - mPadding;
                        mBottom = h - mBorderWidth/2 - mPadding;
                        break;
                    case END:
                    case START:
                    case CENTER:
                        //it's in center
                        mTop = (h - mSize + mBorderWidth) /2;
                        mBottom = (h + mSize - mBorderWidth) /2;
                        break;
                }
                break;

            //calculation of horizontal bounds
            case HORIZONTAL_DETERMINATE:
            case HORIZONTAL_INDETERMINATE:
                switch (mGravity){
                    case START:
                    case BOTTOM_START:
                    case TOP_START:
                        if(mIsRtl){
                            //it's at right
                            mLeft = w - mSize - mPadding;
                            mRight = w - mPadding;
                        }
                        else {
                            //it's at left
                            mLeft = mPadding;
                            mRight = mSize + mPadding;
                        }
                        break;
                    case END:
                    case BOTTOM_END:
                    case TOP_END:
                        if(mIsRtl){
                            //it's at left
                            mLeft = mPadding;
                            mRight = mSize + mPadding;
                        }
                        else {
                            //it's at right
                            mLeft = w - mSize - mPadding;
                            mRight = w - mPadding;
                        }
                        break;
                    case TOP:
                    case BOTTOM:
                    case CENTER:
                        //it's in center
                        mLeft = (w - mSize)/2;
                        mRight = (w + mSize)/2;
                        break;
                }
                switch (mGravity){
                    case TOP_START:
                    case TOP_END:
                    case TOP:
                        //it's on top
                        mTop = mPadding;
                        mBottom = mBorderWidth + mPadding;
                        break;
                    case BOTTOM:
                    case BOTTOM_START:
                    case BOTTOM_END:
                        //it's on bottom
                        mTop = h - mBorderWidth - mPadding;
                        mBottom = h - mPadding;
                        break;
                    case END:
                    case START:
                    case CENTER:
                        //it's in center
                        mTop = (h - mBorderWidth)/2;
                        mBottom = (h + mBorderWidth)/2;
                        break;
                }
                break;

            //if everything goes right, it should never come here. Just a precaution
            case NONE:
            default:
                mLeft = 0;
                mRight = 0;
                mTop = 0;
                mBottom = 0;
                break;
        }
    }

    /** Returns the left bound calculated. Be sure to call calculateBounds() before this! */
    public final float getLeft() {
        return mLeft;
    }

    /** Returns the top bound calculated. Be sure to call calculateBounds() before this! */
    public final float getTop() {
        return mTop;
    }

    /** Returns the right bound calculated. Be sure to call calculateBounds() before this! */
    public final float getRight() {
        return mRight;
    }

    /** Returns the bottom bound calculated. Be sure to call calculateBounds() before this! */
    public final float getBottom() {
        return mBottom;
    }














    /**
     * If the determinate drawer should update its progress with an animation
     *
     * @return true to use animation, false otherwise
     */
    public final boolean isDeterminateAnimationEnabled() {
        return mIsDeterminateAnimationEnabled;
    }

    /**
     * @return Width of the progress indicator
     */
    public final int getBorderWidth() {
        return mBorderWidth;
    }

    /**
     * @return Percentage value of the progress indicator of determinate drawers
     */
    public final float getValuePercent() {
        return mValuePercent;
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






    
    //Parcelable stuff

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

    @Override
    public int describeContents() {
        return 0;
    }

    protected ProgressOptions(Parcel in) {
        mIsDeterminateAnimationEnabled = in.readByte() != 0;
        mBorderWidth = in.readInt();
        mValuePercent = in.readFloat();
        mFrontColor = in.readInt();
        mBackColor = in.readInt();
        mIndeterminateColor = in.readInt();
        mDrawWedge = in.readByte() != 0;
        mSize = in.readInt();
        mPadding = in.readInt();
        mSizePercent = in.readFloat();
        mIsCircleBorderWidthFixed = in.readByte() != 0;
        mIsRtl = in.readByte() != 0;
        mLeft = in.readFloat();
        mTop = in.readFloat();
        mRight = in.readFloat();
        mBottom = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mIsDeterminateAnimationEnabled ? 1 : 0));
        dest.writeInt(mBorderWidth);
        dest.writeFloat(mValuePercent);
        dest.writeInt(mFrontColor);
        dest.writeInt(mBackColor);
        dest.writeInt(mIndeterminateColor);
        dest.writeByte((byte) (mDrawWedge ? 1 : 0));
        dest.writeInt(mSize);
        dest.writeInt(mPadding);
        dest.writeFloat(mSizePercent);
        dest.writeByte((byte) (mIsCircleBorderWidthFixed ? 1 : 0));
        dest.writeByte((byte) (mIsRtl ? 1 : 0));
        dest.writeFloat(mLeft);
        dest.writeFloat(mTop);
        dest.writeFloat(mRight);
        dest.writeFloat(mBottom);
    }
}
