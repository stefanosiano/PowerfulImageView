package com.stefanosiano.powerful_libraries.imageview.progress.drawers

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.animation.LinearInterpolator
import com.stefanosiano.powerful_libraries.imageview.progress.ProgressOptions


/** Default animation duration  */
private const val DEFAULT_ANIMATION_DURATION: Long = 100


/** ProgressDrawer that shows a determinate bar as progress indicator */
internal class HorizontalProgressDrawer : ProgressDrawer {

    /** Left bound used to draw the rectangle  */
    private var mLeft: Float = 0.toFloat()

    /** Right bound used to draw the rectangle  */
    private var mRight: Float = 0.toFloat()

    /** Paint used to draw the front rectangle  */
    private var mProgressFrontPaint = Paint()

    /** Paint used to draw the back rectangle  */
    private var mProgressBackPaint = Paint()

    /** Animator that transforms the angles used to draw the progress  */
    private var mProgressAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

    /** Custom animation duration. If it's less then 0, default duration is used  */
    private var mProgressAnimationDuration: Long = -1

    /** Whether to animate the progress change or not  */
    private var mUseProgressAnimation: Boolean = false

    /** Real progress percentage of the front rectangle  */
    private var mProgress: Float = 0f

    /** Shown progress percentage of the front rectangle  */
    private var mCurrentProgress: Float = 0f

    /** Old progress percentage of the rectangle used to start animation from  */
    private var mOldProgress: Float = 0f

    /** Shown progress x coordinate of the front rectangle  */
    private var mCurrentFrontX: Float = 0f

    /** Whether to reverse the progress  */
    private var mIsProgressReversed: Boolean = false

    /** Listener to handle things from the drawer  */
    private var listener: ProgressDrawerManager.ProgressDrawerListener? = null

    init {
        mProgressAnimator.interpolator = LinearInterpolator()
        mProgressAnimator.duration = if (mProgressAnimationDuration < 0)
            DEFAULT_ANIMATION_DURATION
        else mProgressAnimationDuration
        //Using animation.getAnimatedFraction() because animation.getAnimatedValue() leaks memory
        mProgressAnimator.addUpdateListener {
            setRealProgress(getOldProgress() + (getProgress() - getOldProgress()) * it.animatedFraction)
        }
    }

    /**
     * Sets the progress that will be used to draw the rectangle
     * @param progress Progress used to calculate the front and back rectangles
     */
    private fun setRealProgress(progress: Float) {
        this.mCurrentProgress = progress
        this.mCurrentFrontX = mLeft + (mRight - mLeft) * (progress / 100)

        listener?.onRequestInvalidate()
    }


    override fun setProgressPercent(progressPercent: Float) {
        var mProgress = progressPercent
        if (mProgress > 100) mProgress %= 100

        //Saving last shown progress (will be used to animate, if needed)
        this.mOldProgress = mCurrentProgress

        //Sets the value of the progress (the value the animation will go to)
        this.mProgress = mProgress

        if (this.mUseProgressAnimation) {
            //use the mProgress to set the animation accordingly, after cancelling it.
            //The animation will go from mOldProgress to mProgress
            mProgressAnimator.cancel()
            mProgressAnimator.start()
        } else {
            //sets the mProgress as the real progress to show
            setRealProgress(mProgress)
        }
    }

    override fun setAnimationEnabled(enabled: Boolean) { this.mUseProgressAnimation = enabled }

    override fun setAnimationDuration(millis: Long) {
        mProgressAnimationDuration = if (millis < 0) DEFAULT_ANIMATION_DURATION else millis
        if(mProgressAnimator.duration != mProgressAnimationDuration) {
            mProgressAnimator.duration = mProgressAnimationDuration
            if (mProgressAnimator.isRunning) {
                mProgressAnimator.cancel()
                mProgressAnimator.start()
            }
        }
    }

    override fun setListener(listener: ProgressDrawerManager.ProgressDrawerListener) { this.listener = listener }

    override fun setup(progressOptions: ProgressOptions) {

        mProgressFrontPaint.color = progressOptions.frontColor
        mProgressFrontPaint.style = Paint.Style.FILL_AND_STROKE
        mProgressBackPaint.color = progressOptions.backColor
        mProgressBackPaint.style = Paint.Style.FILL_AND_STROKE

        mLeft = progressOptions.rect.left
        mRight = progressOptions.rect.right
        mUseProgressAnimation = progressOptions.determinateAnimationEnabled

        mIsProgressReversed = progressOptions.isProgressReversed

        setProgressPercent(progressOptions.valuePercent)

        mProgressAnimationDuration = if (progressOptions.animationDuration.toLong() < 0)
            DEFAULT_ANIMATION_DURATION
        else progressOptions.animationDuration.toLong()
        if(mProgressAnimator.duration != mProgressAnimationDuration) {
            mProgressAnimator.duration = mProgressAnimationDuration
            if (mProgressAnimator.isRunning) {
                mProgressAnimator.cancel()
                mProgressAnimator.start()
            }
        }
    }

    override fun startIndeterminateAnimation() { return }

    override fun draw(canvas: Canvas, progressBounds: RectF) {
        if (!mIsProgressReversed) {
            canvas.drawRect(
                mCurrentFrontX, progressBounds.top, progressBounds.right, progressBounds.bottom, mProgressBackPaint)
            canvas.drawRect(
                progressBounds.left, progressBounds.top, mCurrentFrontX, progressBounds.bottom, mProgressFrontPaint)
        } else {
            canvas.drawRect(
                progressBounds.left,
                progressBounds.top,
                progressBounds.right - mCurrentFrontX,
                progressBounds.bottom,
                mProgressBackPaint)
            canvas.drawRect(
                progressBounds.right - mCurrentFrontX,
                progressBounds.top,
                progressBounds.right,
                progressBounds.bottom,
                mProgressFrontPaint)
        }
    }

    override fun stopIndeterminateAnimation() { return }

    /** Returns the last progress shown (used as start in animation)  */
    private fun getOldProgress(): Float = mOldProgress

    /** Returns the right progress to show (used as goal in animation)  */
    private fun getProgress(): Float = mProgress
}
