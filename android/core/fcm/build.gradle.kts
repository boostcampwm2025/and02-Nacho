plugins {
    id("invitation.android.library")
    id("invitation.android.hilt")
}

android {
    namespace = "com.andlife.fcm"
}

dependencies {
    implementation(projects.domain)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
