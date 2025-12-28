plugins {
    id("invitation.android.library")
    id("invitation.android.hilt")
}

android {
    namespace = "com.andlife.data"
}

dependencies {
    // domain
    implementation(projects.domain)

    // datasource
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.datastore)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}