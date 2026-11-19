rootProject.name = "kahf-ads-cmp-example"

// Public Kahf Ads Maven repo (served by GitHub Pages from this repository's /maven folder).
// Override for local testing: ./gradlew build -PkahfAdsMavenUrl=file:///path/to/maven
val kahfAdsMavenUrl = providers.gradleProperty("kahfAdsMavenUrl")
    .orElse("https://kahfads.github.io/kahf-ads-sdk-cmp/maven")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven {
            url = uri(kahfAdsMavenUrl.get())
            content { includeGroupAndSubgroups("co.uk.kahf.kahfAds") }
        }
    }
}

include(":app")
