FROM eclipse-temurin:17-jdk-alpine AS build
LABEL authors="bravin"

WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY backend-service/pom.xml backend-service/

COPY backend-service/src backend-service/src
RUN mvn clean compile install
RUN mvn clean package -pl backend-service -am -DskipTests

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp

# Environment variables
ENV JAR_VERSION=teamsphere-backend

# Copy the JAR file with version
COPY --from=build /workspace/app/target/*.jar ${JAR_VERSION}.jar

# Run the jar file with version
CMD ["sh", "-c", "java  -jar ${JAR_VERSION}.jar"]
