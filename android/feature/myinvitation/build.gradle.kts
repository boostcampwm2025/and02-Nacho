plugins {
    id("invitation.android.feature")
}

android {
    namespace = "com.andlife.myinvitation"
}

dependencies {
    // designSystem
    implementation(projects.core.designsystem)

    // ui
    implementation(projects.core.ui)

    // domain
    implementation(projects.domain)

    // coil
    implementation(libs.coil.kt.compose)

    // paging
    implementation(libs.androidx.paging.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
