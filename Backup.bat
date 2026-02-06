@echo off
REM Backup Tool Startup Script for Windows
REM Usage: backup.bat [source] [destination] [interval_minutes]

REM Configuration (edit these if you want default values)
set DEFAULT_SOURCE=C:\Users\%USERNAME%\Documents
set DEFAULT_DESTINATION=C:\Users\%USERNAME%\Backups
set DEFAULT_INTERVAL=

REM Use command line arguments or defaults
if "%~1"=="" (
    set SOURCE=%DEFAULT_SOURCE%
) else (
    set SOURCE=%~1
)

if "%~2"=="" (
    set DESTINATION=%DEFAULT_DESTINATION%
) else (
    set DESTINATION=%~2
)

if "%~3"=="" (
    set INTERVAL=%DEFAULT_INTERVAL%
) else (
    set INTERVAL=%~3
)

echo ========================================
echo     Backup Tool Launcher
echo ========================================
echo.

REM Check if JAR exists
set JAR_FILE=target\backup-tool-1.0-SNAPSHOT.jar
if not exist "%JAR_FILE%" (
    echo Error: JAR file not found at %JAR_FILE%
    echo Please build the project first with: mvn clean package
    pause
    exit /b 1
)

REM Display configuration
echo Configuration:
echo   Source Directory:      %SOURCE%
echo   Destination Directory: %DESTINATION%

if "%INTERVAL%"=="" (
    echo   Mode:                  One-time backup
) else (
    echo   Mode:                  Scheduled (every %INTERVAL% minutes^)
)

echo.

REM Verify source directory exists
if not exist "%SOURCE%" (
    echo Error: Source directory does not exist: %SOURCE%
    pause
    exit /b 1
)

REM Run the backup tool
echo Starting backup tool...
echo.

if "%INTERVAL%"=="" (
    java -jar "%JAR_FILE%" "%SOURCE%" "%DESTINATION%"
) else (
    java -jar "%JAR_FILE%" "%SOURCE%" "%DESTINATION%" "%INTERVAL%"
)

pause