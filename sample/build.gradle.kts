plugins {
    id(Deps.androidApplication)
    id(Deps.detektPlugin)
    kotlin("android")
}

android {
    compileSdk = Deps.sdkCompile

    defaultConfig {
        applicationId = "com.stefanosiano.powerfullibraries.imageviewsample"
        minSdk = Deps.sdkMin
        targetSdk = Deps.sdkTarget
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true

        renderscriptTargetApi = 18
        renderscriptSupportModeEnabled = true

        testInstrumentationRunner = Deps.androidJUnitRunner

        // The following argument makes the Android Test Orchestrator run its
        // "pm clear" command after each test invocation. This command ensures
        // that the app's state is completely cleared between tests.
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    buildTypes {
        debug {
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree("libs") { include("*.jar") })
    implementation(project(path = ":powerfulimageview_rs"))
    implementation(Deps.appCompat)
    implementation(Deps.kotlinStdLib)
    implementation(Deps.kotlinCoroutinesCore)
    detektPlugins(Deps.detektKtlintDependency)

    androidTestImplementation(Deps.kotlinTestJunit)
    androidTestImplementation(Deps.espressoCore)
    androidTestImplementation(Deps.espressoIdlingResource)
    androidTestImplementation(Deps.androidxRunner)
    androidTestImplementation(Deps.androidxTestRules)
    androidTestImplementation(Deps.androidxTestCoreKtx)
    androidTestImplementation(Deps.androidxJunit)
    androidTestUtil(Deps.androidxTestOrchestrator)
}

detekt {
    toolVersion = Deps.detektPluginVersion
    config = files("${rootDir}/config/detekt/detekt.yml")
//    allRules = true
    buildUponDefaultConfig = true
    autoCorrect = false
}
