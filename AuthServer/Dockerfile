FROM gradle:6.7.1-jdk11 as builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle clean build

FROM openjdk:11-jre-slim

ENV APP_HOME=/app
ENV APP_JAR=AuthServer-0.0.1-SNAPSHOT.jar

RUN mkdir -p $APP_HOME

COPY --from=builder /app/build/libs/$APP_JAR $APP_HOME/$APP_JAR

WORKDIR $APP_HOME

CMD ["java", "-jar", "$APP_JAR"]