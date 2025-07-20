# Stage 1: Build the application
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for better caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/moneymanager-0.0.1-SNAPSHOT.jar moneymanager-v1.0.jar

# Set environment variable for active profile
ENV SPRING_PROFILES_ACTIVE=prod

# Run the jar file with memory optimization
ENTRYPOINT ["java", "-Xmx512m", "-jar", "moneymanager-v1.0.jar"]