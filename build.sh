./gradlew :shadowJar
native-image --no-fallback --enable-all-security-services --report-unsupported-elements-at-runtime \
--install-exit-handlers --allow-incomplete-classpath --initialize-at-build-time=io.ktor,kotlinx,kotlin,org.slf4j\
-H:+ReportUnsupportedElementsAtRuntime -H:+ReportExceptionStackTraces -cp ./build/libs/shadow.jar -H:Class=com.example.ApplicationKt -H:Name=graal-server
#native-image \
#  --no-fallback \
#  --enable-all-security-services \
#  --report-unsupported-elements-at-runtime \
#  --install-exit-handlers \
#  --allow-incomplete-classpath \
#  --initialize-at-build-time=io.ktor,kotlinx,kotlin,org.slf4j\
#  -H:+ReportUnsupportedElementsAtRuntime \
#  -H:+ReportExceptionStackTraces \
#  -cp ./build/libs/shadow.jar \
#  -H:Class=com.example.ApplicationKt \
#  -H:Name=graal-server
