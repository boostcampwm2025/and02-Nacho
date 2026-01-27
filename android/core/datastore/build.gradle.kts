plugins {
    id("invitation.android.library")
    id("invitation.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.andlife.datastore"
}

dependencies {
    // datastore
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.core)

    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
