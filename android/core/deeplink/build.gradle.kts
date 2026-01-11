plugins {
    id("invitation.android.library")
    id("invitation.android.compose")
    id("invitation.android.hilt")
}

android {
    namespace = "com.andlife.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
