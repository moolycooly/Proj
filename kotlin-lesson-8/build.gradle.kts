plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "1.4.21"
}

group = "org.fintech"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.ktor:ktor-client-core-jvm:2.3.12")
    implementation("io.ktor:ktor-client-cio-jvm:2.3.12")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.2")

    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.8")
    implementation("org.yaml:snakeyaml:2.3")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}