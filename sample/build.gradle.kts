plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-android-extensions")
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

        dataBinding { isEnabled = true }

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
//    implementation(project(path = ":powerfulimageview_databinding"))
    implementation(project(path = ":powerfulimageview_rs"))
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.30")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
}
