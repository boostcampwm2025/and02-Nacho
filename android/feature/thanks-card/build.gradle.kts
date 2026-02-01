plugins {
    id("invitation.android.feature")
}

android {
    namespace = "com.andlife.thanks_card"
}

dependencies {
    implementation(projects.feature.editor)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
