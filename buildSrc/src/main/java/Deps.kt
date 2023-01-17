object Deps {

    // Sdk versions
    const val sdkCompile = 33
    const val sdkTarget = sdkCompile
    const val sdkMin = 14

    private const val agpVersion = "7.3.1"
    private const val kotlinVersion = "1.7.10"
    private const val coroutinesVersion = "1.6.4"
    private const val espressoVersion = "3.5.0"
    const val detektPluginVersion = "1.19.0"

    // Gradle plugins
    const val androidGradlePlugin = "com.android.tools.build:gradle:$agpVersion"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val createNonRsPlugin = "com.stefanosiano.powerful_libraries.imageview.plugins_non_rs"
    const val detektPlugin = "io.gitlab.arturbosch.detekt"
    const val androidLibrary = "com.android.library"
    const val androidApplication = "com.android.application"

    // Kotlin
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
    const val kotlinCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"

    // Android
    const val appCompat = "androidx.appcompat:appcompat:1.3.0"

    // Tests
    const val androidJUnitRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val androidxTestOrchestrator = "androidx.test:orchestrator:1.4.2"
    const val espressoCore = "androidx.test.espresso:espresso-core:$espressoVersion"
    const val espressoIdlingResource = "androidx.test.espresso:espresso-idling-resource:$espressoVersion"

    // Other libraries
    const val detektKtlintDependency = "io.gitlab.arturbosch.detekt:detekt-formatting:$detektPluginVersion"

    // Test libraries
    private const val androidxTestVersion = "1.5.0"
    const val kotlinTestJunit = "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
    const val robolectric = "org.robolectric:robolectric:4.7.3"
    const val androidxCore = "androidx.test:core:$androidxTestVersion"
    const val androidxRunner = "androidx.test:runner:$androidxTestVersion"
    const val androidxTestCoreKtx = "androidx.test:core-ktx:$androidxTestVersion"
    const val androidxTestRules = "androidx.test:rules:$androidxTestVersion"
    const val androidxJunit = "androidx.test.ext:junit:1.1.3"
    const val androidxCoreKtx = "androidx.core:core-ktx:1.7.0"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
    const val mockk = "io.mockk:mockk:1.12.5"
}