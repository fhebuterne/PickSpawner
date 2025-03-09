plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(project(":nms:interfaces"))
    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly(files("../../tmp/1.21.3/spigot-1.21.3-R0.1-SNAPSHOT.jar"))
}