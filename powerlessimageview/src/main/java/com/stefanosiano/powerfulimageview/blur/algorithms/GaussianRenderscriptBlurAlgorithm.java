package com.stefanosiano.powerfullibraries.imageview.blur.algorithms;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.stefanosiano.powerfullibraries.imageview.blur.BlurOptions;

import java.lang.ref.WeakReference;


/**
 * Class that performs the gaussian blur with any kind of radius using renderscript.
 * Increasing radius will change the coefficients used and increase the radius of the blur,
 * resulting in the image more blurry, but slower.
 */
final class GaussianRenderscriptBlurAlgorithm implements BlurAlgorithm {
    private WeakReference<RenderScript> renderscript;

    @Override
    public void setRenderscript(RenderScript renderscript) {
        this.renderscript = new WeakReference<>(renderscript);
    }

    @Override
    public Bitmap blur(Bitmap original, int radius, BlurOptions options) throws RenderscriptException {
        RenderScript rs = renderscript.get();
        if(rs == null)
            throw new RenderscriptException("Renderscript is null!");

        Allocation input, output;
        ScriptIntrinsicBlur script;

        try {
            input = Allocation.createFromBitmap(rs, original);
            output = Allocation.createTyped(rs, input.getType());
            script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
        }   catch (Exception e){
            throw new RenderscriptException("Renderscript error while blurring!");
        }

        if(!options.isStaticBlur()) {
            Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
            output.copyTo(bitmap);
            input.destroy();
            output.destroy();
            return bitmap;
        }
        else {
            if (original.isMutable()) {
                output.copyTo(original);
                input.destroy();
                output.destroy();
                return original;
            }
            else {
                Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
                original.recycle();
                output.copyTo(bitmap);
                input.destroy();
                output.destroy();
                return bitmap;
            }
        }

    }
}