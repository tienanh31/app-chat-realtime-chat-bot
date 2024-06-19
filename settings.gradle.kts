pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()


    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url=uri( "https://storage.zego.im/maven") }   // <- Add this line.
        maven { url=uri ("https://www.jitpack.io") }
        google()
        mavenCentral()
    }
}

rootProject.name = "APPCHAT"
include(":app")
 