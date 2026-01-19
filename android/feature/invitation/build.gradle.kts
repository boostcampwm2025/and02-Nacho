plugins {
    id("invitation.android.feature")
}

android {
    namespace = "com.andlife.invitation"
}

dependencies {
    implementation(projects.core.media)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // kotlinx datetime
    implementation(libs.kotlinx.datetime)

    // ExoPlayer
    implementation(libs.bundles.media3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
