import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "me.tapir"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io")
    maven(url = "https://dl.bintray.com/kordlib/Kord")
}

dependencies {
    testImplementation(kotlin("test-junit5"))

    // Microsoft Graph dependencies
    implementation("com.google.guava:guava:28.2-jre")
    implementation( "com.microsoft.azure:msal4j:1.4.0")
    implementation("com.microsoft.graph:microsoft-graph:1.6.0")
    implementation("org.slf4j:slf4j-nop:1.8.0-beta4")

    // Kord Discord Bot API
    implementation("com.gitlab.kordlib.kord:kord-core:0.6.3")

    // PDFBox
    implementation("org.apache.pdfbox:pdfbox:2.0.21")

    // Kuromoji - Japanese morphological analyzer
    implementation("com.github.atilika.kuromoji:kuromoji:0.9.0")
    implementation("com.github.atilika.kuromoji:kuromoji-ipadic:0.9.0")

    // Clikt - Command line parsing
    implementation("com.github.ajalt.clikt:clikt:3.0.1")

    // Tensorflow
//    implementation(group = "org.tensorflow", name = "tensorflow-core-platform", version = "0.2.0")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets {
    main {
        resources {
            srcDir("src/main/resources")
        }
    }
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "MainKt"
            )
        )
    }
}

application {
    mainClassName = "MainKt"
}