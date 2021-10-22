plugins {
    kotlin("plugin.serialization") version KOTLIN_VERSION apply false
    kotlin("multiplatform") version KOTLIN_VERSION apply false
    id("org.jetbrains.compose") version "1.0.0-alpha4-build418" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$ANDROID_GRADLE_PLUGIN")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
