package extension

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureHiltAndroid() {
    with(pluginManager) {
        apply("com.google.devtools.ksp")
        apply("com.google.dagger.hilt.android")
    }

    dependencies {
        "ksp"(libs.findLibrary("hilt-android-compiler").get())
        "implementation"(libs.findLibrary("hilt-android").get())
    }
}
