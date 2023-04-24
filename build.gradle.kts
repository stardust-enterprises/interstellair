@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
    `maven-publish`
    signing
    id("me.champeau.jmh") version "0.6.8"
}

group = "me.xtrm"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains", "annotations", "24.0.1")

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

tasks {
    getByName<Test>("test") {
        useJUnitPlatform()
    }

    val apiJar = create("apiJar", Jar::class.java) {
        archiveClassifier.set("api")
        from(sourceSets["main"].output)
        exclude("enterprises/stardust/interstellair/impl")
        exclude("enterprises/stardust/interstellair/impl/**/*")
        manifest.attributes(
            "Specification-Title" to project.name,
            "Specification-Version" to "1",
            "Specification-Vendor" to "Stardust Enterprises",
        )
    }

    jar {
        manifest.attributes += apiJar.manifest.attributes
        manifest.attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Stardust Enterprises",
        )
    }
}

artifacts {
    archives(tasks["apiJar"])
}
