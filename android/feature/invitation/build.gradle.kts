plugins {
    id("invitation.android.feature")
}

android {
    namespace = "com.andlife.invitation"
}

dependencies {
    implementation(projects.feature.editor)
    implementation(projects.core.media)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // kotlinx datetime
    implementation(libs.kotlinx.datetime)

    // ExoPlayer
    implementation(libs.bundles.media3)

    // Paging
    implementation(libs.androidx.paging.compose)

    // adaptive
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
