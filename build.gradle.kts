plugins {
    kotlin("jvm") version Versions.kotlinJvm
    id("com.github.johnrengelman.shadow") version Versions.shadowJar
    id("com.jetbrains.exposed.gradle.plugin") version Versions.exposedGradlePlugin
}

allprojects {
    group = "fr.fabienhebuterne"
    version = "1.0.0"

    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://jitpack.io")
        maven("https://libraries.minecraft.net/")
    }

    defaultDependencies()
    testDependencies()
}

