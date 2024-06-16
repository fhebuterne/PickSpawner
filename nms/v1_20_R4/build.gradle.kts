plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(project(":nms:interfaces"))
    compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly(files("../../tmp/1.20.6/spigot-1.20.6-R0.1-SNAPSHOT.jar"))
}