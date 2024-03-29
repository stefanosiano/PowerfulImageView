package com.stefanosiano.powerful_libraries.imageview.progress.drawers

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.stefanosiano.powerful_libraries.imageview.progress.ProgressOptions

/** Default animation duration. */
private const val DEFAULT_ANIMATION_DURATION: Long = 800

@Suppress("MagicNumber")
/** ProgressDrawer that shows an indeterminate animated circle as progress indicator. */
internal class CircularIndeterminateProgressDrawer : ProgressDrawer {

    /** Paint used to draw the arcs. */
    private var mProgressPaint: Paint = Paint()

    /** Animator that rotates the whole circle. */
    private var mOffsetAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

    /** Animator that transforms the angles used to draw the progress. */
    private var mProgressAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)

    /** Start angle of the arc. */
    private var mProgressStartAngle: Int = 0

    /** Sweep angle of the arc. */
    private var mProgressSweepAngle: Int = 0

    /** Whether the progress is shrinking or expanding. Used to adjust behaviour during animation. */
    private var isShrinking: Boolean = false

    /** Custom animation duration. If it's less then 0, default duration is used. */
    private var mProgressAnimationDuration: Long = -1

    /** offset of the start angle. It will change linearly continuously. */
    private var mOffset: Int = 0

    /** Last start angle offset. Used when calling setup(), so it doesn't change angle. */
    private var mLastStartAngleOffset: Int = 0

    /** Last sweep angle offset. Used when calling setup(), so it doesn't change angle. */
    private var mLastSweepAngleOffset: Int = 0

    /** Whether to reverse the progress. */
    private var mIsProgressReversed: Boolean = false

    /** Listener to handle things from the drawer. */
    private var listener: ProgressDrawerManager.ProgressDrawerListener? = null

    init {
        this.mProgressStartAngle = -90
        this.mProgressSweepAngle = 180
        this.mLastStartAngleOffset = 0
        this.mLastSweepAngleOffset = 0

        mOffsetAnimator.duration = 3000
        mOffsetAnimator.interpolator = LinearInterpolator()
        mOffsetAnimator.repeatCount = ValueAnimator.INFINITE

        // Using animation.getAnimatedFraction() because animation.getAnimatedValue() leaks memory
        mOffsetAnimator.addUpdateListener { mOffset = (360 * it.animatedFraction).toInt() }

        mProgressAnimator.duration =
            if (mProgressAnimationDuration < 0) DEFAULT_ANIMATION_DURATION else mProgressAnimationDuration
        mProgressAnimator.interpolator = AccelerateDecelerateInterpolator()
        mProgressAnimator.repeatCount = ValueAnimator.INFINITE
        mProgressAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) { isShrinking = !isShrinking }
        })
        // Using animation.getAnimatedFraction() because animation.getAnimatedValue() leaks memory
        mProgressAnimator.addUpdateListener {
            setProgressAngle((360 * it.animatedFraction).toInt(), (290 * it.animatedFraction).toInt())
        }
    }

    override fun setup(progressOptions: ProgressOptions) {
        mProgressPaint.color = progressOptions.indeterminateColor
        mProgressPaint.strokeWidth = progressOptions.calculatedBorderWidth.toFloat()
        mProgressPaint.isAntiAlias = true
        mProgressPaint.style = Paint.Style.STROKE

        mProgressAnimationDuration = if (progressOptions.animationDuration.toLong() < 0) {
            DEFAULT_ANIMATION_DURATION
        } else progressOptions.animationDuration.toLong()
        if (mProgressAnimator.duration != mProgressAnimationDuration) {
            mProgressAnimator.duration = mProgressAnimationDuration
            if (mProgressAnimator.isRunning) {
                mProgressAnimator.cancel()
                mProgressAnimator.start()
            }
        }
        mIsProgressReversed = progressOptions.isProgressReversed

        setProgressAngle(mLastStartAngleOffset, mLastSweepAngleOffset)
    }

    override fun startIndeterminateAnimation() {
        mOffsetAnimator.cancel()
        mProgressAnimator.cancel()

        this.mOffset = 0
        this.isShrinking = false

        this.mLastStartAngleOffset = 0
        this.mLastSweepAngleOffset = 0
        setProgressAngle(mLastStartAngleOffset, mLastSweepAngleOffset)

        mProgressAnimator.start()
        mOffsetAnimator.start()
    }

    /**
     * Set the [startAngleOffset], used when progress is shrinking (summing), and [sweepAngleOffset], used when
     * progress is shrinking (subtracting) or expanding (summing), of the angles of the arcs that will be drawn.
     */
    private fun setProgressAngle(startAngleOffset: Int, sweepAngleOffset: Int) {
        mLastStartAngleOffset = startAngleOffset
        mLastSweepAngleOffset = sweepAngleOffset
        // mProgressSweepAngle when isShrinking and at the end of animation must be equal to itself when !isShrinking
        // and at the beginning of the animation
        if (isShrinking) {
            // When sweepAngleOffset = 1 * 290 => 50. when sweepAngleOffset = 0 * 290 => 340
            this.mProgressStartAngle = -90 + startAngleOffset + mOffset
            this.mProgressSweepAngle = 340 - sweepAngleOffset
        } else {
            // When sweepAngleOffset = 0 * 290 => 50. when sweepAngleOffset = 1 * 290 => 340
            this.mProgressStartAngle = -90 + mOffset
            this.mProgressSweepAngle = sweepAngleOffset + 50
        }

        listener?.onRequestInvalidate()
    }

    override fun draw(canvas: Canvas, progressBounds: RectF) {
        if (!mIsProgressReversed) {
            canvas.drawArc(
                progressBounds,
                mProgressStartAngle.toFloat(),
                mProgressSweepAngle.toFloat(),
                false,
                mProgressPaint
            )
        } else {
            canvas.drawArc(
                progressBounds,
                (-mProgressStartAngle).toFloat(),
                (-mProgressSweepAngle).toFloat(),
                false,
                mProgressPaint
            )
        }
    }

    override fun stopIndeterminateAnimation() {
        mOffsetAnimator.cancel()
        mProgressAnimator.cancel()
    }

    override fun setProgressPercent(progressPercent: Float) {}

    override fun setAnimationEnabled(enabled: Boolean) {}

    override fun setAnimationDuration(millis: Long) {
        mProgressAnimationDuration = if (millis < 0) DEFAULT_ANIMATION_DURATION else millis
        if (mProgressAnimator.duration != mProgressAnimationDuration) {
            mProgressAnimator.duration = mProgressAnimationDuration
            if (mProgressAnimator.isRunning) {
                mProgressAnimator.cancel()
                mProgressAnimator.start()
            }
        }
    }

    override fun setListener(listener: ProgressDrawerManager.ProgressDrawerListener) { this.listener = listener }
}
