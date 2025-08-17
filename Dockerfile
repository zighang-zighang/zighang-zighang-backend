########################
# 1) Build stage
########################
FROM gradle:8.14.3-jdk21-alpine AS builder

WORKDIR /application

COPY gradle gradle
COPY build.gradle.kts gradlew ./
RUN ./gradlew dependencies --no-daemon --quiet

COPY src src
RUN ./gradlew bootJar -x test --no-daemon --quiet \
    && java -Djarmode=layertools -jar build/libs/*.jar extract

########################
# 2) Runtime stage
########################
FROM gcr.io/distroless/java21-debian12:nonroot AS runtime

USER nonroot
WORKDIR /application

COPY --from=builder --chown=nonroot:nonroot /application/dependencies/ ./
COPY --from=builder --chown=nonroot:nonroot /application/snapshot-dependencies/ ./
COPY --from=builder --chown=nonroot:nonroot /application/spring-boot-loader/ ./
COPY --from=builder --chown=nonroot:nonroot /application/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=production", "org.springframework.boot.loader.launch.JarLauncher"]
