package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;
import android.support.v8.renderscript.RenderScript;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;

/**
 * Algorithm taken by
 * http://stackoverflow.com/a/13436737/774398
 * by gordi
 */

final class GaussianFastBlurAlgorithm implements BlurAlgorithm {

    @Override
    public void setRenderscript(RenderScript renderscript) {

    }

    @Override
    public Bitmap blur(Bitmap original, int radius, BlurOptions options) {

            int w = original.getWidth();
            int h = original.getHeight();
            int[] pix = new int[w * h];
            original.getPixels(pix, 0, w, 0, 0, w, h);

            for(int r = radius; r >= 1; r /= 2) {
                for(int i = r; i < h - r; i++) {
                    int topRow = (i - r) * w;
                    int botRow = (i + r) * w;
                    int curRow = i * w;

                    for(int j = r; j < w - r; j++) {
                        int lefCol = j - r;
                        int rigCol = j + r;
                        int curCol = j;

                        int tl = pix[topRow + lefCol];
                        int tr = pix[topRow + rigCol];
                        int tc = pix[topRow + curCol];
                        int bl = pix[botRow + lefCol];
                        int br = pix[botRow + rigCol];
                        int bc = pix[botRow + curCol];
                        int cl = pix[curRow + lefCol];
                        int cr = pix[curRow + rigCol];

                        pix[curRow + curCol] = 0xFF000000 |
                                (((tl & 0xFF) + (tr & 0xFF) + (tc & 0xFF) + (bl & 0xFF) + (br & 0xFF) + (bc & 0xFF) + (cl & 0xFF) + (cr & 0xFF)) >> 3) & 0xFF |
                                (((tl & 0xFF00) + (tr & 0xFF00) + (tc & 0xFF00) + (bl & 0xFF00) + (br & 0xFF00) + (bc & 0xFF00) + (cl & 0xFF00) + (cr & 0xFF00)) >> 3) & 0xFF00 |
                                (((tl & 0xFF0000) + (tr & 0xFF0000) + (tc & 0xFF0000) + (bl & 0xFF0000) + (br & 0xFF0000) + (bc & 0xFF0000) + (cl & 0xFF0000) + (cr & 0xFF0000)) >> 3) & 0xFF0000;
                    }
                }
            }

            if(!options.isStaticBlur()) {
                return Bitmap.createBitmap(pix, 0, w, w, h, Bitmap.Config.ARGB_8888);
            }
            else {
                if (original.isMutable()) {
                    original.setPixels(pix, 0, w, 0, 0, w, h);
                    return original;
                }
                else {
                    original.recycle();
                    return Bitmap.createBitmap(pix, 0, w, w, h, Bitmap.Config.ARGB_8888);
                }
            }
        }

}
