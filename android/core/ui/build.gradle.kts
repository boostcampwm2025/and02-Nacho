plugins {
    id("invitation.android.library")
    id("invitation.android.compose")
    id("invitation.android.hilt")
}

android {
    namespace = "com.andlife.ui"
}

dependencies {
    implementation(libs.lottie.compose)
    implementation(projects.feature.model)
    implementation(projects.core.media)
    // designSystem
    implementation(projects.core.designsystem)

    // immutable
    implementation(libs.kotlinx.immutable)

    // coil
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.network.okhttp)
    implementation(libs.coil.kt.video)

    // paging
    implementation(libs.androidx.paging.compose)

    // kotlinx datetime
    implementation(libs.kotlinx.datetime)

    // Activity Compose
    implementation(libs.androidx.activity.compose)

    // lifecycle
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ExoPlayer
    implementation(libs.bundles.media3)

    // Naver Map
    implementation(libs.naver.map.compose)

    implementation(libs.androidx.material3.adaptive.navigation.suite)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
