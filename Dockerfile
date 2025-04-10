# Use OpenJDK 17 base image
FROM openjdk:17-jdk-slim

# Set environment variables
ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="" \
    PORT=8080

# Set the working directory inside the container
WORKDIR /app

# Copy the built .jar file into the container
COPY target/seniorProject-0.0.1-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
