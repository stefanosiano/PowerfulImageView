package com.stefanosiano.powerful_libraries.imageview.progress.drawers

import android.graphics.Canvas
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.stefanosiano.powerful_libraries.imageview.progress.PivProgressMode
import com.stefanosiano.powerful_libraries.imageview.progress.ProgressOptions
import java.lang.ref.WeakReference


/**
 * Manager class for progress drawers. Used to initialize use the needed drawers.
 */

internal class ProgressDrawerManager
/**
 * Manager class for progress drawers. Used to initialize and get the instances of the needed drawers.
 *
 * @param view View to show progress indicator into
 * @param progressOptions Options of the progress indicator
 */
(view: View, progressOptions: ProgressOptions) : ProgressOptions.ProgressOptionsListener {

    //Variables used to initialize drawers

    //Using a weakRefence to be sure to not leak memory
    private val mView = WeakReference(view)

    /** Bounds in which the progress indicator will be drawn  */
    private val mProgressBounds = RectF()

    /** Bounds in which the progress indicator shadow will be drawn  */
    private val mProgressShadowBorderBounds = RectF()

    /** Bounds in which the progress indicator shadow will be drawn  */
    private val mProgressShadowBounds = RectF()

    //Drawers
    private val mDummyProgressDrawer: DummyProgressDrawer by lazy { DummyProgressDrawer() }
    private val mCircularProgressDrawer: CircularProgressDrawer by lazy { CircularProgressDrawer() }
    private val mHorizontalProgressDrawer: HorizontalProgressDrawer by lazy { HorizontalProgressDrawer() }
    private val mHorizontalIndeterminateProgressDrawer: HorizontalIndeterminateProgressDrawer by lazy {
        HorizontalIndeterminateProgressDrawer()
    }
    private val mCircularIndeterminateProgressDrawer: CircularIndeterminateProgressDrawer by lazy {
        CircularIndeterminateProgressDrawer()
    }

    //Shadow Drawers
    private val mDummyShadowDrawer: DummyShadowDrawer by lazy { DummyShadowDrawer() }
    private val mRectangularShadowDrawer: RectangularShadowDrawer by lazy { RectangularShadowDrawer() }
    private val mCircularShadowDrawer: CircularShadowDrawer by lazy { CircularShadowDrawer() }

    /** Interface used to switch between its implementations, based on the progress mode and options selected.  */
    private var mShadowDrawer: ShadowDrawer = mDummyShadowDrawer

    /** Interface used to switch between its implementations, based on the progress mode selected.  */
    private var mProgressDrawer: ProgressDrawer = mDummyProgressDrawer

    /** Mode of the progress drawer  */
    private var mProgressMode = PivProgressMode.NONE

    /** Options used by progress drawers  */
    private var mProgressOptions = progressOptions

    /** Listener to handle things from drawers  */
    private val listener: ProgressDrawerListener

    init {
        this.listener = object : ProgressDrawerListener {
            override fun onRequestInvalidate() {
                // Invalidates only the area of the progress indicator, instead of the whole view. +1 e -1 are used to
                // be sure to invalidate the whole progress indicator. It is more efficient then just postInvalidate():
                // if something is drawn outside the bounds, it will not be calculated again!
                mView.get()?.postInvalidate(
                    mProgressBounds.left.toInt() - 1,
                    mProgressBounds.top.toInt() - 1,
                    mProgressBounds.right.toInt() + 1,
                    mProgressBounds.bottom.toInt() + 1)
            }
        }
        this.mProgressOptions.setListener(this)
    }


    /**
     * Updates the progress drawers to use and chooses the right one to use based on the mode.
     * If the drawer doesn't exist, it will be instantiated.
     * If the shadow drawer doesn't exist, it will be instantiated.
     *
     * @param progressMode Mode of the progress, used to choose the right drawers.
     */
    private fun updateDrawers(progressMode: PivProgressMode?) {

        when (progressMode ?: PivProgressMode.NONE) {

            PivProgressMode.CIRCULAR -> {
                mProgressDrawer = if (mProgressOptions.isIndeterminate)
                    mCircularIndeterminateProgressDrawer
                else mCircularProgressDrawer
                mShadowDrawer = if (mProgressOptions.shadowEnabled)
                    mCircularShadowDrawer
                else mDummyShadowDrawer
            }
            PivProgressMode.HORIZONTAL -> {
                mProgressDrawer = if (mProgressOptions.isIndeterminate)
                    mHorizontalIndeterminateProgressDrawer
                else mHorizontalProgressDrawer
                mShadowDrawer = if (mProgressOptions.shadowEnabled) mRectangularShadowDrawer else mDummyShadowDrawer
            }
            PivProgressMode.NONE -> {
                mProgressDrawer = mDummyProgressDrawer
                mShadowDrawer = mDummyShadowDrawer
            }
        }
        mProgressDrawer.setListener(listener)
    }


    /**
     * It calculates the bounds of the progress indicator.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     */
    fun onSizeChanged(w: Int, h: Int) {
        mProgressOptions.calculateBounds(w, h, mProgressMode)

        onSizeUpdated(mProgressOptions)
    }

    /** Signals the manager that the drawable changed */
    fun changeDrawable() {
        if (mProgressOptions.isRemovedOnChange)
            changeProgressMode(PivProgressMode.NONE, false)
    }

    /**
     * Changes the progress mode of the indicator (e.g. passing from determinate to indeterminate).
     * It also starts animation of indeterminate progress indicator.
     *
     * @param progressMode mode to change the progress indicator into
     * @param forceUpdate if the drawer should be updated, regardless of anything (may occur when changing indeterminate
     * flag)
     */
    fun changeProgressMode(progressMode: PivProgressMode?, forceUpdate: Boolean) {
        if (mProgressMode == progressMode ?: mProgressMode && !forceUpdate)
            return

        mProgressDrawer.stopIndeterminateAnimation()

        mProgressMode = progressMode ?: mProgressMode
        mProgressOptions.recalculateBounds(mProgressMode)
        updateDrawers(mProgressMode)
        mProgressDrawer.setup(mProgressOptions)
        mShadowDrawer.setup(mProgressOptions)
        mProgressDrawer.startIndeterminateAnimation()
    }


    /** Draws the progress indicator  */
    fun onDraw(canvas: Canvas) {
        mShadowDrawer.draw(canvas, mProgressShadowBorderBounds, mProgressShadowBounds)
        mProgressDrawer.draw(canvas, mProgressBounds)
    }

    /** @return The options of the progress indicator */
    fun getProgressOptions(): ProgressOptions = mProgressOptions

    /** @return The selected progress mode */
    fun getProgressMode(): PivProgressMode = mProgressMode

    interface ProgressDrawerListener {
        /** Request to invalidate the progress indicator bounds  */
        fun onRequestInvalidate()
    }


    /**
     * Called when an option is updated. It propagates the update to the progress drawers.
     */
    override fun onOptionsUpdated(options: ProgressOptions) {
        mShadowDrawer.setup(options)
        mProgressDrawer.setup(options)
        mProgressOptions = options
    }


    /**
     * Called when an option that changes the progress mode is updated: The drawer is calculated again.
     * This may occur when changing the indeterminate flag, or setting a progress value,
     */
    override fun onModeUpdated(options: ProgressOptions) {
        mProgressOptions = options
        changeProgressMode(mProgressMode, true)
    }

    /**
     * Called when an option that changes the size of the progress indicator is updated.
     * The bounds are calculated again, and it propagates the update to the progress drawers.
     */
    override fun onSizeUpdated(options: ProgressOptions) {

        mProgressOptions = options

        //set calculated bounds to our progress bounds
        mProgressBounds.set(mProgressOptions.rect)
        mProgressShadowBorderBounds.set(mProgressOptions.shadowBorderRect)
        mProgressShadowBounds.set(mProgressOptions.shadowRect)

        mProgressDrawer.setup(mProgressOptions)
        mShadowDrawer.setup(mProgressOptions)
    }


    /** Saves state into a bundle.  */
    fun saveInstanceState(): Bundle {
        val bundle = Bundle()
        bundle.putParcelable("progress_options", mProgressOptions)
        bundle.putInt("progress_mode", mProgressMode.value)
        return bundle
    }

    /** Restores state from a bundle.  */
    fun restoreInstanceState(state: Bundle?) {
        if (state == null)
            return

        mProgressOptions.setOptions(state.getParcelable<Parcelable>("progress_options") as ProgressOptions)
        val progressMode = PivProgressMode.fromValue(state.getInt("progress_mode"))
        onSizeUpdated(mProgressOptions)
        changeProgressMode(progressMode, false)
    }
}
