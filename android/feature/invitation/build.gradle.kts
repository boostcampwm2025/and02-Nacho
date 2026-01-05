plugins {
    id("invitation.android.feature")
}

android {
    namespace = "com.andlife.invitation"
}

dependencies {
    // domain module
    implementation(projects.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
