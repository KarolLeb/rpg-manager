# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY backend-auth/pom.xml ./backend-auth/
COPY backend-core/pom.xml ./backend-core/
COPY backend-admin/pom.xml ./backend-admin/
COPY checkstyle.xml .
RUN mvn dependency:go-offline -pl backend-admin -am
COPY backend-admin/src ./backend-admin/src
RUN mvn clean package -pl backend-admin -DskipTests

# Stage 2: Development
FROM maven:3.9.6-eclipse-temurin-21 AS development
WORKDIR /app
COPY pom.xml .
COPY backend-auth/pom.xml ./backend-auth/
COPY backend-core/pom.xml ./backend-core/
COPY backend-admin/pom.xml ./backend-admin/
COPY checkstyle.xml .
RUN mvn dependency:go-offline -pl backend-admin -am
COPY backend-admin/src ./backend-admin/src
WORKDIR /app/backend-admin
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-Dserver.address=0.0.0.0'"]

# Stage 3: Production
FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
COPY --from=build /app/backend-admin/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
