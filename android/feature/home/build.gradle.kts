plugins {
    id("invitation.android.feature")
}

android {
    namespace = "com.andlife.home"
}

dependencies {
    implementation(projects.core.media)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Kotlin DateTime
    implementation(libs.kotlinx.datetime)

    // ExoPlayer
    implementation(libs.bundles.media3)

    // Paging
    implementation(libs.androidx.paging.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
