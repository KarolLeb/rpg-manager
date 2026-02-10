# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY backend-auth/pom.xml ./backend-auth/
COPY backend-core/pom.xml ./backend-core/
COPY backend-admin/pom.xml ./backend-admin/
COPY checkstyle.xml .
RUN mvn dependency:go-offline -pl backend-auth -am
COPY backend-auth/src ./backend-auth/src
RUN mvn clean package -pl backend-auth -DskipTests

# Stage 2: Development
FROM maven:3.9.6-eclipse-temurin-21 AS development
WORKDIR /app
COPY pom.xml .
COPY backend-auth/pom.xml ./backend-auth/
COPY backend-core/pom.xml ./backend-core/
COPY backend-admin/pom.xml ./backend-admin/
COPY checkstyle.xml .
RUN mvn dependency:go-offline -pl backend-auth -am
COPY backend-auth/src ./backend-auth/src
WORKDIR /app/backend-auth
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-Dserver.address=0.0.0.0'"]

# Stage 3: Production
FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
COPY --from=build /app/backend-auth/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
