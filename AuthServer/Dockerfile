FROM ubuntu

WORKDIR /app

RUN apt update && apt upgrade -y
RUN apt install openjdk-17-jdk openjdk-17-jre -y
RUN apt install wget unzip -y
RUN wget -c https://services.gradle.org/distributions/gradle-7.4.2-bin.zip
RUN mkdir /opt/gradle
RUN unzip -d /opt/gradle gradle-7.4.2-bin.zip

ENV PATH $PATH:/opt/gradle/gradle-7.4.2/bin

COPY /src ./src
COPY gradlew gradlew
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle

RUN gradle bootJar

CMD ["java", "-jar", "build/libs/AuthServer-0.0.1-SNAPSHOT.jar"]