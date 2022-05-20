# ktor graalvm server example

This repository contains an example project to build a ktor jvm server with graalvm native-image

## Required
- GraalVM installed and set as environment variable `JAVA_HOME` (sdkman easiest variant)
- native-image installed (us `gu`) and reachable

## Build & Run native image
- Build shadowJar using `./gradlew :shadowJar`
- Build native image using `./build.sh`
- Run with `./graal-server`