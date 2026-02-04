package extension

import androidx.room.gradle.RoomExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidRoom() {
    with(pluginManager) {
        apply("androidx.room")
        apply("com.google.devtools.ksp")
    }

    dependencies {
        "implementation"(libs.findLibrary("room-runtime").get())
        "implementation"(libs.findLibrary("room-ktx").get())
        "implementation"(libs.findLibrary("room-paging").get())
        "ksp"(libs.findLibrary("room-compiler").get())
    }

    extensions.configure<RoomExtension> {
        schemaDirectory("$projectDir/schemas")
    }

    extensions.configure<KspExtension> {
        arg("room.generateKotlin", "true")
    }
}
