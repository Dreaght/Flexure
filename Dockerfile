FROM ubuntu:20.04

RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    wget \
    unzip \
    libgtk-3-0 \
    libcanberra-gtk-module \
    libcanberra-gtk3-module

RUN wget https://services.gradle.org/distributions/gradle-8.7-bin.zip -P /tmp \
    && unzip -d /opt/gradle /tmp/gradle-8.7-bin.zip \
    && rm /tmp/gradle-8.7-bin.zip

ENV GRADLE_HOME=/opt/gradle/gradle-8.7
ENV PATH=${GRADLE_HOME}/bin:${PATH}

WORKDIR /usr/src/app

COPY gradlew /usr/src/app/gradlew
COPY gradlew.bat /usr/src/app/gradlew.bat
COPY gradle /usr/src/app/gradle
COPY build.gradle /usr/src/app/build.gradle
COPY settings.gradle /usr/src/app/settings.gradle
COPY src /usr/src/app/src

COPY aim1.dxf /usr/src/app/

RUN chmod +x /usr/src/app/gradlew

CMD ["./gradlew", "run"]
