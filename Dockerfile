FROM ghcr.io/graalvm/native-image:22.2.0 AS NATIVE_IMAGE
COPY . .
RUN ./gradlew nativeCompile


FROM scratch
COPY --from=NATIVE_IMAGE nativeimage nativeimage
CMD ["/nativeimage"]
