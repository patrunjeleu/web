#pragma once

#include "Window.h"
#include "Camera.h"
#include <glm/glm.hpp>

namespace Engine {

class Input {
public:
    Input(Window* window, Camera* camera);

    void update(float deltaTime);
    void processMouseMovement(double xpos, double ypos);
    void processMouseScroll(double xoffset, double yoffset);

    bool isKeyPressed(int key) const;
    bool isMouseButtonPressed(int button) const;

    void lockMouse(bool lock);
    bool isMouseLocked() const { return mouseLocked; }

private:
    Window* window;
    Camera* camera;

    bool mouseLocked;
    bool firstMouse;
    double lastX, lastY;
};

}
