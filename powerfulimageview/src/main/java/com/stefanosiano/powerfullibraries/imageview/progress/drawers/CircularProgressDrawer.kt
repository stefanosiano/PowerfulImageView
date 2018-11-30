package com.stefanosiano.powerfullibraries.imageview.progress.drawers

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.animation.LinearInterpolator
import com.stefanosiano.powerfullibraries.imageview.progress.ProgressOptions


/** Default animation duration  */
private const val DEFAULT_ANIMATION_DURATION: Long = 100


/** ProgressDrawer that shows a determinate circle as progress indicator */
internal class CircularProgressDrawer : ProgressDrawer {

    /** Paint used to draw the front arc  */
    private var mProgressFrontPaint: Paint = Paint()

    /** Paint used to draw the back arc  */
    private var mProgressBackPaint: Paint = Paint()

    /** Animator that transforms the angles used to draw the progress  */
    private var mProgressAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

    /** Custom animation duration. If it's less then 0, default duration is used  */
    private var mProgressAnimationDuration: Long = -1


    /** Start angle of the back arc  */
    private var mProgressBackStartAngle: Int = 0

    /** Sweep angle of the back arc  */
    private var mProgressBackSweepAngle: Int = 0

    /** Real sweep angle of the front arc  */
    private var mProgressFrontSweepAngle: Int = 0

    /** Shown sweep angle of the front arc  */
    private var mCurrentProgressFrontSweepAngle: Int = 0

    /** Old angle of the front arc used to start animation from  */
    private var mOldProgressFrontSweepAngle: Int = 0

    /** Whether to animate the progress change or not  */
    private var mUseProgressAnimation: Boolean = false

    /** Whether to draw wedges or simple arcs  */
    private var drawWedge: Boolean = false

    /** Whether to reverse the progress  */
    private var mIsProgressReversed: Boolean = false

    /** Listener to handle things from the drawer  */
    private var listener: ProgressDrawerManager.ProgressDrawerListener? = null

    init {
        mProgressAnimator.interpolator = LinearInterpolator()
        mProgressAnimator.duration = if (mProgressAnimationDuration < 0) com.stefanosiano.powerfullibraries.imageview.progress.drawers.DEFAULT_ANIMATION_DURATION else mProgressAnimationDuration
        //Using animation.getAnimatedFraction() because animation.getAnimatedValue() leaks memory
        mProgressAnimator.addUpdateListener { setRealProgressAngle((getOldSweepAngle() + (getSweepAngle() - getOldSweepAngle()) * it.animatedFraction).toInt()) }
    }

    /**
     * Sets the angle that will be used to draw the arcs
     * @param progressAngle Angle used to calculate the front and back arcs
     */
    private fun setRealProgressAngle(progressAngle: Int) {
        this.mProgressBackStartAngle = progressAngle - 90
        this.mProgressBackSweepAngle = 360 - progressAngle
        this.mCurrentProgressFrontSweepAngle = progressAngle

        listener?.onRequestInvalidate()
    }

    override fun setProgressPercent(progressPercent: Float) {
        var mProgressAngle = (progressPercent * 3.6f).toInt()
        if (mProgressAngle > 360) mProgressAngle %= 360

        //Saving last shown angle (will be used to animate, if needed)
        this.mOldProgressFrontSweepAngle = this.mCurrentProgressFrontSweepAngle

        //Sets the value of the progress angle (the value the animation will go to)
        this.mProgressFrontSweepAngle = mProgressAngle

        if (this.mUseProgressAnimation) {
            //use the mProgressFrontSweepAngle to set the animation accordingly, after cancelling it.
            //The animation will go from mOldProgressFrontSweepAngle to mProgressFrontSweepAngle
            mProgressAnimator.cancel()
            mProgressAnimator.start()
        } else {
            //sets the mProgressFrontSweepAngle as the real angle to show
            setRealProgressAngle(mProgressAngle)
        }
    }

    override fun setAnimationEnabled(enabled: Boolean) {
        this.mUseProgressAnimation = enabled
    }

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

    override fun setListener(listener: ProgressDrawerManager.ProgressDrawerListener) {
        this.listener = listener
    }

    override fun setup(progressOptions: ProgressOptions) {

        mUseProgressAnimation = progressOptions.determinateAnimationEnabled
        drawWedge = progressOptions.drawWedge

        mProgressFrontPaint.color = progressOptions.frontColor
        mProgressFrontPaint.strokeWidth = progressOptions.calculatedBorderWidth.toFloat()
        mProgressFrontPaint.isAntiAlias = true
        mProgressFrontPaint.style = if (drawWedge) Paint.Style.FILL_AND_STROKE else Paint.Style.STROKE

        mProgressBackPaint.color = progressOptions.backColor
        mProgressBackPaint.strokeWidth = progressOptions.calculatedBorderWidth.toFloat()
        mProgressBackPaint.isAntiAlias = true
        mProgressBackPaint.style = Paint.Style.STROKE

        mIsProgressReversed = progressOptions.isProgressReversed

        mProgressAnimationDuration = if (progressOptions.animationDuration.toLong() < 0) DEFAULT_ANIMATION_DURATION else progressOptions.animationDuration.toLong()
        if(mProgressAnimator.duration != mProgressAnimationDuration) {
            mProgressAnimator.duration = mProgressAnimationDuration
            if (mProgressAnimator.isRunning) {
                mProgressAnimator.cancel()
                mProgressAnimator.start()
            }
        }

        setProgressPercent(progressOptions.valuePercent)
    }

    override fun startIndeterminateAnimation() {}

    override fun draw(canvas: Canvas, progressBounds: RectF) {
        if (!mIsProgressReversed) {
            canvas.drawArc(progressBounds, mProgressBackStartAngle.toFloat(), mProgressBackSweepAngle.toFloat(), drawWedge, mProgressBackPaint)
            canvas.drawArc(progressBounds, -90f, mCurrentProgressFrontSweepAngle.toFloat(), drawWedge, mProgressFrontPaint)
        } else {
            canvas.drawArc(progressBounds, -90f, mProgressBackSweepAngle.toFloat(), drawWedge, mProgressBackPaint)
            canvas.drawArc(progressBounds, -90f, (-mCurrentProgressFrontSweepAngle).toFloat(), drawWedge, mProgressFrontPaint)
        }
    }

    override fun stopIndeterminateAnimation() {}

    /** Returns the last angle shown (used as start in animation)  */
    private fun getOldSweepAngle(): Int = mOldProgressFrontSweepAngle

    /** Returns the right angle to show (used as goal in animation)  */
    private fun getSweepAngle(): Int = mProgressFrontSweepAngle

}
