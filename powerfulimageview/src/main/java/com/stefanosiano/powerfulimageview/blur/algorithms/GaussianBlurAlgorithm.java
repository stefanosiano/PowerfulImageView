package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;
import android.support.v8.renderscript.RenderScript;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;

/**
 * Created by stefano on 28/06/17.
 */

public class GaussianBlurAlgorithm implements BlurAlgorithm {
    @Override
    public void setRenderscript(RenderScript renderscript) {

    }

    @Override
    public Bitmap blur(Bitmap original, int radius, BlurOptions options) throws RenderscriptException {

        int[] filter = {1, 2, 1};
        if (filter.length % filterWidth != 0) {
            throw new IllegalArgumentException("filter contains a incomplete row");
        }

        final int w = original.getWidth();
        final int h = original.getHeight();
        int sum = 0;
        int filterWidth = w*h;

        for(int i = 0; i < filter.length; i++)
            sum += filter[i];

        int[] pix = new int[w * h];
        original.getPixels(pix, 0, w, 0, 0, w, h);

        int[] output = new int[pix.length];

        final int pixelIndexOffset = w - filterWidth;
        final int centerOffsetX = filterWidth / 2;
        final int centerOffsetY = filter.length / filterWidth / 2;

        // apply filter
        for (int i = h - filter.length / filterWidth + 1, j = w - filterWidth + 1, y = 0; y < i; y++) {
            for (int x = 0; x < w; x++) {
                int r = 0;
                int g = 0;
                int b = 0;
                for (int filterIndex = 0, pixelIndex = y * j + x;
                     filterIndex < filter.length;
                     pixelIndex += pixelIndexOffset) {
                    for (int fx = 0; fx < filterWidth; fx++, pixelIndex++, filterIndex++) {
                        int col = pix[pixelIndex];
                        int factor = filter[filterIndex];

                        // sum up color channels seperately
                        r += ((col >>> 16) & 0xFF) * factor;
                        g += ((col >>> 8) & 0xFF) * factor;
                        b += (col & 0xFF) * factor;
                    }
                }
                r /= sum;
                g /= sum;
                b /= sum;
                // combine channels with full opacity
                output[x + centerOffsetX + (y + centerOffsetY) * j] = (r << 16) | (g << 8) | b | 0xFF000000;
            }
        }

        //BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        //result.setRGB(0, 0, w, h, output, 0, w);
        return null;
    }
}
