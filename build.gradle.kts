plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.github.l1an.variflight2steplife"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.opencsv:opencsv:5.8")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}