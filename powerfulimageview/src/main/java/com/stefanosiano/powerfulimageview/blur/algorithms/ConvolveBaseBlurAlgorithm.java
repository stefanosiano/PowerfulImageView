package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v8.renderscript.RenderScript;

import com.stefanosiano.powerfulimageview.blur.BlurOptions;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Base class that performs a convolution blur.
 * Should be overridden to provide the coefficients to use, so that it can perform the box or gaussian blu.
 * Changing radius will repeat the process radius times.
 */

abstract class ConvolveBaseBlurAlgorithm implements BlurAlgorithm {

    private int w, h;

    @Override
    public void setRenderscript(RenderScript renderscript) {

    }

    @Override
    public Bitmap blur(Bitmap original, int radius, BlurOptions options) throws RenderscriptException {

        w = original.getWidth();
        h = original.getHeight();
        int[] pix = new int[w * h];
        original.getPixels(pix, 0, w, 0, 0, w, h);

        int cores = Math.min(options.getNumThreads(), Runtime.getRuntime().availableProcessors());
        if(cores <= 0)
            cores = Runtime.getRuntime().availableProcessors();


        ArrayList<BlurTask> horizontal = new ArrayList<>(cores);
        ArrayList<BlurTask> vertical = new ArrayList<>(cores);
        for (int i = 0; i < cores; i++) {
            horizontal.add(new BlurTask(pix, w, h, radius, cores, i, 1));
            vertical.add(new BlurTask(pix, w, h, radius, cores, i, 2));
        }

        try {
            SharedBlurManager.getExecutorService().invokeAll(horizontal);

            SharedBlurManager.getExecutorService().invokeAll(vertical);
        } catch (InterruptedException e) {
            return null;
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


    /**
     * Returns a pixel from the provided vector.
     * Returns a pixel on the edge if the index passed is not valid.
     */
    // sample a repeated image. Returns a valid result for any x and y.
    // w is the image width, h the image height and pix the image itself.
    private int getPixel(int x, int y, int [] pix) {
        //if the pixel doen't exists i return a transparent pixel
        if(x < 0) x = 0;
        if(x >= w) x = w-1;
        if(y < 0) y = 0;
        if(y >= h) y = h-1;

        return pix[y*w+x];
    }


    /** Returns a 1D gaussian filter to perform the 2D blur */
    abstract float[] getFilter();


    private void apply(int[] srcPix, int w, int h, int radius, int cores, int core, int step) {

        //getting gaussian filter
        float[] gaussianFilter = getFilter();
        int filterLength = gaussianFilter.length;

        //auxiliary array used to store current pixels to blur. Moving this through all the image, i don't use additional memory!
        int [] tmpPix = new int[filterLength];


        // horizontal filtering
        if (step == 1) {

            //repeating radius times
            for(int iteration = 0; iteration < radius; iteration++) {

                int minY = core * h / cores;
                int maxY = (core + 1) * h / cores;
                int minIndex = minY * w;

                for (int y = minY; y < maxY; y++) {

                    int row = y * w;
                    int position = 0;

                    for (int x = 0; x < w; ++x) {

                        int sum;
                        int r = 0;
                        int g = 0;
                        int b = 0;
                        int a = 0;

                        //applying blur to the filterLength close pixels, weighting through the corresponding filter
                        for (int i = 0; i < filterLength; i++) {
                            int pixel = getPixel(x + i - filterLength / 2, y, srcPix);

                            r += Color.red(pixel) * gaussianFilter[i];
                            g += Color.green(pixel) * gaussianFilter[i];
                            b += Color.blue(pixel) * gaussianFilter[i];
                            a += Color.alpha(pixel) * gaussianFilter[i];
                        }

                        sum = Color.argb(a, r, g, b);

                        // store the pixel
                        position = row + x;
                        if (position >= minIndex + filterLength)
                            srcPix[position - filterLength] = tmpPix[position % filterLength];

                        tmpPix[position % filterLength] = sum;
                    }

                    //store remaining pixels to src vector
                    position++;
                    for (int i = 0; i < filterLength; i++)
                        srcPix[position - filterLength + i] = tmpPix[(position + i) % filterLength];

                }
            }
        }


        // vertical filtering
        if (step == 2) {

            //repeating radius times
            for(int iteration = 0; iteration < radius; iteration++) {

                int minX = core * w / cores;
                int maxX = (core + 1) * w / cores;

                for (int x = minX; x < maxX; x++) {

                    int row = x;
                    int position = 0;

                    for (int y = 0; y < h; y++) {

                        int sum;
                        int r = 0;
                        int g = 0;
                        int b = 0;
                        int a = 0;

                        //applying blur to the filterLength close pixels, weighting through the corresponding filter
                        for (int i = 0; i < filterLength; i++) {
                            int pixel = getPixel(x, y + i - filterLength / 2, srcPix);

                            r += Color.red(pixel) * gaussianFilter[i];
                            g += Color.green(pixel) * gaussianFilter[i];
                            b += Color.blue(pixel) * gaussianFilter[i];
                            a += Color.alpha(pixel) * gaussianFilter[i];
                        }

                        sum = Color.argb(a, r, g, b);

                        if (position >= filterLength)
                            srcPix[row - filterLength * w] = tmpPix[position % filterLength];

                        tmpPix[position % filterLength] = sum;

                        position++;
                        row += w;
                    }

                    //store remaining pixels to src vector
                    for (int i = 0; i < filterLength; i++)
                        srcPix[row - (filterLength - i) * w] = tmpPix[(position + i) % filterLength];

                }
            }
        }

    }




    private class BlurTask implements Callable<Void> {
        private final int[] _src;
        private final int _w;
        private final int _h;
        private final int _totalCores;
        private final int _radius;
        private final int _coreIndex;
        private final int _round;

        BlurTask(int[] src, int w, int h, int radius, int totalCores, int coreIndex, int round) {
            _src = src;
            _w = w;
            _h = h;
            _radius = radius;
            _totalCores = totalCores;
            _coreIndex = coreIndex;
            _round = round;
        }

        @Override public Void call() throws Exception {
            apply(_src, _w, _h, _radius, _totalCores, _coreIndex, _round);
            return null;
        }

    }
}
