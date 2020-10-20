FROM openjdk:15 AS builder

ADD . /source

WORKDIR /source

RUN ./gradlew clean build --no-daemon

FROM openjdk:15-slim-buster

RUN adduser --disabled-password --gecos '' micronaut

ARG JAR_FILE=/source/build/libs/*all.jar
COPY --chown=micronaut --from=builder ${JAR_FILE} /home/micronaut/app.jar

WORKDIR /home/micronaut
USER micronaut

ENTRYPOINT ["java","-jar","./app.jar"]