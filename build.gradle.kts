import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktor_version: String by extra { "2.0.2" }
val kotlin_version: String by extra { "1.7.0" }
val logback_version: String by extra { "1.2.3" }
val exposed_version: String by extra { "0.38.2" }
val h2_version: String by extra { "2.1.214" }

plugins {
    application
    kotlin("jvm") version "1.7.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.0"
    id("org.graalvm.buildtools.native") version "0.9.11"
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

tasks.withType<ShadowJar> {
    archiveFileName.set("shadow.jar")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

    implementation("io.ktor:ktor-server-compression-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")

//    implementation("io.ktor:ktor-serialization-gson-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("com.h2database:h2:$h2_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
}

graalvmNative {
    binaries {
        named("main") {
            fallback.set(false)
            verbose.set(true)

//            buildArgs.add("--enable-all-security-services")
//            buildArgs.add("--report-unsupported-elements-at-runtime")
//            buildArgs.add("--install-exit-handlers")
//            buildArgs.add("--allow-incomplete-classpath")
//            useFatJar.set(true)

            buildArgs.add("--initialize-at-build-time=io.ktor,kotlin,kotlinx,org.slf4j")

            buildArgs.add("-H:+InstallExitHandlers")
            buildArgs.add("-H:+ReportUnsupportedElementsAtRuntime")
            buildArgs.add("-H:+ReportExceptionStackTraces")

            imageName.set("graal-server")
        }
    }
}

