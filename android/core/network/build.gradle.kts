import java.util.Properties

plugins {
    id("invitation.android.library")
    id("invitation.kotlin.serialization")
    id("invitation.android.hilt")
}

android {
    namespace = "com.andlife.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {

        val properties = Properties()
        val localProps = project.rootProject.file("local.properties")
        properties.load(localProps.inputStream())
        val serverUrl = properties.getProperty("SERVER_URL") ?: ""

        buildConfigField("String", "SERVER_URL", "\"$serverUrl\"")
    }
}

dependencies {
    // retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
