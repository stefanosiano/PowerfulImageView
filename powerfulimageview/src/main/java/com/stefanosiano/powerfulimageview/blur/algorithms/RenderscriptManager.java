package com.stefanosiano.powerfulimageview.blur.algorithms;

import android.content.Context;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by stefano on 13/06/17.
 */

public class RenderscriptManager {

    private static AtomicInteger count;
    private static Context applicationContext;
    private static RenderScript renderScript;

    public synchronized static void addContext(Context context){
        if(count == null){
            count = new AtomicInteger(0);
        }
        count.addAndGet(1);
        if(applicationContext == null)
            applicationContext = context.getApplicationContext();
    }

    public synchronized static void removeContext(){
        int c = count.decrementAndGet();
        if(c == 0) {
            applicationContext = null;
            renderScript.destroy();
            renderScript = null;
        }
    }

    public static RenderScript getRenderScript(){
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
