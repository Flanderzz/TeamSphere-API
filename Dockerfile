FROM eclipse-temurin:17-jdk-alpine AS build
LABEL authors="bravin"

WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Fix executable permission for mvnw
RUN chmod +x mvnw
# Package the application skipping tests
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp

# Environment variables
ENV JAR_VERSION=teamsphere-backend

# Copy the JAR file with version
COPY --from=build /workspace/app/target/*.jar ${JAR_VERSION}.jar

# Run the jar file with version
CMD ["sh", "-c", "java  -jar ${JAR_VERSION}.jar"]
