@echo off
setlocal enabledelayedexpansion
title RedCell Blood Bank Management System

set "APP_DIR=%~dp0"
echo ==========================================
echo Launching RedCell Blood Bank Management System
echo ==========================================
echo.

REM ===== Check Java Installation =====
echo Checking Java installation...
where java >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH!
    set /p USER_JAVA="Do you want to install Java 21 now? (Y/N): "
    if /i "!USER_JAVA!"=="Y" (
        start https://adoptium.net/temurin/releases/?version=21
    )
    echo.
    echo Press any key to close this window. After installing Java 21, run this file again.
    pause >nul
    exit /b 1
)

REM ===== Get Java Version =====
for /f "tokens=3 delims= " %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set "JAVA_VER=%%~v"
)
set "JAVA_VER=%JAVA_VER:"=%"
for /f "tokens=1 delims=." %%j in ("%JAVA_VER%") do set "JAVA_MAJOR=%%j"

echo Found Java version: %JAVA_VER%
if NOT "%JAVA_MAJOR%"=="21" (
    echo [ERROR] Java 21 is required but found: %JAVA_VER%
    set /p USER_JAVA="Do you want to install Java 21 now? (Y/N): "
    if /i "!USER_JAVA!"=="Y" (
        start https://adoptium.net/temurin/releases/?version=21
    )
    echo.
    echo Press any key to close this window. After installing Java 21, run this file again.
    pause >nul
    exit /b 1
)

REM ===== Check Maven Installation =====
echo Checking Maven installation...
where mvn >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed or not in PATH!
    set /p USER_MAVEN="Do you want to install Maven now? (Y/N): "
    if /i "!USER_MAVEN!"=="Y" (
        start https://maven.apache.org/download.cgi
    )
    echo.
    echo Press any key to close this window. After installing Maven, run this file again.
    pause >nul
    exit /b 1
)

REM ===== Get Maven Version =====
for /f "tokens=3" %%m in ('mvn -version ^| findstr /R /C:"^Apache Maven"') do (
    set "MAVEN_VER=%%m"
)
set "MAVEN_VER=%MAVEN_VER:"=%"
for /f "tokens=1-3 delims=." %%a in ("%MAVEN_VER%") do (
    set "MVN_MAJOR=%%a"
    set "MVN_MINOR=%%b"
    set "MVN_PATCH=%%c"
)

echo Found Maven version: !MAVEN_VER!
set /a IS_OLD=0
if !MVN_MAJOR! LSS 3 set IS_OLD=1
if !MVN_MAJOR!==3 (
    if !MVN_MINOR! LSS 8 set IS_OLD=1
    if !MVN_MINOR!==8 if !MVN_PATCH! LSS 1 set IS_OLD=1
)

if !IS_OLD!==1 (
    echo [ERROR] Maven 3.8.1 or later is required, but found: !MAVEN_VER!
    set /p USER_MAVEN="Do you want to install Maven now? (Y/N): "
    if /i "!USER_MAVEN!"=="Y" (
        start https://maven.apache.org/download.cgi
    )
    echo.
    echo Press any key to close this window. After installing Maven, run this file again.
    pause >nul
    exit /b 1
)

REM ===== Build the Application =====
echo.
echo Building RedCell...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo [ERROR] Build failed.
    echo.
    echo Press any key to close this window.
    pause >nul
    exit /b 1
)

REM ===== Run the Application =====
echo.
echo Starting RedCell...
call mvn javafx:run

echo.
echo If the application did not start, there might be an error.
echo Check the output above or logs for details.
echo Press any key to close this window.
pause >nul
