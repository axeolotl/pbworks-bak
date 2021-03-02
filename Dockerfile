# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine
# copy WAR into image
COPY target/pbworks-bak-1.0-jar-with-dependencies.jar /app.jar
# run application with this command line 
CMD ["/usr/bin/java", "-jar", "/app.jar", "/export"]
