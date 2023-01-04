val kotlin_version = "1.8.0"
val logback_version = "1.4.1"
val exposed_version = "0.39.2"
val h2_version = "2.1.214"
val prometheus_version = "1.9.4"

plugins {
    application
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
    id("io.ktor.plugin") version "2.2.2"
    id("org.graalvm.buildtools.native") version "0.9.19"
//    id("com.github.johnrengelman.shadow") version "7.1.1"
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
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("io.ktor:ktor-server-compression-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-cio")

    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-call-id")

    implementation("io.ktor:ktor-serialization-kotlinx-json")

    implementation("io.ktor:ktor-server-metrics-micrometer")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheus_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("com.h2database:h2:$h2_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-client-content-negotiation")
}

ktor {
    docker {
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_17)
        localImageName.set("ktor-graalvm-server-example")
        imageTag.set("my-docker-sample")

        externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
                appName = provider { "ktor-graalvm-server-example" },
                username = providers.environmentVariable("DOCKER_USERNAME"),
                password = providers.environmentVariable("DOCKER_PASSWORD")
            )
        )
    }
}


graalvmNative {
    metadataRepository {
        enabled.set(true)
    }

    binaries {
        named("main") {
            useFatJar.set(true)
            fallback.set(false)
//            verbose.set(true)

            buildArgs.add("--enable-all-security-services")
            buildArgs.add("--report-unsupported-elements-at-runtime")
            buildArgs.add("--install-exit-handlers")
            buildArgs.add("--allow-incomplete-classpath")

            buildArgs.add("--initialize-at-build-time=io.ktor,kotlin,kotlinx,org.slf4j,ch.qos.logback")

            buildArgs.add("-H:+InstallExitHandlers")
            buildArgs.add("-H:+ReportUnsupportedElementsAtRuntime")
            buildArgs.add("-H:+ReportExceptionStackTraces")

            imageName.set("graal-server")
        }
    }
}

