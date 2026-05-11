# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the JAR file inside Docker (skipping tests to avoid the BACKEND_URL error)
RUN mvn clean package -DskipTests

# Stage 2: Create the final small image
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the JAR from the 'build' stage to this final image
COPY --from=build /app/target/moneymanager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9020
ENTRYPOINT ["java", "-jar", "app.jar"]