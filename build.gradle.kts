@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
    `maven-publish`
    signing
    id("me.champeau.jmh") version "0.6.8"
    id("me.tatarka.retrolambda") version "3.7.1"
}

group = "me.xtrm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains", "annotations", "24.0.1")
    retrolambdaConfig("net.orfjackal.retrolambda", "retrolambda", "2.5.7")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

java {
    withJavadocJar()
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

retrolambda {
    javaVersion = JavaVersion.VERSION_1_5
}

tasks {
    getByName<Test>("test") {
        useJUnitPlatform()
    }
}