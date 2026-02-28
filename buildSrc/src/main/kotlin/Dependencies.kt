import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

fun Project.defaultDependencies() {
    dependencies {
        "testCompileOnly"(kotlin("stdlib-jdk8"))
        "compileOnly"(kotlin("stdlib-jdk8"))
    }
}

fun Project.testDependencies() {
    dependencies {
        "testImplementation"("io.mockk:mockk:${Versions.mockk}")
        "testImplementation"("io.strikt:strikt-core:${Versions.strikt}")
        "testImplementation"("org.junit.jupiter:junit-jupiter:${Versions.junit}")
        "testImplementation"("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    }
}