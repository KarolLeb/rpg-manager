# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY backend-auth/pom.xml ./backend-auth/
COPY backend-core/pom.xml ./backend-core/
COPY backend-admin/pom.xml ./backend-admin/
COPY backend-common/pom.xml ./backend-common/
# Cache dependencies
RUN mvn install -N -DskipTests
RUN mvn dependency:go-offline -pl backend-core -am

COPY backend-common/src ./backend-common/src
COPY backend-core/src ./backend-core/src
RUN mvn clean package -pl backend-core -am -DskipTests

# Stage 2: Development
FROM maven:3.9.6-eclipse-temurin-21 AS development
WORKDIR /app

# Install JaCoCo agent
RUN curl -L https://repo1.maven.org/maven2/org/jacoco/org.jacoco.agent/0.8.12/org.jacoco.agent-0.8.12-runtime.jar -o /app/jacocoagent.jar

COPY pom.xml .
COPY backend-auth/pom.xml ./backend-auth/
COPY backend-core/pom.xml ./backend-core/
COPY backend-admin/pom.xml ./backend-admin/
COPY backend-common/pom.xml ./backend-common/
# Cache dependencies
RUN mvn install -N -DskipTests
RUN mvn dependency:go-offline -pl backend-core -am

COPY backend-common/src ./backend-common/src
COPY backend-core/src ./backend-core/src
RUN mvn install -pl backend-common -DskipTests
RUN mvn package -pl backend-core -am -DskipTests
WORKDIR /app/backend-core
CMD ["sh", "-c", "java -javaagent:/app/jacocoagent.jar=address=*,port=6300,output=tcpserver -jar target/*.jar --server.address=0.0.0.0"]

# Stage 3: Production
FROM eclipse-temurin:21-jre-alpine AS production
WORKDIR /app
COPY --from=build /app/backend-core/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]