@echo off
echo Starting Game Engine Demo...
echo.

if not exist "build\bin\Release\GameDemo.exe" (
    echo ERROR: GameDemo.exe not found.
    echo Please run build.bat first.
    pause
    exit /b 1
)

cd build\bin\Release
GameDemo.exe
cd ..\..\..

pause
