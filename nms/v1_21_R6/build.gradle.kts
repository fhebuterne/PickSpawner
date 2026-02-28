plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(project(":nms:interfaces"))
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.2-SNAPSHOT")
    compileOnly(files("../../tmp/1.21.11/spigot-1.21.11-R0.2-SNAPSHOT.jar"))
}