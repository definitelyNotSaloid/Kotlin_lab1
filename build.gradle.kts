import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    application
}

group = "me.saloid"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.0")
    implementation("dev.inmo:tgbotapi:4.2.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

application {
    mainClass.set("MainKt")
}