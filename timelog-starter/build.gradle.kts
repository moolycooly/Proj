plugins {
    id("java")
}

group = "org.fintech"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.springframework.boot:spring-boot-starter-aop:3.3.3")

}

tasks.test {
    useJUnitPlatform()
}