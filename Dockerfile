FROM eclipse-temurin:20-jdk-alpine

WORKDIR /app

COPY target/chessGame-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
