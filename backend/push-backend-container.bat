@echo off
SET IMAGE_NAME=build.dockerfile
SET CONTAINER_NAME=backend-container
SET PORT=8080

echo ---------------------------------------
echo Building Docker image: %IMAGE_NAME%
echo ---------------------------------------
docker build -f build.dockerfile -t %IMAGE_NAME% .

IF %ERRORLEVEL% NEQ 0 (
    echo Error: Docker build failed.
    goto :END
)

echo ---------------------------------------
echo Stopping and removing any existing container named: %CONTAINER_NAME%
echo ---------------------------------------
docker stop %CONTAINER_NAME% >nul 2>&1
docker rm %CONTAINER_NAME% >nul 2>&1

echo ---------------------------------------
echo Running container: %CONTAINER_NAME%
echo ---------------------------------------
docker run -d -p %PORT%:8080 --restart unless-stopped --name %CONTAINER_NAME% %IMAGE_NAME%
IF %ERRORLEVEL% EQU 0 (
    echo  Container is running on http://localhost:%PORT%
) ELSE (
    echo Error: Failed to start the container.
)

:END
echo.
echo Press any key to exit...
pause >nul
