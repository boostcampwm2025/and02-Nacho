plugins {
    id("invitation.android.feature")
}

android {
    namespace = "com.andlife.myinvitation"
}

dependencies {
    // feature modules 공통 UI 모델
    implementation(projects.feature.model)

    // kakao 공유
    implementation(libs.kakao.sdk.share)

    implementation(projects.core.deeplink)

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
