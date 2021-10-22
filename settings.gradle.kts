rootProject.name = "refresh"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":androidApp")
include(":shared")
include(":server")
include(":serialized-data")
include(":common-desktop")
include(":desktop")
