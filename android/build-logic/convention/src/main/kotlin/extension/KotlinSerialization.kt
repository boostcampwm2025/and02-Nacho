package extension

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureKotlinSerialization() {
    with(pluginManager) {
        apply("org.jetbrains.kotlin.plugin.serialization")
    }

    dependencies {
        "implementation"(libs.findLibrary("kotlinx-serialization-json").get())
    }
}
