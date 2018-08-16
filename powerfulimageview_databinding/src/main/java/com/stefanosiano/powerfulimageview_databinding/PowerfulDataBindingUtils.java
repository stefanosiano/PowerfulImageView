package com.stefanosiano.powerfulimageview_databinding;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class PowerfulDataBindingUtils {

    @BindingAdapter("dbuImgSrc")
    public void setImageViewSource(ImageView imageView, Drawable drawable) { imageView.setImageDrawable(drawable); }
}
