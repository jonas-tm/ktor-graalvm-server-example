FROM eclipse-temurin:17 AS JAR_IMAGE
COPY . .
RUN ./gradlew shadowJar

FROM ghcr.io/graalvm/native-image:22.1.0 AS NATIVE_IMAGE
COPY --from=JAR_IMAGE /build/libs/shadow.jar shadow.jar
COPY reflection.json reflection.json
RUN native-image --no-fallback  \
    --enable-all-security-services  \
    --report-unsupported-elements-at-runtime \
    --install-exit-handlers  \
    --allow-incomplete-classpath  \
    --initialize-at-build-time=io.ktor,kotlinx,kotlin,org.slf4j\
    -H:+ReportUnsupportedElementsAtRuntime  \
    -H:+ReportExceptionStackTraces  \
    -H:ReflectionConfigurationFiles=reflection.json  \
    -cp shadow.jar  \
    -H:Class=com.example.ApplicationKt  \
    -H:Name=nativeimage


FROM scratch
COPY --from=NATIVE_IMAGE nativeimage nativeimage
CMD ["/nativeimage"]
