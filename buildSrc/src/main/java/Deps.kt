object Deps {
    private const val agpVersion = "7.0.4"
    private const val kotlinVersion = "1.4.30"
    private const val coroutinesVersion = "1.4.1"
    const val detektPluginVersion = "1.19.0"

    // Gradle plugins
    const val androidGradlePlugin = "com.android.tools.build:gradle:$agpVersion"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val createNonRsPlugin = "com.stefanosiano.powerful_libraries.imageview.plugins_non_rs"
    const val detektPlugin = "io.gitlab.arturbosch.detekt"
    const val androidLibrary = "com.android.library"
    const val androidApplication = "com.android.application"

    // Kotlin things
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    const val kotlinCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"

    // Android things
    const val appCompat = "androidx.appcompat:appcompat:1.2.0"

    // Other libraries
    const val detektKtlintDependency = "io.gitlab.arturbosch.detekt:detekt-formatting:$detektPluginVersion"
}