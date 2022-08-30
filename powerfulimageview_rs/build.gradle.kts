plugins {
    id(Deps.androidLibrary)
    id(Deps.detektPlugin)
    kotlin("android")
}

ext {
    set("LIB_VERSION", "1.0.24") // This is the library version used when deploying the artifact
    set("ENABLE_DEPLOY", "true") //Flag whether the ci/cd workflow should deploy to sonatype or not

    set("LIB_GROUP_ID", "io.github.stefanosiano.powerful_libraries")                              // Maven Group ID for the artifact
    set("LIB_ARTIFACT_ID", "imageview_rs")                                                        // Maven Artifact ID for the artifact
    set("LIB_NAME", "Powerful Image View")                                                        // Library name
    set("SITE_URL", "https://github.com/stefanosiano/PowerfulImageView")                          // Homepage URL of the library
    set("GIT_URL", "https://github.com/stefanosiano/PowerfulImageView.git")                       // Git repository URL
    set("LIB_DESCRIPTION", "Android library with a powerful ImageView with several added features. It currently provides a progress indicator, several shapes and blur support.") // Library description
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 14
        targetSdk = 32
        consumerProguardFiles("piv-proguard-rules.txt")
        vectorDrawables.useSupportLibrary = true

        renderscriptSupportModeEnabled = true
    }
}
dependencies {
    implementation(Deps.appCompat)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.kotlinCoroutinesCore)
    detektPlugins(Deps.detektKtlintDependency)

    testImplementation(Deps.kotlinTestJunit)
    testImplementation(Deps.robolectric)
    testImplementation(Deps.androidxCore)
    testImplementation(Deps.androidxRunner)
    testImplementation(Deps.androidxTestCoreKtx)
    testImplementation(Deps.androidxTestRules)
    testImplementation(Deps.androidxJunit)
    testImplementation(Deps.androidxCoreKtx)
    testImplementation(Deps.mockitoKotlin)
    testImplementation(Deps.mockk)
}

apply("${rootProject.projectDir}/sonatype-publish.gradle")

detekt {
    toolVersion = Deps.detektPluginVersion
    config = files("${rootDir}/config/detekt/detekt.yml")
//    allRules = true
    buildUponDefaultConfig = true
    autoCorrect = false
}