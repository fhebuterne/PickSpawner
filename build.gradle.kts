plugins {
    kotlin("jvm") version Versions.kotlinJvm
    id("com.github.johnrengelman.shadow") version Versions.shadowJar
}

allprojects {
    group = "fr.fabienhebuterne"
    version = "3.4.2"

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

    tasks.compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    tasks.compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}
