// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id(Deps.createNonRsPlugin)
    id(Deps.detektPlugin).version(Deps.detektPluginVersion)
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
    toolVersion = Deps.detektPluginVersion
    config = files("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}