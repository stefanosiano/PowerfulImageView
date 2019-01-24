package com.stefanosiano.powerful_libraries.imageview_databinding;

import android.graphics.drawable.Drawable;

import com.stefanosiano.powerful_libraries.imageview.PowerfulImageView;
import com.stefanosiano.powerful_libraries.imageview.blur.PivBlurMode;
import com.stefanosiano.powerful_libraries.imageview.progress.PivProgressGravity;
import com.stefanosiano.powerful_libraries.imageview.progress.PivProgressMode;
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeMode;
import com.stefanosiano.powerful_libraries.imageview.shape.PivShapeScaleType;

import androidx.databinding.BindingAdapter;

public class PowerfulDataBindingUtils {

    @BindingAdapter("app:piv_progress_gravity")
    public static void setPivProgressGravity (PowerfulImageView piv, PivProgressGravity progressGravity) { piv.getProgressOptions().setGravity(progressGravity); }

    @BindingAdapter("app:piv_progress_mode")
    public static void setPivProgressMode (PowerfulImageView piv, PivProgressMode progressMode) { piv.setProgressMode(progressMode); }

    @BindingAdapter("app:piv_progress_indeterminate")
    public static void setProgressIndeterminate (PowerfulImageView piv, boolean progressIndeterminate) { piv.setProgressIndeterminate(progressIndeterminate); }

    @BindingAdapter("app:piv_progress_animation_duration")
    public static void setProgressAnimationDuration (PowerfulImageView piv, int duration) { piv.getProgressOptions().setAnimationDuration(duration); }

    @BindingAdapter("app:piv_progress_value")
    public static void setProgressValue (PowerfulImageView piv, float progressValue) {
        if(piv.getProgress() == 100 && progressValue != 100 && piv.getProgressOptions().getDeterminateAnimationEnabled()){
            piv.getProgressOptions().setDeterminateAnimationEnabled(false);
            piv.setProgressValue(0);
            piv.getProgressOptions().setDeterminateAnimationEnabled(true);
        }
        piv.setProgressValue(progressValue);
    }

    @BindingAdapter("app:piv_progress_size")
    public static void setProgressSize (PowerfulImageView piv, int progressSize) { piv.getProgressOptions().setSize(progressSize); }

    @BindingAdapter("app:piv_progress_size")
    public static void setProgressSize (PowerfulImageView piv, float progressSize) { piv.getProgressOptions().setSize(progressSize); }

    @BindingAdapter("app:piv_progress_padding")
    public static void setProgressPadding (PowerfulImageView piv, int progressPadding) { piv.getProgressOptions().setPadding(progressPadding); }

    @BindingAdapter("app:piv_progress_border_width")
    public static void setProgressBorderWidth (PowerfulImageView piv, int progressBorderWidth) { piv.getProgressOptions().setBorderWidth(progressBorderWidth); }

    @BindingAdapter("app:piv_progress_border_width")
    public static void setProgressBorderWidth (PowerfulImageView piv, float progressBorderWidth) { piv.getProgressOptions().setBorderWidth(progressBorderWidth); }

    @BindingAdapter("app:piv_progress_shadow_border_width")
    public static void setProgressShadowBorderWidth (PowerfulImageView piv, float progressShadowBorderWidth) { piv.getProgressOptions().setShadowBorderWidth(progressShadowBorderWidth); }

    @BindingAdapter("app:piv_progress_shadow_padding")
    public static void setProgressShadowPadding (PowerfulImageView piv, int progressShadowPadding) { piv.getProgressOptions().setShadowPadding(progressShadowPadding); }

    @BindingAdapter("app:piv_progress_shadow_padding")
    public static void setProgressShadowPadding (PowerfulImageView piv, float progressShadowPadding) { piv.getProgressOptions().setShadowPadding(progressShadowPadding); }

    @BindingAdapter("app:piv_progress_shadow_enabled")
    public static void setProgressShadowEnabled (PowerfulImageView piv, boolean progressShadowEnabled) { piv.getProgressOptions().setShadowEnabled(progressShadowEnabled); }

    @BindingAdapter("app:piv_progress_determinate_animation_enabled")
    public static void setProgressDeterminateAnimationEnabled (PowerfulImageView piv, boolean progressDeterminateAnimationEnabled) { piv.getProgressOptions().setDeterminateAnimationEnabled(progressDeterminateAnimationEnabled); }

    @BindingAdapter("app:piv_progress_rtl_disabled")
    public static void setProgressRtlDisabled (PowerfulImageView piv, boolean progressRtlDisabled) { piv.getProgressOptions().setRtlDisabled(progressRtlDisabled); }

    @BindingAdapter("app:piv_progress_draw_wedge")
    public static void setProgressDrawWedge (PowerfulImageView piv, boolean progressDrawWedge) { piv.getProgressOptions().setDrawWedge(progressDrawWedge); }

    @BindingAdapter("app:piv_progress_reversed")
    public static void setProgressReversed (PowerfulImageView piv, boolean progressReversed) { piv.getProgressOptions().setProgressReversed(progressReversed); }

    @BindingAdapter("app:piv_progress_removed_on_change")
    public static void setProgressRemovedOnChange (PowerfulImageView piv, boolean progressRemovedOnChange) { piv.getProgressOptions().setRemovedOnChange(progressRemovedOnChange); }

    @BindingAdapter("app:piv_progress_front_color")
    public static void setProgressFrontColor (PowerfulImageView piv, int progressFrontColor) { piv.getProgressOptions().setFrontColor(progressFrontColor); }

    @BindingAdapter("app:piv_progress_back_color")
    public static void setProgressBackColor (PowerfulImageView piv, int progressBackColor) { piv.getProgressOptions().setBackColor(progressBackColor); }

    @BindingAdapter("app:piv_progress_indeterminate_color")
    public static void setProgressIndeterminateColor (PowerfulImageView piv, int progressIndeterminateColor) { piv.getProgressOptions().setIndeterminateColor(progressIndeterminateColor); }

    @BindingAdapter("app:piv_progress_shadow_color")
    public static void setProgressShadowColor (PowerfulImageView piv, int progressShadowColor) { piv.getProgressOptions().setShadowColor(progressShadowColor); }

    @BindingAdapter("app:piv_progress_shadow_border_color")
    public static void setProgressShadowBorderColor (PowerfulImageView piv, int progressShadowBorderColor) { piv.getProgressOptions().setShadowBorderColor(progressShadowBorderColor); }



    @BindingAdapter("app:piv_shape_mode")
    public static void setShapeMode (PowerfulImageView piv, PivShapeMode shapeMode) { piv.setShapeMode(shapeMode); }

    @BindingAdapter("app:piv_shape_scaleType")
    public static void setShapeScaleType (PowerfulImageView piv, PivShapeScaleType shapeScaleType) { piv.setShapeScaleType(shapeScaleType); }

    @BindingAdapter("app:piv_shape_inner_padding")
    public static void setShapeInnerPadding (PowerfulImageView piv, int shapeInnerPadding) { piv.getShapeOptions().setInnerPadding(shapeInnerPadding); }

    @BindingAdapter("app:piv_shape_inner_padding")
    public static void setShapeInnerPadding (PowerfulImageView piv, float shapeInnerPadding) { piv.getShapeOptions().setInnerPadding(shapeInnerPadding); }

    @BindingAdapter("app:piv_shape_border_width")
    public static void setShapeBorderWidth (PowerfulImageView piv, int shapeBorderWidth) { piv.getShapeOptions().setBorderWidth(shapeBorderWidth); }

    @BindingAdapter("app:piv_shape_ratio")
    public static void setShapeRatio (PowerfulImageView piv, float shapeRatio) { piv.getShapeOptions().setRatio(shapeRatio); }

    @BindingAdapter("app:piv_shape_radius_x")
    public static void setShapeRadiusX (PowerfulImageView piv, float shapeRadiusX) { piv.getShapeOptions().setRadiusX(shapeRadiusX); }

    @BindingAdapter("app:piv_shape_radius_y")
    public static void setShapeRadiusY (PowerfulImageView piv, float shapeRadiusY) { piv.getShapeOptions().setRadiusY(shapeRadiusY); }

    @BindingAdapter("app:piv_shape_border_overlay")
    public static void setShapeBorderOverlay (PowerfulImageView piv, boolean shapeBorderOverlay) { piv.getShapeOptions().setBorderOverlay(shapeBorderOverlay); }

    @BindingAdapter("app:piv_shape_solid_color")
    public static void setShapeSolidColor (PowerfulImageView piv, int shapeSolidColor) { piv.getShapeOptions().setSolidColor(shapeSolidColor); }

    @BindingAdapter("app:piv_shape_background_color")
    public static void setShapeBackgroundColor (PowerfulImageView piv, int shapeBackgroundColor) { piv.getShapeOptions().setBackgroundColor(shapeBackgroundColor); }

    @BindingAdapter("app:piv_shape_foreground_color")
    public static void setShapeForegroundColor (PowerfulImageView piv, int shapeForegroundColor) { piv.getShapeOptions().setForegroundColor(shapeForegroundColor); }

    @BindingAdapter("app:piv_shape_background")
    public static void setShapeBackground (PowerfulImageView piv, Drawable shapeBackground) { piv.getShapeOptions().setBackgroundDrawable(shapeBackground); }

    @BindingAdapter("app:piv_shape_foreground")
    public static void setShapeForeground (PowerfulImageView piv, Drawable shapeForeground) { piv.getShapeOptions().setForegroundDrawable(shapeForeground); }

    @BindingAdapter("app:piv_shape_border_color")
    public static void setShapeBorderColor (PowerfulImageView piv, int shapeBorderColor) { piv.getShapeOptions().setBorderColor(shapeBorderColor); }



    @BindingAdapter("app:piv_blur_mode")
    public static void setPivBlurMode (PowerfulImageView piv, PivBlurMode blurMode) { piv.setBlurMode(blurMode, piv.getBlurRadius()); }

    @BindingAdapter("app:piv_blur_radius")
    public static void setBlurRadius (PowerfulImageView piv, int blurRadius) { piv.setBlurRadius(blurRadius); }

    @BindingAdapter("app:piv_blur_down_sampling_rate")
    public static void setBlurDownSamplingRate (PowerfulImageView piv, int blurDownSamplingRate) { piv.getBlurOptions().setDownSamplingRate(blurDownSamplingRate); }

    @BindingAdapter("app:piv_blur_static")
    public static void setBlurStatic (PowerfulImageView piv, boolean blurStatic) { piv.getBlurOptions().setStaticBlur(blurStatic); }

    @BindingAdapter("app:piv_blur_use_rs_fallback")
    public static void setBlurUseRsFallback (PowerfulImageView piv, boolean blurUseRsFallback) { piv.getBlurOptions().setUseRsFallback(blurUseRsFallback); }

    @BindingAdapter("app:piv_blur_num_threads")
    public static void setBlurNumThreads (PowerfulImageView piv, int blurNumThreads) { piv.getBlurOptions().setNumThreads(blurNumThreads); }


}
