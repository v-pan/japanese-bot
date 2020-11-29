import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
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
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}