#!/bin/bash

REPO_URL="https://github.com/Dreaght/Flexure/archive/refs/heads/master.zip"
ZIP_FILE="Flexure-master.zip"
DIR_NAME="Flexure-master"

if [ -d "$DIR_NAME" ]; then
    echo "Removing existing directory $DIR_NAME..."
    rm -rf "$DIR_NAME"
    if [ $? -ne 0 ]; then
        echo "Failed to remove directory $DIR_NAME."
        exit 1
    fi
fi

if [ -f "$ZIP_FILE" ]; then
    echo "Removing existing ZIP file $ZIP_FILE..."
    rm -f "$ZIP_FILE"
    if [ $? -ne 0 ]; then
        echo "Failed to remove ZIP file $ZIP_FILE."
        exit 1
    fi
fi

if command -v curl &> /dev/null; then
    echo "Downloading repository using curl..."
    curl -L -o "$ZIP_FILE" "$REPO_URL"
    if [ $? -ne 0 ]; then
        echo "Failed to download using curl."
        exit 1
    fi
elif command -v wget &> /dev/null; then
    echo "Downloading repository using wget..."
    wget -O "$ZIP_FILE" "$REPO_URL"
    if [ $? -ne 0 ]; then
        echo "Failed to download using wget."
        exit 1
    fi
else
    echo "Neither curl nor wget is available. Please install one of them."
    exit 1
fi

if [ ! -f "$ZIP_FILE" ]; then
    echo "Download failed or ZIP file not found."
    exit 1
fi

echo "Extracting the ZIP file..."
if ! unzip -q "$ZIP_FILE"; then
    echo "Failed to unzip $ZIP_FILE. It might be corrupted or not a valid ZIP file."
    exit 1
fi

cd "$DIR_NAME" || { echo "Failed to enter directory $DIR_NAME"; exit 1; }

echo "Running the application..."
if ! ./gradlew run; then
    echo "Failed to run the application."
    exit 1
fi
