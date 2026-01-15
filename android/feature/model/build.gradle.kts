plugins {
    alias(libs.plugins.kotlin.android)
    id("invitation.android.library")
}

android {
    namespace = "com.andlife.model"
}

dependencies {
    implementation(projects.domain)

    // immutable
    implementation(libs.kotlinx.immutable)

    // kotlinx datetime
    implementation(libs.kotlinx.datetime)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
