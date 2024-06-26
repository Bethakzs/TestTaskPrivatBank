# Stage 1: Build stage
FROM maven:3.8.5-openjdk-21-slim AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Production stage
FROM adoptopenjdk:17-jre-hotspot

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
