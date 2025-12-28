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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}