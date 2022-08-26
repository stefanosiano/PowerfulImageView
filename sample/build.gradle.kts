plugins {
    id(Deps.androidApplication)
    id(Deps.detektPlugin)
    kotlin("android")
}

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "com.stefanosiano.powerfullibraries.imageviewsample"
        minSdk = 14
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true

        renderscriptTargetApi = 18
        renderscriptSupportModeEnabled = true

        testInstrumentationRunner = Deps.androidJUnitRunner
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
