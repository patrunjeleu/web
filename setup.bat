@echo off
echo ========================================
echo  3D Game Engine - Dependency Setup
echo ========================================
echo.

REM Create external directory if it doesn't exist
if not exist "external" mkdir external
cd external

REM Download GLFW
echo [1/3] Setting up GLFW...
if not exist "glfw" (
    git clone --depth 1 --branch 3.3.8 https://github.com/glfw/glfw.git
    echo GLFW downloaded successfully.
) else (
    echo GLFW already exists, skipping...
)

REM Download GLAD
echo [2/3] Setting up GLAD...
if not exist "glad" (
    mkdir glad
    cd glad
    mkdir include
    mkdir src
    cd ..
    echo.
    echo Please download GLAD manually:
    echo 1. Go to https://glad.dav1d.de/
    echo 2. Set Language to C/C++
    echo 3. Set Specification to OpenGL
    echo 4. Set gl to Version 3.3 or higher
    echo 5. Set Profile to Core
    echo 6. Check "Generate a loader"
    echo 7. Click GENERATE
    echo 8. Download the ZIP file
    echo 9. Extract "include" folder contents to external/glad/include/
    echo 10. Extract "src" folder contents to external/glad/src/
    echo.
    pause
) else (
    echo GLAD already exists, skipping...
)

REM Download GLM
echo [3/3] Setting up GLM...
if not exist "glm" (
    git clone --depth 1 --branch 0.9.9.8 https://github.com/g-truc/glm.git
    echo GLM downloaded successfully.
) else (
    echo GLM already exists, skipping...
)

cd ..

echo.
echo ========================================
echo  Dependencies setup complete!
echo ========================================
echo.
echo Next steps:
echo 1. If you haven't downloaded GLAD manually, do so now
echo 2. Run: build.bat
echo.
pause
