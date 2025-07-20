# Use official OpenJDK image as base
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy built jar to container
COPY target/moneymanager-0.0.1-SNAPSHOT.jar moneymanager-v1.0.jar

# Expose the port your app runs on
EXPOSE 8080

# Set environment variable for active profile (if needed)
ENV SPRING_PROFILES_ACTIVE=prod

# Run the jar file
ENTRYPOINT ["java", "-jar", "moneymanager-v1.0.jar"]
