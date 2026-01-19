plugins {
    id("invitation.android.feature")
}

android {
    namespace = "com.andlife.home"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Kotlin DateTime
    implementation(libs.kotlinx.datetime)

    implementation(projects.core.media)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
