plugins {
    id("invitation.android.library")
    id("invitation.android.compose")
}

android {
    namespace = "com.andlife.ui"
}

dependencies {
    // designSystem
    implementation(projects.core.designsystem)

    // immutable
    implementation(libs.kotlinx.immutable)

    // coil
    implementation(libs.coil.kt.compose)

    // paging
    implementation(libs.androidx.paging.compose)

    // kotlinx datetime
    implementation(libs.kotlinx.datetime)

    // lifecycle
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
