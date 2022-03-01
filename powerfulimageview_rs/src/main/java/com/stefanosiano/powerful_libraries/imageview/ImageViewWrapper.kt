package com.stefanosiano.powerful_libraries.imageview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat

/**
 * ImageView Wrapper that enables to catch all the methods where the image or a size changes and react accordingly.
 */

@Suppress("TooManyFunctions")
abstract class ImageViewWrapper : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /** Method called when the drawable has been changed, through a set..() method. */
    internal abstract fun onDrawableChanged()

    /* Methods to check! They are here just as a reminder of what i could use */
    override fun setImageMatrix(matrix: Matrix) = super.setImageMatrix(matrix)

    override fun setColorFilter(cf: ColorFilter) = super.setColorFilter(cf)

    override fun hasOverlappingRendering(): Boolean = false

    // These methods propagate their effects to the methods of the PIV

    // Just a remainder: it calls setImageDrawable, so there's no need to call onDrawableChanged()!
    override fun setImageResource(resId: Int) = super.setImageResource(resId)

    override fun setImageURI(uri: Uri?) { super.setImageURI(uri); onDrawableChanged() }

    override fun setImageDrawable(drawable: Drawable?) { super.setImageDrawable(drawable); onDrawableChanged() }

    // Just a remainder: it calls setImageDrawable, so there's no need to call onDrawableChanged()!
    override fun setImageIcon(icon: Icon?) = super.setImageIcon(icon)

    // Just a remainder: it calls setImageDrawable, so there's no need to call onDrawableChanged()!
    override fun setImageBitmap(bm: Bitmap) = super.setImageBitmap(bm)

    override fun setScaleType(scaleType: ScaleType) = super.setScaleType(scaleType)

    /** Returns selected color (default color if selected color is not available) for any api level. */
    protected fun getColor(a: TypedArray, colorId: Int, defaultColorId: Int): Int =
        a.getColor(colorId, ContextCompat.getColor(context, defaultColorId))
}
