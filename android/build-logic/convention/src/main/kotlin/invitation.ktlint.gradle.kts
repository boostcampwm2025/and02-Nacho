import gradle.kotlin.dsl.accessors._91bc9c3cf6fd3c8c115c0349fa718e74.ktlint

plugins {
    id("org.jlleitschuh.gradle.ktlint")
}

dependencies {
    add("ktlintRuleset", "io.nlopez.compose.rules:ktlint:0.4.28")
}

ktlint {
    android.set(true)
    ignoreFailures.set(false)
}
