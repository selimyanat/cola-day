FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /opt/coladay
ARG JAR_FILE=target/*.jar
# Copy *.jar /opt/coladay/coladay.jar
COPY ${JAR_FILE} coladay.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000","-jar","coladay.jar"]
EXPOSE 8080
EXPOSE 8081