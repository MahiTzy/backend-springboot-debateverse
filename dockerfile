# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Set the working directory in the build container
WORKDIR /build

# Copy the project files to the container
COPY . .

# Build the application and package it
RUN mvn clean install -DskipTests

# Stage 2: Package the application
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /build/target/debateverse-0.0.1-SNAPSHOT.jar app.jar

# Set environment variables
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV MAIL_USERNAME=${MAIL_USERNAME}
ENV MAIL_PASSWORD=${MAIL_PASSWORD}

# Expose the port that the application will run on
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
