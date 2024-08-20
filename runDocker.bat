@echo off
setlocal

REM Set the path for Docker Desktop installer
set "DOCKER_INSTALLER_URL=https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe"
set "DOCKER_INSTALLER=DockerDesktopInstaller.exe"

REM Download Docker Desktop installer using curl
echo Downloading Docker Desktop installer...
cmd.exe /c curl -L -o %DOCKER_INSTALLER% %DOCKER_INSTALLER_URL%

REM Check if Docker Desktop installer was downloaded
if not exist "%DOCKER_INSTALLER%" (
    echo Docker Desktop installer could not be downloaded.
    exit /b 1
)

REM Install Docker Desktop
echo Installing Docker Desktop...
start /wait "" "%DOCKER_INSTALLER%" install

REM Check if Docker is installed
where docker >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo Docker Desktop installation failed. Please install Docker manually and try again.
    exit /b 1
)

REM Verify Docker installation
docker --version

REM Create Dockerfile for JavaFX application
echo Creating Dockerfile for JavaFX application...

(
echo FROM ubuntu:20.04
echo.
echo RUN apt-get update && apt-get install -y ^\
echo     openjdk-17-jdk ^\
echo     wget ^\
echo     unzip ^\
echo     libgtk-3-0 ^\
echo     libcanberra-gtk-module ^\
echo     libcanberra-gtk3-module
echo.
echo RUN wget https://services.gradle.org/distributions/gradle-8.7-bin.zip -P /tmp ^\
echo     && unzip -d /opt/gradle /tmp/gradle-8.7-bin.zip ^\
echo     && rm /tmp/gradle-8.7-bin.zip
echo.
echo ENV GRADLE_HOME=/opt/gradle/gradle-8.7
echo ENV PATH=%GRADLE_HOME%/bin:%PATH%
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
echo RUN chmod +x /usr/src/app/gradlew
echo.
echo CMD ["./gradlew", "run"]
) > Dockerfile

REM Build Docker image
echo Building Docker image...
docker build -t javafx-app .

REM Run JavaFX application in Docker
echo Running JavaFX application in Docker...

REM Map the Windows user directory to the container
docker run -it --rm ^
    -e DISPLAY=%DISPLAY% ^
    -v C:/Users:/mnt/host_users ^
    javafx-app

endlocal
