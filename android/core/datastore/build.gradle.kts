plugins {
    id("invitation.android.library")
    id("invitation.android.hilt")
}

android {
    namespace = "com.andlife.datastore"
}

dependencies {
    // datastore
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.core)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}