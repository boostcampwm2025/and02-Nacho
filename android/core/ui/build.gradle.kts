plugins {
    id("invitation.android.library")
    id("invitation.android.compose")
}

android {
    namespace = "com.andlife.ui"
}

dependencies {
    implementation(projects.feature.model)
    // designSystem
    implementation(projects.core.designsystem)

    // immutable
    implementation(libs.kotlinx.immutable)

    // coil
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.network.okhttp)

    // paging
    implementation(libs.androidx.paging.compose)

    // kotlinx datetime
    implementation(libs.kotlinx.datetime)

    // Activity Compose
    implementation(libs.androidx.activity.compose)

    // lifecycle
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Naver Map
    implementation(libs.naver.map.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
