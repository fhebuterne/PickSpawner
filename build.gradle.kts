plugins {
    kotlin("jvm") version Versions.kotlinJvm
    id("com.github.johnrengelman.shadow") version Versions.shadowJar
}

group = "fr.fabienhebuterne"
version = "1.0.0"

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://jitpack.io")
    }

    defaultDependencies()
    testDependencies()
}

