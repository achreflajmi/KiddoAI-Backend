# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file
COPY target/KiddoAI-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8081

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
