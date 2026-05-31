# Step 1: Use a Maven image to compile and package the Java code
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Use a lightweight Java runtime image to execute your built app
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/attendance-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]