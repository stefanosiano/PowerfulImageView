package com.stefanosiano.powerful_libraries.imageviewsample

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import kotlin.test.BeforeTest

abstract class BaseUiTest {

    /** Runner of the test. */
    protected lateinit var runner: AndroidJUnitRunner

    /** Application context for the current test. */
    protected lateinit var context: Context

    @BeforeTest
    fun baseSetUp() {
        runner = InstrumentationRegistry.getInstrumentation() as AndroidJUnitRunner
        context = ApplicationProvider.getApplicationContext()
        context.cacheDir.deleteRecursively()
    }
}
