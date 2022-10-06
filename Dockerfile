FROM openjdk:17-jdk-alpine
COPY build/libs/opticket-api-0.0.1-SNAPSHOT.jar opticket-api.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /opticket-api.jar"]