package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;

import java.lang.ref.WeakReference;

/**
 * Created by stefano on 7/2/17.
 */

public class Gaussian5x5RenderscriptBlurAlgorithm implements BlurAlgorithm {

    private float[] coefficientMatrix = new float[] {
            0.0030f,    0.0133f,    0.0219f,    0.0133f,    0.0030f,
            0.0133f,    0.0596f,    0.0983f,    0.0596f,    0.0133f,
            0.0219f,    0.0983f,    0.1621f,    0.0983f,    0.0219f,
            0.0133f,    0.0596f,    0.0983f,    0.0596f,    0.0133f,
            0.0030f,    0.0133f,    0.0219f,    0.0133f,    0.0030f
    };

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
        try{
            input = Allocation.createFromBitmap(rs, original);
            output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicConvolve5x5 script = ScriptIntrinsicConvolve5x5.create(rs, Element.U8_4(rs));
            script.setCoefficients(coefficientMatrix);
            for (int i = 0; i < radius; i++) {
                script.setInput(input);
                script.forEach(output);
                if(input != output)
                    input.destroy();
                input = output;
            }

        }   catch (Exception e){
            e.printStackTrace();
            throw new RenderscriptException("Renderscript error while blurring! \n" + e.getLocalizedMessage());
        }



        if(!options.isStaticBlur()) {
            Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
            output.copyTo(bitmap);
            output.destroy();
            return bitmap;
        }
        else {
            if (original.isMutable()) {
                output.copyTo(original);
                output.destroy();
                return original;
            }
            else {
                Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
                original.recycle();
                output.copyTo(bitmap);
                output.destroy();
                return bitmap;
            }
        }
    }
}
