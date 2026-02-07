FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/backup-tool-1.0-SNAPSHOT.jar /app/backup-tool.jar

ENTRYPOINT ["java", "-jar", "/app/backup-tool.jar"]

# if you prefer the scrip runner( I recommend for local dev testing)
#FROM eclipse-temurin:21-jre

#WORKDIR /app

#COPY backup.sh /app/backup.sh
#COPY target/backup-tool-1.0-SNAPSHOT.jar /app/backup-tool.jar

#RUN chmod +x /app/backup.sh

#ENTRYPOINT ["/app/backup.sh"]
