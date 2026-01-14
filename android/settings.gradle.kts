pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Invitation"
include(":app")
include(":feature:home")
include(":feature:invitation")
include(":feature:myinvitation")
include(":core:designsystem")
include(":core:ui")
include(":core:data")
include(":core:network")
include(":core:datastore")
include(":core:database")
include(":domain")
include(":feature:invitation-edit")
include(":feature:invitation-card")
include(":feature:thanks-card")
include(":feature:model")
