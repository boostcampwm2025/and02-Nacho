pluginManagement {
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

rootProject.name = "Invitation"
include(":app")
include(":feature:home")
include(":feature:invitation")
include(":feature:myinvitation")
include(":core:navigator")
include(":core:designsystem")
include(":core:ui")
include(":core:data")
include(":core:network")
include(":core:datastore")
include(":core:database")
