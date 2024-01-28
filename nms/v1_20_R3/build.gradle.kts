plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(project(":nms:interfaces"))
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly(files("../../tmp/1.20.4/spigot-1.20.4-R0.1-SNAPSHOT.jar"))
}