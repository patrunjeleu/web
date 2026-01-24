@echo off
echo ========================================
echo  3D Game Engine - Build Script
echo ========================================
echo.

REM Check if dependencies exist
if not exist "external\glfw" (
    echo ERROR: GLFW not found. Run setup.bat first.
    pause
    exit /b 1
)

if not exist "external\glad" (
    echo ERROR: GLAD not found. Run setup.bat first.
    pause
    exit /b 1
)

if not exist "external\glm" (
    echo ERROR: GLM not found. Run setup.bat first.
    pause
    exit /b 1
)

REM Create build directory
if not exist "build" mkdir build
cd build

REM Configure with CMake
echo Configuring project with CMake...
cmake .. -G "Visual Studio 17 2022" -A x64

if errorlevel 1 (
    echo.
    echo ERROR: CMake configuration failed.
    echo Make sure you have CMake and Visual Studio 2022 installed.
    echo If you have a different version of Visual Studio, edit build.bat
    echo and change the generator name.
    echo.
    pause
    exit /b 1
)

REM Build the project
echo.
echo Building project...
cmake --build . --config Release

if errorlevel 1 (
    echo.
    echo ERROR: Build failed.
    pause
    exit /b 1
)

cd ..

echo.
echo ========================================
echo  Build successful!
echo ========================================
echo.
echo Executable location: build\bin\Release\GameDemo.exe
echo.
echo Run: run.bat
echo.
pause
