package com.stefanosiano.powerful_libraries.imageview.blur.algorithms

import android.content.Context
import androidx.renderscript.RenderScript
import com.stefanosiano.powerful_libraries.imageview.extensions.tryOrPrint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * Manager class for content shared through all instances of BlurManager, including renderscript related stuff and a
 * ThreadPool for java blurring methods.
 */
internal object SharedBlurManager {

    private var count = AtomicInteger(0)

    val executorService: ExecutorService = Executors.newCachedThreadPool()

    private val c = C()

    fun getRenderScriptContext() = if (c.applicationContext != null) c.renderScript ?: c.buildRenderscript() else null

    @Synchronized fun addRenderscriptContext(context: Context) {
        count.addAndGet(1)
        if (c.applicationContext == null) c.applicationContext = context.applicationContext
    }

    @Synchronized fun removeRenderscriptContext() {
        val count = count.decrementAndGet()
        if (count == 0) {
            c.applicationContext = null
            c.renderScript?.destroy()
            c.renderScript = null
        }
    }
}

internal class C {
    internal var renderScript: RenderScript? = null
    internal var applicationContext: Context? = null

    fun buildRenderscript(): RenderScript? {
        applicationContext ?: return null
        renderScript = tryOrPrint { RenderScript.create(applicationContext) }
        return renderScript
    }
}
