FROM maven:3.6.3-openjdk-8-slim as maven

COPY ./pom.xml ./pom.xml

RUN mvn --legacy-local-repository dependency:resolve-plugins dependency:go-offline -B

# dry run to fill parts of the cache that go-offline misses
RUN mvn --legacy-local-repository package

COPY ./src ./src

RUN mvn -o --legacy-local-repository package

# Alpine Linux with OpenJDK JRE
FROM openjdk:8-jre-alpine
# copy WAR into image
COPY  --from=maven target/pbworks-bak-*-jar-with-dependencies.jar /app.jar

# run application with this command line 
CMD ["/usr/bin/java", "-jar", "/app.jar", "/export"]
