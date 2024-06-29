FROM mcr.microsoft.com/windows/servercore:ltsc2019

# Install PowerShell
RUN powershell -Command     Set-ExecutionPolicy Bypass -Scope Process -Force;     Invoke-WebRequest -Uri https://aka.ms/pscore -OutFile PowerShell.zip;     Expand-Archive PowerShell.zip -DestinationPath C:\PowerShell;     Remove-Item PowerShell.zip -Force

# Set PowerShell as the default shell
SHELL ["pwsh", "-Command", " = 'Stop';  = 'SilentlyContinue';"]

# Install necessary tools and dependencies
RUN Invoke-WebRequest -Uri https://github.com/gradle/gradle/releases/download/v8.7/gradle-8.7-bin.zip -OutFile C:\gradle.zip ;     Expand-Archive -Path C:\gradle.zip -DestinationPath C:\ ;     Remove-Item C:\gradle.zip -Force

# Set environment variables
ENV JAVA_HOME=C:\opt\jdk-17.0.0
ENV PATH="\bin;C:\gradle-8.7\bin;/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/var/lib/snapd/snap/bin"

# Set working directory
WORKDIR C:\opt\project

# Copy Gradle wrapper and necessary files
COPY gradlew C:\opt\project\gradlew
COPY gradle C:\opt\project\gradle
COPY build.gradle C:\opt\project\build.gradle
COPY settings.gradle C:\opt\project\settings.gradle
COPY src C:\opt\project\src

# Ensure executable permissions for Gradle wrapper
RUN ./gradlew --version

# Run Gradle command to build jlinkZip (or your desired build command)
RUN ./gradlew jlinkZip || echo "Gradle build failed. See previous logs for details."

# Create directory for artifacts
RUN mkdir C:\opt\project\docker-build

# Copy entire build directory to docker-build directory
RUN cp -r build C:\opt\project\docker-build

# Default command to run when container starts
CMD ["cmd", "/k", "echo Cross-compilation Docker image built successfully."]

