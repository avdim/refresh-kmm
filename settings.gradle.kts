rootProject.name = "refresh"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

include(":androidApp")
include(":shared")
include(":server")
include(":serialized-data")

