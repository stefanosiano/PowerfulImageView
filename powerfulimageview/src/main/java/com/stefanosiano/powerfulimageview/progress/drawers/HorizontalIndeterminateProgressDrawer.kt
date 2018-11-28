package com.stefanosiano.powerfulimageview.progress.drawers

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.animation.AccelerateDecelerateInterpolator
import com.stefanosiano.powerfulimageview.progress.ProgressOptions


/** Default animation duration  */
private const val DEFAULT_ANIMATION_DURATION: Long = 1000


/** ProgressDrawer that shows an indeterminate animated bar as progress indicator */
class HorizontalIndeterminateProgressDrawer : ProgressDrawer {

    /** Left bound used to draw the rectangle  */
    private var mLeft: Float = 0f

    /** Right bound used to draw the rectangle  */
    private var mRight: Float = 0f

    /** Paint used to draw the rectangle  */
    private var mProgressPaint: Paint = Paint()

    /** Animator that transforms the x coordinates used to draw the progress  */
    private var mProgressAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

    /** Start x coordinate of the rectangle  */
    private var mStartX: Float = 0f

    /** End x coordinate of the rectangle  */
    private var mEndX: Float = 0f

    /** Whether the progress is shrinking or expanding. Used to adjust behaviour during animation  */
    private var isShrinking: Boolean = false

    /** Whether to reverse the progress  */
    private var mIsProgressReversed: Boolean = false

    /** Custom animation duration. If it's less then 0, default duration is used  */
    private var mProgressAnimationDuration: Long = -1

    /** Listener to handle things from the drawer  */
    private var listener: ProgressDrawerManager.ProgressDrawerListener? = null


    init {
        mProgressAnimator.duration = if (mProgressAnimationDuration < 0) DEFAULT_ANIMATION_DURATION else mProgressAnimationDuration
        mProgressAnimator.interpolator = AccelerateDecelerateInterpolator()
        mProgressAnimator.repeatCount = ValueAnimator.INFINITE
        mProgressAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) { isShrinking = !isShrinking }
        })
        //Using animation.getAnimatedFraction() because animation.getAnimatedValue() leaks memory
        mProgressAnimator.addUpdateListener { setProgressValues(mLeft + (mRight - mLeft) * it.animatedFraction) }
    }


    override fun setProgressPercent(progressPercent: Float) {}

    override fun setAnimationEnabled(enabled: Boolean) {}

    override fun setup(progressOptions: ProgressOptions) {

        mProgressPaint.color = progressOptions.indeterminateColor
        mProgressPaint.style = Paint.Style.FILL_AND_STROKE

        mLeft = progressOptions.rect.left
        mRight = progressOptions.rect.right
        setProgressValues(if (isShrinking) mStartX else mEndX)

        mIsProgressReversed = progressOptions.isProgressReversed

        mProgressAnimationDuration = progressOptions.animationDuration.toLong()
        mProgressAnimator.duration = if (mProgressAnimationDuration < 0) DEFAULT_ANIMATION_DURATION else mProgressAnimationDuration
        if (mProgressAnimator.isRunning) {
            mProgressAnimator.cancel()
            mProgressAnimator.start()
        }
    }


    /**
     * Sets the x coordinate of the rectangle that will be drawn
     * @param currentX Used when progress is shrinking (as start) or expanding (as end)
     */
    private fun setProgressValues(currentX: Float) {

        if (isShrinking) {
            this.mStartX = currentX
            this.mEndX = mRight
        } else {
            this.mStartX = mLeft
            this.mEndX = currentX
        }

        listener?.onRequestInvalidate()
    }

    override fun startIndeterminateAnimation() {
        this.mProgressAnimator.cancel()

        this.isShrinking = false
        setProgressValues(mLeft)

        mProgressAnimator.start()
    }

    override fun draw(canvas: Canvas, progressBounds: RectF) {
        if (!mIsProgressReversed)
            canvas.drawRect(mStartX, progressBounds.top, mEndX, progressBounds.bottom, mProgressPaint)
        else
            canvas.drawRect(progressBounds.right - mEndX, progressBounds.top, progressBounds.right + progressBounds.left - mStartX, progressBounds.bottom, mProgressPaint)
    }

    override fun stopIndeterminateAnimation() {
        mProgressAnimator.cancel()
    }

    override fun setAnimationDuration(millis: Long) {
        this.mProgressAnimationDuration = millis
        mProgressAnimator.duration = millis
        if (mProgressAnimator.isRunning) {
            mProgressAnimator.cancel()
            mProgressAnimator.start()
        }
    }

    override fun setListener(listener: ProgressDrawerManager.ProgressDrawerListener) { this.listener = listener }

}

