plugins {
    id("invitation.android.library")
    id("invitation.kotlin.serialization")
    id("invitation.android.hilt")
}

android {
    namespace = "com.andlife.network"
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