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

        int w = original.getWidth();
        int h = original.getHeight();
        int[] pix = new int[w * h];

        original.getPixels(pix, 0, w, 0, 0, w, h);
        apply(pix, pix, w, h);

        return Bitmap.createBitmap(pix, w, h, Bitmap.Config.ARGB_8888);
    }










    // extract a channel value from a RGB 'int' packed color
    static int getChannel(int color, int channel) {
        return  (color >> (8*channel)) & 0xFF;
    }

    // shift a color value of the corresponding channel offset
    static int channelShift(int color, int channel) {
        return (color&0xFF)<<(8*channel);
    }

    // sample a repeated image. Returns a valid result for any x and y.
    // w is the image width, h the image height and pix the image itself.
    static int getPixRepeat(int x, int y, int w, int h, int [] pix)
    {
        int x2 = (x+w) % w;
        int y2 = (y+h) % h;
        return pix[y2*w+x2];
    }

    // appy a 5x5 gaussian blur of sigma = 1.
    // Put the result into dstpix. Both images must have the same size,
    // defined by w and h (for width and height).
    static void apply(int[] srcpix, int[] dstpix, int w, int h) {

        int [] tmppix = new int[w*h + 1];

        // horizontal filtering
        for (int y=0; y<h; ++y) {

            int pos = y*w;

            for(int x=0; x<w; ++x) {

                // accumulate each channel for this pixel
                int r=0;
                for (int c=0; c<4; c++) {
                    // [1 4 6 4 1] filter
                    r += channelShift((
                            (getChannel(getPixRepeat(x-2,y,w,h,srcpix), c) +
                                    getChannel(getPixRepeat(x-1,y,w,h,srcpix), c)*4 +
                                    getChannel(getPixRepeat(x  ,y,w,h,srcpix), c)*6 +
                                    getChannel(getPixRepeat(x+1,y,w,h,srcpix), c)*4 +
                                    getChannel(getPixRepeat(x+2,y,w,h,srcpix), c)) / 16
                    ), c);
                }

                // store the pixel
                tmppix[pos + x] = r;
            }
        }

        // vertical filtering
        for (int x=0; x<w; ++x) {

            int pos = x;

            for (int y=0; y<h; y++) {
                int r=0;
                for (int c=0; c<4; c++) {
                    r += channelShift((
                            (getChannel(getPixRepeat(x,y-2,w,h,tmppix), c) +
                                    getChannel(getPixRepeat(x,y-1,w,h,tmppix), c)*4 +
                                    getChannel(getPixRepeat(x,y  ,w,h,tmppix), c)*6 +
                                    getChannel(getPixRepeat(x,y+1,w,h,tmppix), c)*4 +
                                    getChannel(getPixRepeat(x,y+2,w,h,tmppix), c)) / 16
                    ), c);
                }
                dstpix[pos] = r;//+(0xff << 24);
                pos += w;
            }
        }
    }
}
