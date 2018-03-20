FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD credit-card-recommendation-service-* app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]