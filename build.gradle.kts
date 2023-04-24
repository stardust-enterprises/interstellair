@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.+"
    id("me.champeau.jmh") version "0.6.+"
}

group = "enterprises.stardust"
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

    afterEvaluate {
        // Task priority
        val publishToSonatype by getting
        val closeAndReleaseSonatypeStagingRepository by getting

        closeAndReleaseSonatypeStagingRepository
            .mustRunAfter(publishToSonatype)

        // Wrapper task since calling both one after the other in IntelliJ
        // seems to cause some problems.
        create("releaseToSonatype") {
            group = "publishing"

            dependsOn(
                publishToSonatype,
                closeAndReleaseSonatypeStagingRepository
            )
        }
    }
}

val artifactTasks = arrayOf(
    tasks["apiJar"],
)
artifacts {
    artifactTasks.forEach {
        add("archives", it)
    }
}

val projectName: String = project.name
val desc = "A Java Stream API near drop-in alternative designed around simplicity and efficiency"
val repo = "stardust-enterprises/$projectName"

publishing.publications {
    create<MavenPublication>("mavenJava") {
        from(components["java"])
        artifactTasks.forEach(::artifact)

        pom {
            name.set(projectName)
            description.set(desc)
            url.set("https://github.com/$repo")

            licenses {
                license {
                    name.set("ISC License")
                    url.set("https://opensource.org/licenses/ISC")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    id.set("xtrm")
                    name.set("xtrm")
                    email.set("oss@xtrm.me")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/$repo.git")
                developerConnection.set("scm:git:ssh://github.com/$repo.git")
                url.set("https://github.com/$repo")
            }
        }

        // Configure the signing extension to sign this Maven artifact.
        signing {
            isRequired = project.properties["signing.keyId"] != null
            sign(this@create)
        }
    }
}

// Set up the Sonatype artifact publishing.
nexusPublishing.repositories.sonatype {
    nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
    snapshotRepositoryUrl.set(
        uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    )

    // Skip this step if environment variables NEXUS_USERNAME or NEXUS_PASSWORD aren't set.
    username.set(properties["NEXUS_USERNAME"] as? String ?: return@sonatype)
    password.set(properties["NEXUS_PASSWORD"] as? String ?: return@sonatype)
}
