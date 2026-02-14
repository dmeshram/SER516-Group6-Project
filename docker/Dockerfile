#Stage 1: Build 
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first for dependency caching
COPY pom.xml ./

# Install Maven (the project does not include a Maven wrapper)
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the project (skip tests — they run in CI)
RUN mvn package -B -DskipTests

#Stage 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/ser516-group6-metrics-1.0.0.jar app.jar

# Default entry point — runs the scanner on /project (mount your code there)
ENTRYPOINT ["java", "-cp", "app.jar", "edu.asu.ser516.metrics.ScannerMain"]

# Default argument: scan /project directory
CMD ["/project"]
