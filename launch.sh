#!/bin/bash

# Run gradle to generate jlinkZip
echo "Building application..."
./gradlew jlinkZip

# Unzip the distribution to a temporary directory
echo "Extracting application..."
unzip -o build/distributions/app-linux.zip -d /tmp/app-linux

# Change to the extracted directory and run the application
echo "Launching application..."
cd /tmp/app-linux/image/bin
./app

# Clean up temporary files if needed
# rm -rf /tmp/app-linux  # Uncomment if you want to delete the extracted files after running
