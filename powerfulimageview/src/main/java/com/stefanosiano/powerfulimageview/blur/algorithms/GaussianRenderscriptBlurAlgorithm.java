package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;


/**
 * Simple example of ScriptIntrinsicBlur Renderscript gaussion blur.
 * In production always use this algorithm as it is the fastest on Android.
 */
public class GaussianRenderscriptBlurAlgorithm implements BlurAlgorithm {
    private RenderScript rs;

    public GaussianRenderscriptBlurAlgorithm(RenderScript rs) {
        this.rs = rs;
    }

    @Override
    public Bitmap blur(Bitmap original, int radius, BlurOptions options) {
        final Allocation input = Allocation.createFromBitmap(rs, original);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);

        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
        if(options.isKeepOriginal()) {
            output.copyTo(bitmap);
            return bitmap;
        }
        else {
            if (original.isMutable()) {
                output.copyTo(original);
                return original;
            }
            else {
                original.recycle();
                output.copyTo(bitmap);
                return bitmap;
            }
        }

    }
}