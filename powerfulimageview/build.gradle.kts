plugins {
    id("com.android.library")
    id("kotlin-android")
    id("io.gitlab.arturbosch.detekt")
}

ext {
    set("LIB_VERSION", "1.0.23") // This is the library version used when deploying the artifact
    set("ENABLE_DEPLOY", "true") //Flag whether the ci/cd workflow should deploy to sonatype or not

    set("LIB_GROUP_ID", "io.github.stefanosiano.powerful_libraries")                              // Maven Group ID for the artifact
    set("LIB_ARTIFACT_ID", "imageview")                                                        // Maven Artifact ID for the artifact
    set("LIB_NAME", "Powerful Image View")                                                        // Library name
    set("SITE_URL", "https://github.com/stefanosiano/PowerfulImageView")                          // Homepage URL of the library
    set("GIT_URL", "https://github.com/stefanosiano/PowerfulImageView.git")                       // Git repository URL
    set("LIB_DESCRIPTION", "Android library with a powerful ImageView with several added features. It currently provides a progress indicator, several shapes and blur support.") // Library description
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 14
        targetSdk = 30
        consumerProguardFiles("piv-proguard-rules.txt")
        vectorDrawables.useSupportLibrary = true

        
    }
}
dependencies {
    implementation(Deps.appCompat)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.kotlinCoroutinesCore)
    detektPlugins(Deps.detektKtlintPlugin)
}

apply("${rootProject.projectDir}/sonatype-publish.gradle")

detekt {
    toolVersion = "1.19.0"
    config = files("${rootDir}/config/detekt/detekt.yml")
//    allRules = true
    buildUponDefaultConfig = true
    autoCorrect = false
}