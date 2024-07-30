@echo off
setlocal enabledelayedexpansion


call :run_javafx_app

goto :eof

:: Function to build Docker image and run the JavaFX application
:run_javafx_app
echo Creating Dockerfile for JavaFX application...

set /p file_path="Enter the path to the file you want to copy into the Docker container: "

if not exist "%file_path%" (
    echo File not found!
    exit /b 1
)

for %%I in ("%file_path%") do set file_name=%%~nxI

(
    echo FROM ubuntu:20.04
    echo.
    echo RUN apt-get update ^&^& apt-get install -y \^
    echo     openjdk-17-jdk \^
    echo     wget \^
    echo     unzip \^
    echo     libgtk-3-0 \^
    echo     libcanberra-gtk-module \^
    echo     libcanberra-gtk3-module
    echo.
    echo RUN wget https://services.gradle.org/distributions/gradle-8.7-bin.zip -P /tmp \^
    echo     ^&^& unzip -d /opt/gradle /tmp/gradle-8.7-bin.zip \^
    echo     ^&^& rm /tmp/gradle-8.7-bin.zip
    echo.
    echo ENV GRADLE_HOME=/opt/gradle/gradle-8.7
    echo ENV PATH=\${GRADLE_HOME}/bin:\${PATH}
    echo.
    echo WORKDIR /usr/src/app
    echo.
    echo COPY gradlew /usr/src/app/gradlew
    echo COPY gradlew.bat /usr/src/app/gradlew.bat
    echo COPY gradle /usr/src/app/gradle
    echo COPY build.gradle /usr/src/app/build.gradle
    echo COPY settings.gradle /usr/src/app/settings.gradle
    echo COPY src /usr/src/app/src
    echo.
    echo COPY %file_name% /usr/src/app/
    echo.
    echo RUN chmod +x /usr/src/app/gradlew
    echo.
    echo CMD ["./gradlew", "run"]
) > Dockerfile

copy "%file_path%" .

docker build -t javafx-app .

del "%file_name%"

echo Docker image built successfully. Running the JavaFX application...

:: Replace this command with the actual command to launch your JavaFX application inside the container
docker run -it --rm ^
    -v /c/Users:/c/Users ^
    javafx-app

goto :eof
