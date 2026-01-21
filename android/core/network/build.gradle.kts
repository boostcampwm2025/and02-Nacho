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
        val headerUserId = properties.getProperty("HEADER_USER_ID") ?: ""
        val userId = properties.getProperty("USER_ID") ?: ""

        buildConfigField("String", "SERVER_URL", "\"$serverUrl\"")
        buildConfigField("String", "HEADER_USER_ID", "\"$headerUserId\"")
        buildConfigField("String", "USER_ID", "\"$userId\"")
    }
}

dependencies {
    // retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // kotlin datetime
    implementation(libs.kotlinx.datetime)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
