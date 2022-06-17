# Ktor GraalVM Server Example

This repository contains an example project to build a ktor jvm server with GraalVM native-image.

## Features
- JSON Rest API (`/api/v1/news`)
- Exposed DB persistence
- H2 in-memory database
- Custom Logging Middleware (Ktor Application Plugin)

## Required
- Linux/MacOS
- GraalVM installed and set as environment variable `JAVA_HOME` (sdkman easiest variant)
- native-image installed (uss `gu`) and reachable

## Build & Run native image
- Build native image using `./build.sh`
  - *This first builds a shadow jar with gradle and then trigger native-image build*
- Run with `./graal-server`

## Known Issues
- Serialization is not working for native-image out of the box
  - Follow [this GitHub Issue](https://github.com/Kotlin/kotlinx.serialization/issues/1348) for progress 
  - Somewhat workaround: Analyze reflection by running Jar with tracing mode
    - Run `./gradlw :shardowJar`
    - Copy jar from `/build/libs` to main folder
    - Create a folder `/reflection`
    - Run `java -agentlib:native-image-agent=config-output-dir=reflect -jar com.example.sample-0.0.1-all.jar`
    - Hit all endpoint in all variants once
    - Copy content of json array `/reflection/reflect-config.json` 
    - Add content at the end of json array in `/reflection.json`
    - *Seems not to be fixing list news endpoint*