# Ktor GraalVM Server Example

This repository contains an example project to build a ktor jvm server with GraalVM native-image.

## Features
- JSON Rest API (`/api/v1/news`)
- Exposed DB persistence
- H2 in-memory database
- Micrometer + Prometheus monitoring
- Automatic error handling
- CallID plugin
- Custom Logging Middleware (Ktor Application Plugin)

## Required
- Linux/MacOS
- GraalVM installed and set as environment variable `JAVA_HOME` (sdkman easiest variant)
- native-image installed (use `gu`) and reachable

## Build & Run native image
- Directly run with `./gradlew nativeRun`
- Compile
  - `./gradlew nativeCompile`
  - Run with `./graal-server`
- Use option `--native-quick-build` or evironment variable `GRAALVM_QUICK_BUILD=true` for faster builds

## Docker Image (not tested!)
- Use `Dockerfile` for multistage native-image build.
- Results image is minmal size based of `scratch`

## Known Issues
- Micrometer not working correct on native
- Serialization is not working for native-image out of the box
  - Follow [this GitHub Issue](https://github.com/Kotlin/kotlinx.serialization/issues/1348) for progress 
  - Somewhat workaround: Analyze reflection by running Jar with tracing mode
    - Run with tracing `./gradlew -Pagent run`
      - Try to test all possible paths (also possible via running tests)
    - Copy tracing config `./gradlew metadataCopy --task run --dir src/main/resources/META-INF/native-image`