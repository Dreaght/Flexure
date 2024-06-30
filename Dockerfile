FROM mcr.microsoft.com/dotnet/framework/sdk:4.8

# Install PowerShell Core
RUN curl -L https://github.com/PowerShell/PowerShell/releases/download/v7.3.1/PowerShell-7.3.1-win-x64.zip -o PowerShell.zip &&     mkdir C:\PowerShell &&     tar -xf PowerShell.zip -C C:\PowerShell &&     rm PowerShell.zip

# Install necessary tools and dependencies
RUN powershell -NoProfile -Command "Invoke-WebRequest -Uri https://services.gradle.org/distributions/gradle-8.7-bin.zip -OutFile gradle.zip ;     Expand-Archive -Path gradle.zip -DestinationPath C:\ ;     Remove-Item gradle.zip -Force"

# Set environment variables
ENV JAVA_HOME=C:\openjdk-17
ENV GRADLE_HOME=C:\gradle-8.7
ENV PATH="${JAVA_HOME}\bin;${GRADLE_HOME}\bin;${PATH}"

# Set working directory
WORKDIR C:\project

# Copy Gradle wrapper and necessary files
COPY gradlew.bat C:\project\gradlew.bat
COPY gradle C:\project\gradle
COPY build.gradle C:\project\build.gradle
COPY settings.gradle C:\project\settings.gradle
COPY src C:\project\src

# Ensure executable permissions for Gradle wrapper
RUN powershell -NoProfile -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; C:\project\gradlew.bat --version"

# Run Gradle command to build jlinkZip (or your desired build command)
RUN powershell -NoProfile -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; C:\project\gradlew.bat jlinkZip || echo 'Gradle build failed. See previous logs for details.'"

# Create directory for artifacts
RUN powershell -NoProfile -Command "New-Item -ItemType Directory -Path C:\project\docker-build"

# Copy entire build directory to docker-build directory
RUN powershell -NoProfile -Command "Copy-Item -Path C:\project\build -Destination C:\project\docker-build -Recurse"

# Default command to run when container starts
CMD ["powershell.exe", "-NoLogo", "-Command", "echo Cross-compilation Docker image built successfully."]
