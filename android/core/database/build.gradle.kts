plugins {
    id("invitation.android.library")
    id("invitation.android.room")
    id("invitation.android.hilt")
    id("invitation.kotlin.serialization")
}

android {
    namespace = "com.andlife.database"
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
