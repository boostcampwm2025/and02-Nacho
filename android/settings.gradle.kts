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
        maven(url = "https://devrepo.kakao.com/nexus/content/groups/public/")
        maven(url = "https://repository.map.naver.com/archive/maven")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Nacho"
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
include(":core:deeplink")
include(":domain")
include(":feature:invitation-edit")
include(":feature:invitation-card")
include(":feature:thanks-card")
include(":feature:model")
include(":core:media")
