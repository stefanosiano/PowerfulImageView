package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.content.Context;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manager class for renderscript related stuff. Using this, there will be only one instance of
 * renderscript context at a time.
 */

final class RenderscriptManager {

    private static AtomicInteger count;
    private static Context applicationContext;
    private static RenderScript renderScript;

    synchronized static void addContext(Context context){
        if(count == null){
            count = new AtomicInteger(0);
        }
        count.addAndGet(1);
        if(applicationContext == null)
            applicationContext = context.getApplicationContext();
    }

    synchronized static void removeContext(){
        int c = count.decrementAndGet();
        if(c == 0) {
            applicationContext = null;
            if(renderScript != null)
                renderScript.destroy();
            renderScript = null;
        }
    }

    static RenderScript getRenderScript(){
        if(renderScript == null && applicationContext != null) {
            try {
                renderScript = RenderScript.create(applicationContext);
            } catch (Exception e){
                Log.e(BlurManager.class.getSimpleName(), e.getLocalizedMessage());
            }
        }

        return renderScript;
    }
}
