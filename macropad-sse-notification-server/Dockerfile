FROM eclipse-temurin:21-jre-alpine

EXPOSE 2020

WORKDIR /application

COPY ./target/*.jar app.jar

ARG USER_NAME=containeruser
ARG GROUP_NAME=containergroup
RUN addgroup -g 1001 -S $GROUP_NAME \
    && adduser -u 1000 -S $USER_NAME -G $GROUP_NAME \
    && chown -R $USER_NAME:$GROUP_NAME /application
USER $USER_NAME

ENTRYPOINT exec java -jar /application/app.jar
