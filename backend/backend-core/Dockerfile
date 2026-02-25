# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY backend-auth/pom.xml ./backend-auth/
COPY backend-core/pom.xml ./backend-core/
COPY backend-admin/pom.xml ./backend-admin/
COPY backend-common/pom.xml ./backend-common/
COPY backend-common/src ./backend-common/src
COPY backend-core/src ./backend-core/src
RUN mvn install -N -DskipTests
RUN mvn clean package -pl backend-core -am -DskipTests

# Stage 2: Development
FROM maven:3.9.6-eclipse-temurin-21 AS development
WORKDIR /app
COPY pom.xml .
COPY backend-auth/pom.xml ./backend-auth/
COPY backend-core/pom.xml ./backend-core/
COPY backend-admin/pom.xml ./backend-admin/
COPY backend-common/pom.xml ./backend-common/
COPY backend-common/src ./backend-common/src
COPY backend-core/src ./backend-core/src
RUN mvn install -N -DskipTests
RUN mvn install -pl backend-common -DskipTests
WORKDIR /app/backend-core
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-Dserver.address=0.0.0.0'"]

# Stage 3: Production
FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
COPY --from=build /app/backend-core/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]