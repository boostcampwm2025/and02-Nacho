plugins {
    id("invitation.android.feature")
    id("kotlin-parcelize")
}

android {
    namespace = "com.andlife.invitation_edit"
}

dependencies {
    implementation(projects.feature.editor)
    implementation(libs.androidx.compose.material.icons.extended)
    // paging
    implementation(libs.androidx.paging.compose)
    implementation(libs.kotlinx.datetime)
    implementation(libs.compose.reorderable)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
