# Stage 1: Build the application using Maven and JDK 21
FROM openjdk:21-slim AS build

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy application source code and build (skipping tests)
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Use Eclipse Temurin JRE to run the application
FROM eclipse-temurin:21-jre-jammy

# Copy the compiled JAR file from the build stage
COPY --from=build /target/*.jar app.jar

# Set the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
