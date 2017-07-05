package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.content.Context;
import android.renderscript.RenderScript;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manager class for content shared through all instances of BlurManager
 * Renderscript related stuff
 * ThreadPool for java blurring methods
 */

final class SharedBlurManager {

    private static AtomicInteger count;
    private static Context applicationContext;
    private static RenderScript renderScript;

    private static ExecutorService executorService;

    synchronized static void addRenderscriptContext(Context context){
        if(count == null){
            count = new AtomicInteger(0);
        }
        count.addAndGet(1);
        if(applicationContext == null)
            applicationContext = context.getApplicationContext();
    }

    synchronized static void removeRenderscriptContext(){
        int c = count.decrementAndGet();
        if(c == 0) {
            applicationContext = null;
            if(renderScript != null)
                renderScript.destroy();
            renderScript = null;
        }
    }

    synchronized static RenderScript getRenderScriptContext(){
        if(renderScript == null && applicationContext != null) {
            try {
                renderScript = RenderScript.create(applicationContext);
            } catch (Exception e){
                Log.e(BlurManager.class.getSimpleName(), e.getLocalizedMessage());
            }
        }

        return renderScript;
    }

    synchronized static ExecutorService getExecutorService(){
        if(executorService == null || executorService.isShutdown())
            executorService = Executors.newCachedThreadPool();

        return executorService;
    }
}
