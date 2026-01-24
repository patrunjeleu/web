#include "Window.h"
#include <iostream>
#include <stdexcept>

namespace Engine {

Window::Window(int width, int height, const std::string& title)
    : width(width), height(height), window(nullptr) {

    if (!glfwInit()) {
        throw std::runtime_error("Failed to initialize GLFW");
    }

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    window = glfwCreateWindow(width, height, title.c_str(), nullptr, nullptr);
    if (!window) {
        glfwTerminate();
        throw std::runtime_error("Failed to create GLFW window");
    }

    glfwMakeContextCurrent(window);
    glfwSetWindowUserPointer(window, this);

    if (!gladLoadGLLoader((GLADloadproc)glfwGetProcAddress)) {
        throw std::runtime_error("Failed to initialize GLAD");
    }

    glViewport(0, 0, width, height);
    glfwSetFramebufferSizeCallback(window, framebufferSizeCallback);

    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);
}

Window::~Window() {
    glfwDestroyWindow(window);
    glfwTerminate();
}

bool Window::shouldClose() const {
    return glfwWindowShouldClose(window);
}

void Window::pollEvents() {
    glfwPollEvents();
}

void Window::swapBuffers() {
    glfwSwapBuffers(window);
}

void Window::setFramebufferSizeCallback(std::function<void(int, int)> callback) {
    // Store callback in window user pointer if needed
}

void Window::setMouseCallback(std::function<void(double, double)> callback) {
    glfwSetCursorPosCallback(window, mouseCallback);
}

void Window::setScrollCallback(std::function<void(double, double)> callback) {
    glfwSetScrollCallback(window, scrollCallback);
}

bool Window::isKeyPressed(int key) const {
    return glfwGetKey(window, key) == GLFW_PRESS;
}

bool Window::isMouseButtonPressed(int button) const {
    return glfwGetMouseButton(window, button) == GLFW_PRESS;
}

void Window::getCursorPos(double& xpos, double& ypos) const {
    glfwGetCursorPos(window, &xpos, &ypos);
}

void Window::setCursorMode(int mode) {
    glfwSetInputMode(window, GLFW_CURSOR, mode);
}

void Window::framebufferSizeCallback(GLFWwindow* window, int width, int height) {
    glViewport(0, 0, width, height);
    Window* win = static_cast<Window*>(glfwGetWindowUserPointer(window));
    if (win) {
        win->width = width;
        win->height = height;
    }
}

void Window::mouseCallback(GLFWwindow* window, double xpos, double ypos) {
    // Handled by Camera/Input system
}

void Window::scrollCallback(GLFWwindow* window, double xoffset, double yoffset) {
    // Handled by Camera/Input system
}

}
