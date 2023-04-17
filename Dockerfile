FROM gradle:jdk8 as builder

ARG DISCORD_BOT_TOKEN
ARG MONGO_CONNECTION_STRING
ARG SPRING_PROFILES_ACTIVE

ENV DISCORD_BOT_TOKEN=$DISCORD_BOT_TOKEN
ENV MONGO_CONNECTION_STRING=$MONGO_CONNECTION_STRING
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

COPY . .

RUN ./gradlew bootjar

FROM eclipse-temurin:8u362-b09-jdk
COPY ./build/libs/ready-botlin-1.0.0.jar ./ready-botlin.jar

RUN echo "java -XX:MaxRAMPercentage=75.0 -jar ready-botlin.jar" > entrypoint.sh && chmod +x entrypoint.sh

ENTRYPOINT ./entrypoint.sh