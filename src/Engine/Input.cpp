#include "Input.h"

namespace Engine {

Input::Input(Window* window, Camera* camera)
    : window(window), camera(camera), mouseLocked(false), firstMouse(true),
      lastX(0.0), lastY(0.0) {}

void Input::update(float deltaTime) {
    if (!window || !camera) return;

    // Camera movement
    if (isKeyPressed(GLFW_KEY_W)) {
        camera->processKeyboard(FORWARD, deltaTime);
    }
    if (isKeyPressed(GLFW_KEY_S)) {
        camera->processKeyboard(BACKWARD, deltaTime);
    }
    if (isKeyPressed(GLFW_KEY_A)) {
        camera->processKeyboard(LEFT, deltaTime);
    }
    if (isKeyPressed(GLFW_KEY_D)) {
        camera->processKeyboard(RIGHT, deltaTime);
    }
    if (isKeyPressed(GLFW_KEY_SPACE)) {
        camera->processKeyboard(UP, deltaTime);
    }
    if (isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
        camera->processKeyboard(DOWN, deltaTime);
    }
}

void Input::processMouseMovement(double xpos, double ypos) {
    if (!mouseLocked || !camera) return;

    if (firstMouse) {
        lastX = xpos;
        lastY = ypos;
        firstMouse = false;
    }

    double xoffset = xpos - lastX;
    double yoffset = lastY - ypos; // Reversed since y-coordinates range from bottom to top

    lastX = xpos;
    lastY = ypos;

    camera->processMouseMovement(static_cast<float>(xoffset), static_cast<float>(yoffset));
}

void Input::processMouseScroll(double xoffset, double yoffset) {
    if (camera) {
        camera->processMouseScroll(static_cast<float>(yoffset));
    }
}

bool Input::isKeyPressed(int key) const {
    return window->isKeyPressed(key);
}

bool Input::isMouseButtonPressed(int button) const {
    return window->isMouseButtonPressed(button);
}

void Input::lockMouse(bool lock) {
    mouseLocked = lock;
    if (window) {
        window->setCursorMode(lock ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }
    if (lock) {
        firstMouse = true;
    }
}

}
