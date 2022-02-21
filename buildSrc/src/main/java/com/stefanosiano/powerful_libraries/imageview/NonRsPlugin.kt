package com.stefanosiano.powerful_libraries.imageview

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

class NonRsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register("createNonRs", CreateNonRsTask::class.java)
    }
}

open class CreateNonRsTask : DefaultTask() {

    @TaskAction
    fun execute() {
        println("Recreating non renderscript module")
        val rootDir = project.rootDir.absolutePath
        val algDir = "$rootDir/powerfulimageview/src/main/java/com/stefanosiano/powerful_libraries/imageview/blur/algorithms"
        val pivDir = File("$rootDir/powerfulimageview")
        pivDir.deleteRecursively()
        val pivRsDir = File("$rootDir/powerfulimageview_rs")
        pivRsDir.copyRecursively(pivDir, true)

        replaceInFile(File(pivDir, "build.gradle.kts")) { content -> content
            .replace("imageview_rs", "imageview")
            .replace("renderscriptSupportModeEnabled = true", "")
        }
        replaceInFile(File(algDir, "BoxAlgorithms.kt")) { content -> content
            .replace("import androidx.renderscript.*".toRegex(), "")
            .replaceAfterIncluding("//RENDERSCRIPT", "")
        }
        replaceInFile(File(algDir, "GaussianAlgorithms.kt")) { content -> content
            .replace("import androidx.renderscript.*".toRegex(), "")
            .replaceAfterIncluding("//RENDERSCRIPT", "")
        }
        replaceInFile(File(algDir, "BaseConvolveRenderscriptBlurAlgorithm.kt")) { content -> content
            .replace("import androidx.renderscript.*".toRegex(), "")
            .replaceAfter("BaseConvolveRenderscriptBlurAlgorithm : BlurAlgorithm {", "}")
        }
        replaceInFile(File(algDir, "BlurAlgorithm.kt")) { content -> content
            .replace("import androidx.renderscript.*".toRegex(), "")
            .replace("renderscript: RenderScript?", "renderscript: Any?")
        }
        replaceInFile(File(algDir, "BlurManager.kt")) { content -> content
            .replace("RenderscriptBlurAlgorithm()", "BlurAlgorithm()")
        }
        replaceInFile(File(algDir, "SharedBlurManager.kt")) { content -> content
            .replace("import androidx.renderscript.*\n".toRegex(), "")
            .replace("import com.stefanosiano.powerful_libraries.imageview.tryOrPrint.*\n".toRegex(), "")
            .replace("c.renderScript?.destroy()", "")
            .replace(": RenderScript?", ": Any?")
            .replace("tryOrPrint { RenderScript.create(applicationContext) }", "null")
        }
        File(algDir, "BaseConvolveRenderscriptBlurAlgorithm.kt").delete()
    }

    private fun replaceInFile(f: File, replace: (content: String) -> String) {
        val content = f.readText()
        f.writeText(replace(content))
    }

    /**
     * Replace part of string after the first occurrence of given delimiter with the [replacement] string.
     * If the string does not contain the delimiter, returns [missingDelimiterValue] which defaults to the original string.
     */
    private fun String.replaceAfterIncluding(delimiter: String, replacement: String, missingDelimiterValue: String = this): String {
        val index = indexOf(delimiter)
        return if (index == -1) missingDelimiterValue else replaceRange(index, length, replacement)
    }
}