#pragma once

#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <string>
#include <functional>

namespace Engine {

class Window {
public:
    Window(int width, int height, const std::string& title);
    ~Window();

    bool shouldClose() const;
    void pollEvents();
    void swapBuffers();
    GLFWwindow* getGLFWWindow() const { return window; }

    int getWidth() const { return width; }
    int getHeight() const { return height; }
    float getAspectRatio() const { return static_cast<float>(width) / static_cast<float>(height); }

    // Callbacks
    void setFramebufferSizeCallback(std::function<void(int, int)> callback);
    void setMouseCallback(std::function<void(double, double)> callback);
    void setScrollCallback(std::function<void(double, double)> callback);

    // Input
    bool isKeyPressed(int key) const;
    bool isMouseButtonPressed(int button) const;
    void getCursorPos(double& xpos, double& ypos) const;
    void setCursorMode(int mode);

private:
    GLFWwindow* window;
    int width;
    int height;

    static void framebufferSizeCallback(GLFWwindow* window, int width, int height);
    static void mouseCallback(GLFWwindow* window, double xpos, double ypos);
    static void scrollCallback(GLFWwindow* window, double xoffset, double yoffset);
};

}
