plugins {
    kotlin("jvm")
}

dependencies {
    compileOnly(project(":nms:interfaces"))
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly(files("../../tmp/1.19.3/spigot-1.19.3-R0.1-SNAPSHOT.jar"))
}