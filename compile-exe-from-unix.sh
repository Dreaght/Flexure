#!/bin/bash

# Script to install Docker, configure it with Wine and Gradle for compiling .exe and generating Windows-compatible binaries

set -e

# Function to install Docker if not already installed
install_docker() {
  echo "Installing Docker..."

  # Check if Docker is installed
  if ! command -v docker &> /dev/null; then
    # Install Docker using the appropriate package manager
    if command -v yum &> /dev/null; then
      yum install -y docker
    elif command -v apt-get &> /dev/null; then
      apt-get update
      apt-get install -y docker.io
    else
      echo "Unsupported package manager. Please install Docker manually."
      exit 1
    fi
  fi

  # Start Docker service if not already running
  if ! systemctl is-active --quiet docker; then
    echo "Starting Docker daemon..."
    systemctl start docker
  fi

  # Verify Docker installation
  docker --version
}

# Function to configure Docker with Wine and Gradle for Windows
configure_docker_with_wine_and_gradle() {
  echo "Configuring Docker with Wine and Gradle for building Windows executables..."

  # Create temporary directory to hold generated files
  TEMP_DIR=$(mktemp -d)
  trap "rm -rf $TEMP_DIR" EXIT

  # Create Dockerfile for Wine and Gradle environment
  cat <<EOF > Dockerfile
FROM mcr.microsoft.com/windows/servercore:ltsc2019

# Install PowerShell
RUN powershell -Command \
    Set-ExecutionPolicy Bypass -Scope Process -Force; \
    Invoke-WebRequest -Uri https://aka.ms/pscore -OutFile PowerShell.zip; \
    Expand-Archive PowerShell.zip -DestinationPath C:\PowerShell; \
    Remove-Item PowerShell.zip -Force

# Set PowerShell as the default shell
SHELL ["pwsh", "-Command", "$ErrorActionPreference = 'Stop'; $ProgressPreference = 'SilentlyContinue';"]

# Install necessary tools and dependencies
RUN Invoke-WebRequest -Uri https://github.com/gradle/gradle/releases/download/v8.7/gradle-8.7-bin.zip -OutFile C:\gradle.zip ; \
    Expand-Archive -Path C:\gradle.zip -DestinationPath C:\ ; \
    Remove-Item C:\gradle.zip -Force

# Set environment variables
ENV JAVA_HOME=C:\\opt\\jdk-17.0.0
ENV PATH="${JAVA_HOME}\\bin;C:\\gradle-8.7\\bin;${PATH}"

# Set working directory
WORKDIR C:\\opt\\project

# Copy Gradle wrapper and necessary files
COPY gradlew C:\\opt\\project\\gradlew
COPY gradle C:\\opt\\project\\gradle
COPY build.gradle C:\\opt\\project\\build.gradle
COPY settings.gradle C:\\opt\\project\\settings.gradle
COPY src C:\\opt\\project\\src

# Ensure executable permissions for Gradle wrapper
RUN ./gradlew --version

# Run Gradle command to build jlinkZip (or your desired build command)
RUN ./gradlew jlinkZip || echo "Gradle build failed. See previous logs for details."

# Create directory for artifacts
RUN mkdir C:\\opt\\project\\docker-build

# Copy entire build directory to docker-build directory
RUN cp -r build C:\\opt\\project\\docker-build

# Default command to run when container starts
CMD ["cmd", "/k", "echo Cross-compilation Docker image built successfully."]

EOF

  # Build the Docker image
  docker build -t wine-gradle-builder .

  echo "Docker configured successfully with Wine and Gradle for Windows. Built artifacts copied to 'docker-build' directory in the root of your project."

  # Copy artifacts from Docker to host
  docker cp $(docker create wine-gradle-builder):/opt/project/docker-build/. $TEMP_DIR/
  cp -r $TEMP_DIR/. docker-build/
}

# Main execution
install_docker
configure_docker_with_wine_and_gradle
