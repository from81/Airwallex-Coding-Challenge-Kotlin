import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.5.30"
    application
}

group = "com.airwallex"
version = "1.0-SNAPSHOT"

application {
    applicationDefaultJvmArgs = listOf("-Dkotlinx.coroutines.debug")
}

repositories {
    mavenCentral()
    google()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.9+")
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9+")

    testImplementation("org.junit.jupiter", "junit-jupiter", "5.4.2")
    testImplementation("org.assertj", "assertj-core", "3.11.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    mainClass.set("com.airwallex.codechallenge.AppKt")
}