pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()  // Make sure Maven Central is properly configured
        mavenLocal()    // Include Maven Local if you use locally published artifacts
        gradlePluginPortal()  // Ensure Gradle Plugin Portal is configured for plugins
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // Enforce centralized repository config
    repositories {
        google()  // Google repository for Android dependencies
        mavenCentral()  // Maven Central for common Java libraries
    }
}

rootProject.name = "SmartBikeHelmet"
include(":app")
