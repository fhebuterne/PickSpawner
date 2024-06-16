plugins {
    kotlin("jvm") version Versions.kotlinJvm
    id("com.github.johnrengelman.shadow") version Versions.shadowJar
}

allprojects {
    group = "fr.fabienhebuterne"
    version = "3.6.0"

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

    val javaVersion = JavaVersion.VERSION_17.toString()

    tasks.compileKotlin {
        kotlinOptions {
            jvmTarget = javaVersion
        }
    }

    tasks.compileTestKotlin {
        kotlinOptions {
            jvmTarget = javaVersion
        }
    }

    tasks.compileJava {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.compileTestJava {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

// This is workaround to fix build issue with jackson 2.17.x and shadowJar 8.1.x
buildscript {
    configurations {
        classpath {
            resolutionStrategy {
                //in order to handle jackson's higher release version in shadow, this needs to be upgraded to latest.
                force("org.ow2.asm:asm:9.6")
                force("org.ow2.asm:asm-commons:9.6")
            }
        }
    }
}