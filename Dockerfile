FROM ubuntu:20.04

# Install necessary tools and dependencies
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    wget \
    unzip \
    libgtk-3-0 \
    libcanberra-gtk-module \
    libcanberra-gtk3-module

# Install Gradle
RUN wget https://services.gradle.org/distributions/gradle-8.7-bin.zip -P /tmp \
    && unzip -d /opt/gradle /tmp/gradle-8.7-bin.zip \
    && rm /tmp/gradle-8.7-bin.zip

# Set environment variables
ENV GRADLE_HOME=/opt/gradle/gradle-8.7
ENV PATH=${GRADLE_HOME}/bin:${PATH}

# Set working directory
WORKDIR /usr/src/app

# Copy Gradle wrapper and necessary files
COPY gradlew /usr/src/app/gradlew
COPY gradlew.bat /usr/src/app/gradlew.bat
COPY gradle /usr/src/app/gradle
COPY build.gradle /usr/src/app/build.gradle
COPY settings.gradle /usr/src/app/settings.gradle
COPY src /usr/src/app/src

# Copy user-provided file
COPY in2.dxf /usr/src/app/

# Ensure executable permissions for Gradle wrapper
RUN chmod +x /usr/src/app/gradlew

# Run the JavaFX application
CMD ["./gradlew", "run"]
