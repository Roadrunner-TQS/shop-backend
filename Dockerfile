FROM maven:3-eclipse-temurin-17-alpine AS builder
WORKDIR /app
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
COPY pom.xml pom.xml
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean install

FROM builder AS base
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar
CMD ["java", "-jar", "app.jar"]