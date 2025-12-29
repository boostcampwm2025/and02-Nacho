import extension.configureHiltAndroid
import extension.libs

plugins {
    id("invitation.android.library")
    id("invitation.android.compose")
    id("invitation.kotlin.serialization")
}

configureHiltAndroid()

dependencies {
    "implementation"(project(":domain"))
    "implementation"(project(":core:designsystem"))
    "implementation"(project(":core:ui"))

    "implementation"(libs.findLibrary("androidx-hilt-navigation-compose").get())
    "implementation"(libs.findLibrary("androidx-navigation-compose").get())
    "androidTestImplementation"(libs.findLibrary("androidx-navigation-testing").get())

    "implementation"(libs.findLibrary("androidx-compose-material3-adaptive-navigation-suite").get())
    "implementation"(libs.findLibrary("androidx-compose-material3-adaptive").get())
    "implementation"(libs.findLibrary("androidx-compose-material3-adaptive-layout").get())
    "implementation"(libs.findLibrary("androidx-compose-material3-adaptive-navigation").get())

    "implementation"(libs.findLibrary("androidx-lifecycle-viewModelCompose").get())
    "implementation"(libs.findLibrary("kotlinx-immutable").get())
    "implementation"(libs.findLibrary("coil-kt-compose").get())
}
