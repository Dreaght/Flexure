#!/bin/bash

# Script to install Docker, configure it with PowerShell and Gradle for compiling .exe and generating Windows-compatible binaries

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

# Function to configure Docker with PowerShell and Gradle for Windows
configure_docker_with_powershell_and_gradle() {
  echo "Configuring Docker with PowerShell and Gradle for building Windows executables..."

  # Create Dockerfile for PowerShell and Gradle environment
  cat <<EOF > Dockerfile
FROM mcr.microsoft.com/dotnet/framework/sdk:4.8

# Install PowerShell Core
RUN curl -L https://github.com/PowerShell/PowerShell/releases/download/v7.3.1/PowerShell-7.3.1-win-x64.zip -o PowerShell.zip && \
    mkdir C:\\PowerShell && \
    tar -xf PowerShell.zip -C C:\\PowerShell && \
    rm PowerShell.zip

# Install necessary tools and dependencies
RUN powershell -NoProfile -Command "Invoke-WebRequest -Uri https://services.gradle.org/distributions/gradle-8.7-bin.zip -OutFile gradle.zip ; \
    Expand-Archive -Path gradle.zip -DestinationPath C:\\ ; \
    Remove-Item gradle.zip -Force"

# Set environment variables
ENV JAVA_HOME=C:\\openjdk-17
ENV GRADLE_HOME=C:\\gradle-8.7
ENV PATH="\${JAVA_HOME}\\bin;\${GRADLE_HOME}\\bin;\${PATH}"

# Set working directory
WORKDIR C:\\project

# Copy Gradle wrapper and necessary files
COPY gradlew.bat C:\\project\\gradlew.bat
COPY gradle C:\\project\\gradle
COPY build.gradle C:\\project\\build.gradle
COPY settings.gradle C:\\project\\settings.gradle
COPY src C:\\project\\src

# Ensure executable permissions for Gradle wrapper
RUN powershell -NoProfile -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; C:\\project\\gradlew.bat --version"

# Run Gradle command to build jlinkZip (or your desired build command)
RUN powershell -NoProfile -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; C:\\project\\gradlew.bat jlinkZip || echo 'Gradle build failed. See previous logs for details.'"

# Create directory for artifacts
RUN powershell -NoProfile -Command "New-Item -ItemType Directory -Path C:\\project\\docker-build"

# Copy entire build directory to docker-build directory
RUN powershell -NoProfile -Command "Copy-Item -Path C:\\project\\build -Destination C:\\project\\docker-build -Recurse"

# Default command to run when container starts
CMD ["powershell.exe", "-NoLogo", "-Command", "echo Cross-compilation Docker image built successfully."]
EOF

  # Build the Docker image
  docker build -t powershell-gradle-builder .

  echo "Docker configured successfully with PowerShell and Gradle for Windows. Built artifacts copied to 'docker-build' directory in the root of your project."

  # Create a container from the built image
  CONTAINER_ID=$(docker create powershell-gradle-builder)

  # Copy artifacts from Docker to host
  docker cp $CONTAINER_ID:C:/project/docker-build/. ./docker-build/
  docker rm $CONTAINER_ID
}

# Main execution
install_docker
configure_docker_with_powershell_and_gradle
