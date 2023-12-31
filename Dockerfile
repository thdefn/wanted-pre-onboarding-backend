FROM openjdk:17-alpine
ARG JAR_FILE=/build/libs/wanted-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-jar","/app.jar"]