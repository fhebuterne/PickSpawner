plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("com.jetbrains.exposed.gradle.plugin")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:${Versions.vault}") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinJvm}")
    implementation("com.fasterxml.jackson.core:jackson-core:${Versions.jackson}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformats-text:${Versions.jackson}")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${Versions.jackson}")
    implementation("me.lucko:commodore:${Versions.commodore}")
}

tasks.processResources {
    filesMatching("**/**.yml") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        expand(project.properties)
    }
}

val buildVersion: String? by project

tasks.shadowJar {
    mergeServiceFiles()

    if (buildVersion == null) {
        archiveFileName.set("PickSpawner-${archiveVersion.getOrElse("unknown")}.jar")
    } else {
        // For ci/cd
        archiveFileName.set("PickSpawner.jar")
    }

    relocate("org.yaml", "fr.fabienhebuterne.pickspawner.libs.org.yaml")
    relocate("com.fasterxml", "fr.fabienhebuterne.pickspawner.libs.com.fasterxml")
    relocate("org.intellij", "fr.fabienhebuterne.pickspawner.libs.org.intellij")
    relocate("org.jetbrains.annotations", "fr.fabienhebuterne.pickspawner.libs.org.jetbrains.annotations")
    relocate("me.lucko.commodore", "fr.fabienhebuterne.pickspawner.libs.me.lucko.commodore")

    exclude("DebugProbesKt.bin")
    exclude("module-info.class")

    dependencies {
        exclude(dependency("com.mojang:brigadier"))
    }

    destinationDirectory.set(file(System.getProperty("outputDir") ?: "$rootDir/build/"))
}

tasks.build {
    dependsOn("shadowJar")
}
