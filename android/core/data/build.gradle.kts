plugins {
    id("invitation.android.library")
    id("invitation.android.hilt")
    id("invitation.kotlin.serialization")
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

    // paging
    implementation(libs.androidx.paging.runtime)

    // retrofit
    implementation(libs.retrofit)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // kotlinx datetime
    implementation(libs.kotlinx.datetime)

    // exif
    implementation(libs.androidx.exifinterface)

    // workmanager Hilt
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
