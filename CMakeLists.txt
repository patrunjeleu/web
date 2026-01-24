cmake_minimum_required(VERSION 3.15)
project(GameEngine3D VERSION 1.0.0)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Output directories
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)

# Options
option(BUILD_SHARED_LIBS "Build shared libraries" OFF)

# Dependencies
find_package(OpenGL REQUIRED)

# GLFW
set(GLFW_BUILD_DOCS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(GLFW_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
add_subdirectory(external/glfw)

# GLAD
add_library(glad external/glad/src/glad.c)
target_include_directories(glad PUBLIC external/glad/include)

# GLM (header-only)
add_library(glm INTERFACE)
target_include_directories(glm INTERFACE external/glm)

# STB Image (header-only implementation)
add_library(stb_image external/stb/stb_image.cpp)
target_include_directories(stb_image PUBLIC external/stb)

# Engine Library
file(GLOB_RECURSE ENGINE_SOURCES
    "src/Engine/*.cpp"
    "src/Engine/*.h"
)

add_library(EngineCore ${ENGINE_SOURCES})
target_include_directories(EngineCore PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}/src
    ${OPENGL_INCLUDE_DIR}
)
target_link_libraries(EngineCore PUBLIC
    OpenGL::GL
    glfw
    glad
    glm
    stb_image
)

# Demo Application
add_executable(GameDemo
    src/Demo/main.cpp
)
target_link_libraries(GameDemo PRIVATE EngineCore)

# Copy shaders and assets to build directory
add_custom_command(TARGET GameDemo POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_directory
    ${CMAKE_SOURCE_DIR}/assets $<TARGET_FILE_DIR:GameDemo>/assets
)
