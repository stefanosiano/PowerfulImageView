package com.stefanosiano.powerfulimageview_databinding;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.stefanosiano.powerfulimageview.PowerfulImageView;
import com.stefanosiano.powerfulimageview.progress.PivProgressGravity;
import com.stefanosiano.powerfulimageview.progress.PivProgressMode;

public class PowerfulDataBindingUtils {

    @BindingAdapter("piv_progress_gravity")
    public void setPivProgressGravity (PowerfulImageView piv, PivProgressGravity progressGravity) { piv.getProgressOptions().setGravity(progressGravity); }

    @BindingAdapter("piv_progress_mode")
    public void setPivProgressMode (PowerfulImageView piv, PivProgressMode progressMode) { piv.setProgressMode(progressMode); }

    @BindingAdapter("piv_progress_indeterminate")
    public void setProgressIndeterminate (PowerfulImageView piv, boolean progressIndeterminate) { piv.setProgressIndeterminate(progressIndeterminate); }

    @BindingAdapter("piv_progress_value")
    public void setProgressValue (PowerfulImageView piv, float progressValue) { piv.setProgressValue(progressValue); }

    @BindingAdapter("piv_progress_size")
    public void setProgressSize (PowerfulImageView piv, int progressSize) { piv.getProgressOptions().setSize(progressSize); }

    @BindingAdapter("piv_progress_size")
    public void setProgressSize (PowerfulImageView piv, float progressSize) { piv.getProgressOptions().setSize(progressSize); }

    @BindingAdapter("piv_progress_padding")
    public void set (PowerfulImageView piv, int progressPadding) { piv.getProgressOptions().setPadding(progressPadding); }

    @BindingAdapter("piv_progress_border_width")
    public void set (PowerfulImageView piv, int borderWidth) { piv.getProgressOptions().setBorderWidth(borderWidth); }

    @BindingAdapter("piv_progress_border_width")
    public void set (PowerfulImageView piv, float borderWidth) { piv.getProgressOptions().setBorderWidth(borderWidth); }

    @BindingAdapter("piv_progress_shadow_border_width")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_shadow_padding")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_shadow_enabled")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_determinate_animation_enabled")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_rtl_disabled")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_draw_wedge")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_reversed")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_removed_on_change")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_front_color")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_back_color")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_indeterminate_color")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_shadow_color")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_progress_shadow_border_color")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_mode")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_scaleType")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_inner_padding")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_border_width")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_ratio")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_radius_x")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_radius_y")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_border_overlay")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_solid_color")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_background_color")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_foreground_color")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_background")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_foreground")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_shape_border_color")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_blur_mode")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_blur_radius")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_blur_down_sampling_rate")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_blur_static")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_blur_use_rs_fallback")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }

    @BindingAdapter("piv_blur_num_threads")
    public void set (PowerfulImageView piv, ) { piv.getProgressOptions().set(); }


}
