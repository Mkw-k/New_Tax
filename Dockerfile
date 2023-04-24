FROM adoptopenjdk/openjdk11
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE_PATH=target/*.jar
COPY ${JAR_FILE_PATH} hometax-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "hometax-0.0.1-SNAPSHOT.jar"]