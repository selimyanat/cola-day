FROM adoptopenjdk/openjdk11:alpine-jre
#adding a non-root user and group to the container’s system
RUN addgroup -g 2000 -S appuser && adduser -u 2000 -S appuser -G appuser -h /home/appuser
#setting our root directory because we cannot put contents on root now
WORKDIR /home/appuser
#copying executable jar there from target folder
COPY target/*.jar coladay.jar
#changing owner and permissions of that jar
RUN chown appuser:appuser coladay.jar && chmod 700 coladay.jar
#specifying newly created user
USER appuser
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000","-jar","coladay.jar"]
EXPOSE 8080
EXPOSE 8081