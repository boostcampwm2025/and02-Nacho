plugins {
    id("invitation.android.feature")
}

android {
    namespace = "com.andlife.setting"
}

dependencies {
    implementation(projects.feature.login)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.ucrop)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
