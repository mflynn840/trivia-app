@echo off
echo Starting Trivia Backend Server...
echo.

cd backend

echo Building the project...
call mvn clean compile

echo.
echo Starting Spring Boot application...
echo The server will be available at: http://localhost:8080
echo.
echo To load questions into the database, visit: http://localhost:8080/api/game/questions/load
echo.

call mvn spring-boot:run

pause
