#!/bin/bash

set -e

# Function to install Docker on Linux
install_docker_linux() {
  echo "Installing Docker on Linux..."
  if ! command -v docker &> /dev/null; then
    if command -v yum &> /dev/null; then
      sudo yum install -y docker
    elif command -v apt-get &> /dev/null; then
      sudo apt-get update
      sudo apt-get install -y docker.io
    else
      echo "Unsupported package manager. Please install Docker manually."
      exit 1
    fi
  fi

  if ! systemctl is-active --quiet docker; then
    echo "Starting Docker daemon..."
    sudo systemctl start docker
  fi

  sudo usermod -aG docker $(whoami)

  docker --version
}

# Function to install Docker on macOS
install_docker_mac() {
  echo "Installing Docker on macOS..."
  if ! command -v docker &> /dev/null; then
    if ! command -v brew &> /dev/null; then
      echo "Homebrew is not installed. Installing Homebrew..."
      /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    fi
    echo "Installing Docker Desktop using Homebrew..."
    brew install --cask docker
    echo "Please start Docker Desktop manually and ensure it's running."
  fi
  docker --version
}

# Function to install Docker on Windows
install_docker_windows() {
  echo "Installing Docker on Windows..."
  if ! command -v docker &> /dev/null; then
    echo "Downloading Docker Desktop installer..."
    curl -LO https://desktop.docker.com/win/stable/Docker%20Desktop%20Installer.exe
    echo "Running Docker Desktop installer..."
    start /wait "" "Docker%20Desktop%20Installer.exe" install
    echo "Docker Desktop installed. Please start Docker Desktop manually and ensure it's running."
  fi
  docker --version
}

# Function to build Docker image and run the JavaFX application
run_javafx_app() {
  echo "Creating Dockerfile for JavaFX application..."

  read -p "Enter the path to the file you want to copy into the Docker container: " file_path

  if [[ ! -f "$file_path" ]]; then
    echo "File not found!"
    exit 1
  fi

  file_name=$(basename "$file_path")

  cat <<EOF > Dockerfile
FROM ubuntu:20.04

RUN apt-get update && apt-get install -y \\
    openjdk-17-jdk \\
    wget \\
    unzip \\
    libgtk-3-0 \\
    libcanberra-gtk-module \\
    libcanberra-gtk3-module

RUN wget https://services.gradle.org/distributions/gradle-8.7-bin.zip -P /tmp \\
    && unzip -d /opt/gradle /tmp/gradle-8.7-bin.zip \\
    && rm /tmp/gradle-8.7-bin.zip

ENV GRADLE_HOME=/opt/gradle/gradle-8.7
ENV PATH=\${GRADLE_HOME}/bin:\${PATH}

WORKDIR /usr/src/app

COPY gradlew /usr/src/app/gradlew
COPY gradlew.bat /usr/src/app/gradlew.bat
COPY gradle /usr/src/app/gradle
COPY build.gradle /usr/src/app/build.gradle
COPY settings.gradle /usr/src/app/settings.gradle
COPY src /usr/src/app/src

COPY $file_name /usr/src/app/

RUN chmod +x /usr/src/app/gradlew

CMD ["./gradlew", "run"]
EOF

  cp "$file_path" .

  docker build -t javafx-app .

  rm "$file_name"

  echo "Docker image built successfully. Running the JavaFX application..."

  if [[ -z "$DISPLAY" ]]; then
    export DISPLAY=:0
  fi

  if [[ "$OSTYPE" == "linux-gnu"* || "$OSTYPE" == "darwin"* ]]; then
    xhost +local:docker

    docker run -it --rm \
      -e DISPLAY=$DISPLAY \
      -v /tmp/.X11-unix:/tmp/.X11-unix \
      javafx-app

    xhost -local:docker

  elif [[ "$OSTYPE" == "msys"* ]]; then
    docker run -it --rm \
      -e DISPLAY=$DISPLAY \
      -v /c/Users:/c/Users \
      javafx-app
  fi
}

# Main execution
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
  install_docker_linux
elif [[ "$OSTYPE" == "darwin"* ]]; then
  install_docker_mac
elif [[ "$OSTYPE" == "msys"* ]]; then
  install_docker_windows
else
  echo "Unsupported OS. Please install Docker manually."
  exit 1
fi

run_javafx_app
