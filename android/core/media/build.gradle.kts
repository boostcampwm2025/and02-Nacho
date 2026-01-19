plugins {
    id("invitation.android.library")
    id("invitation.android.hilt")
}

android {
    namespace = "com.andlife.media"
}

dependencies {
    implementation(libs.androidx.media3.exoplayer)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
