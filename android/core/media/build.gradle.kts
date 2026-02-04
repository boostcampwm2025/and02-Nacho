plugins {
    id("invitation.android.library")
    id("invitation.android.hilt")
}

android {
    namespace = "com.andlife.media"
}

dependencies {
    implementation(libs.bundles.media3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
