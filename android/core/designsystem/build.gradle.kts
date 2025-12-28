plugins {
    id("invitation.android.library")
    id("invitation.android.compose")
}

android {
    namespace = "com.andlife.designsystem"
}

dependencies {
    // datetime
    implementation(libs.kotlinx.datetime)

    // immutable
    implementation(libs.kotlinx.immutable)

    // coil
    implementation(libs.coil.kt.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
