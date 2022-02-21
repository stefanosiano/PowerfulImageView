// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.stefanosiano.powerful_libraries.imageview.plugins_non_rs")
    id("io.gitlab.arturbosch.detekt").version("1.19.0")
}
buildscript {

    repositories {
        google()
    }
    dependencies {
        classpath(Deps.androidGradlePlugin)
        classpath(Deps.kotlinGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

detekt {
    toolVersion = "1.19.0"
    config = files("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}