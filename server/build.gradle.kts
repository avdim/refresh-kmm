plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$KTOR_VERSION")
    implementation("io.ktor:ktor-serialization:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-host-common:$KTOR_VERSION")
    implementation("io.ktor:ktor-server-netty:$KTOR_VERSION")
    implementation(LOG_MAVEN_ARTIFACT)
    testImplementation("io.ktor:ktor-server-tests:$KTOR_VERSION")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$KOTLIN_VERSION")
}
