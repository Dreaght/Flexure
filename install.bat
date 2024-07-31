@echo off
setlocal

set REPO_URL=https://github.com/Dreaght/Flexure/archive/refs/heads/master.zip
set ZIP_FILE=Flexure-master.zip
set DIR_NAME=Flexure-master

echo Downloading repository using PowerShell...
powershell -Command "Invoke-WebRequest -Uri %REPO_URL% -OutFile %ZIP_FILE%"

echo Extracting the ZIP file...
powershell -Command "Expand-Archive -Path %ZIP_FILE% -DestinationPath ."

cd %DIR_NAME%

echo Running the application...
gradlew.bat run

endlocal
