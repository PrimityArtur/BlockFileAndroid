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

rootProject.name = "BlockFile"
include(":app")
include(":core:model")
include(":core:common")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":feature:auth")
include(":feature:catalog")
include(":feature:productdetail")
include(":feature:profile")
include(":feature:rankings")
include(":feature:adminProducts")
include(":feature:adminCategories")
include(":feature:adminUsers")
