@echo off

rem Run gradle to generate jlinkZip
echo Building application...
.\gradlew jlinkZip

rem Unzip the distribution to a temporary directory
echo Extracting application...
powershell Expand-Archive -Path .\build\distributions\app-windows.zip -DestinationPath C:\Temp\app-windows -Force

rem Change to the extracted directory and run the application
echo Launching application...
cd C:\Temp\app-windows\image\bin
app.bat

rem Clean up temporary files if needed
rem rd /s /q C:\Temp\app-windows  # Uncomment if you want to delete the extracted files after running
