package com.stefanosiano.powerfullibraries.imageview.blur.algorithms;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicConvolve3x3;

import com.stefanosiano.powerfullibraries.imageview.blur.BlurOptions;

import java.lang.ref.WeakReference;

/**
 * Class that performs the gaussian blur with 3x3 coefficient matrix using renderscript.
 * Changing radius will repeat the process radius times.
 */

final class Gaussian3x3RenderscriptBlurAlgorithm implements BlurAlgorithm {

    private final float[] coefficientMatrix = new float[] {
            0.0387f, 0.1194f, 0.0387f,
            0.1194f, 0.3676f, 0.1194f,
            0.0387f, 0.1194f, 0.0387f
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
            final ScriptIntrinsicConvolve3x3 script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
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
