@echo off
setlocal

set BRANCH=master
set REPO_URL="https://github.com/Dreaght/Flexure/archive/refs/heads/%BRANCH%.zip"
set ZIP_FILE="Flexure-%BRANCH%.zip"
set DIR_NAME="Flexure-%BRANCH%"

echo Removing existing directory if it exists...
if exist %DIR_NAME% rd /s /q %DIR_NAME%

echo Removing existing ZIP file if it exists...
if exist %ZIP_FILE% del /q %ZIP_FILE%

echo Downloading repository using PowerShell...
powershell -Command "Invoke-WebRequest -Uri %REPO_URL% -OutFile %ZIP_FILE%"

echo Extracting the ZIP file...
powershell -Command "Expand-Archive -Path %ZIP_FILE% -DestinationPath ."

cd %DIR_NAME%

echo Running the application...
gradlew.bat run

endlocal
