package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;

import com.stefanosiano.powerfulimageview.ScriptC_stackblur;
import com.stefanosiano.powerfulimageview.blur.BlurOptions;

import java.lang.ref.WeakReference;

/**
 * by kikoso
 * from https://github.com/kikoso/android-stackblur/blob/master/StackBlur/src/blur.rs
 */
final class StackRenderscriptBlurAlgorithm implements BlurAlgorithm {

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

        int width = original.getWidth();
        int height = original.getHeight();

        ScriptC_stackblur blurScript = new ScriptC_stackblur(rs);
        Allocation inAllocation = Allocation.createFromBitmap(rs, original);

        blurScript.set_gIn(inAllocation);
        blurScript.set_width(width);
        blurScript.set_height(height);
        blurScript.set_radius(radius);

        int[] row_indices = new int[height];
        for (int i = 0; i < height; i++) {
            row_indices[i] = i;
        }

        Allocation rows = Allocation.createSized(rs, Element.U32(rs), height, Allocation.USAGE_SCRIPT);
        rows.copyFrom(row_indices);

        row_indices = new int[width];
        for (int i = 0; i < width; i++) {
            row_indices[i] = i;
        }

        Allocation columns = Allocation.createSized(rs, Element.U32(rs), width, Allocation.USAGE_SCRIPT);
        columns.copyFrom(row_indices);

        blurScript.forEach_blur_h(rows);
        blurScript.forEach_blur_v(columns);


        if(!options.isStaticBlur()) {
            Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
            inAllocation.copyTo(bitmap);
            inAllocation.destroy();
            rows.destroy();
            columns.destroy();
            return bitmap;
        }
        else {
            if (original.isMutable()) {
                inAllocation.copyTo(original);
                inAllocation.destroy();
                rows.destroy();
                columns.destroy();
                return original;
            }
            else {
                Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
                original.recycle();
                inAllocation.copyTo(bitmap);
                inAllocation.destroy();
                rows.destroy();
                columns.destroy();
                return bitmap;
            }
        }


    }
}