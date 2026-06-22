# language: dockerfile
# filepath: /Users/waldrafft/Documents/projects/personal/book-manager/devops/dev/Dockerfile
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /workspace/app

# copy only what is needed to download dependencies first (speeds rebuilds)
COPY pom.xml mvnw ./
COPY .mvn .mvn

# download dependencies
RUN mvn -B -ntp dependency:go-offline

# copy source and build
COPY src ./src
# use the wrapper if present; fallback to 'mvn' otherwise
RUN if [ -x ./mvnw ]; then ./mvnw -B -DskipTests package; else mvn -B -DskipTests package; fi

# runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app

# copy the built jar (first matching jar in target)
COPY --from=builder /workspace/app/target/*.jar /app/app.jar

EXPOSE 8443
ENTRYPOINT ["java","-jar","/app/app.jar"]