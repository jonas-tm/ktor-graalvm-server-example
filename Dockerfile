FROM ghcr.io/graalvm/native-image:22.1.0 AS NATIVE_IMAGE
COPY . .
RUN ./gradlew shadowJar
RUN native-image --no-fallback  \
    --enable-all-security-services  \
    --report-unsupported-elements-at-runtime \
    --install-exit-handlers  \
    --allow-incomplete-classpath  \
    --initialize-at-build-time=io.ktor,kotlinx,kotlin,org.slf4j\
    -H:+ReportUnsupportedElementsAtRuntime  \
    -H:+ReportExceptionStackTraces  \
    -H:ReflectionConfigurationFiles=reflection.json  \
    -cp ./build/libs/shadow.jar  \
    -H:Class=com.example.ApplicationKt  \
    -H:Name=nativeimage


FROM scratch
COPY --from=NATIVE_IMAGE nativeimage nativeimage
CMD ["/nativeimage"]
