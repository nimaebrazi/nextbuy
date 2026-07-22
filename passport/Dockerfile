# syntax=docker/dockerfile:1

# ==========================================
# STAGE 1: Base Builder & Dependency Cache
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:resolve dependency:resolve-plugins -DincludeScope=test

COPY src ./src
RUN mvn -B compile test-compile

# ==========================================
# STAGE 2A: Unit Tests (Pure Java, no Spring, lightning fast)
# ==========================================
FROM builder AS unit-test-runner
RUN mvn -B test -Dtest="com.nextbuy.passport.unit.**"

# ==========================================
# STAGE 2B: Controller Tests (Web Slices using @WebMvcTest)
# ==========================================
FROM builder AS slice-test-runner
RUN mvn -B test -Dtest="com.nextbuy.passport.slice.**"

# ==========================================
# STAGE 2C: Component Integration Tests (Internal cross-service logic)
# ==========================================
FROM builder AS integration-test-runner
RUN mvn -B test \
    -Dtest="com.nextbuy.passport.integration.**" \
    -Dspring.profiles.active=test

# ==========================================
# STAGE 3: Production Package Builder
# ==========================================
FROM builder AS packager
RUN mvn package -DskipTests

# ==========================================
# STAGE 4: Final Production Runtime Image
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS final
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=packager /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseContainerSupport", \
  "-jar", "app.jar"]