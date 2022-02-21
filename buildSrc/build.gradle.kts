plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.0.4")
}

gradlePlugin {
    plugins {
        create("nonRsPlugin") {
            id = "com.stefanosiano.powerful_libraries.imageview.plugins_non_rs"
            displayName = "Powerful ImageView Non Rs Plugin"
            description = "Create the non renderscript module from the renderscript one"
            implementationClass = "com.stefanosiano.powerful_libraries.imageview.NonRsPlugin"
        }
    }
}
