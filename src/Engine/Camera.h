#pragma once

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

namespace Engine {

enum CameraMovement {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    UP,
    DOWN
};

class Camera {
public:
    Camera(glm::vec3 position = glm::vec3(0.0f, 0.0f, 3.0f),
           glm::vec3 up = glm::vec3(0.0f, 1.0f, 0.0f),
           float yaw = -90.0f, float pitch = 0.0f);

    glm::mat4 getViewMatrix() const;
    glm::mat4 getProjectionMatrix(float aspectRatio) const;

    void processKeyboard(CameraMovement direction, float deltaTime);
    void processMouseMovement(float xoffset, float yoffset, bool constrainPitch = true);
    void processMouseScroll(float yoffset);

    glm::vec3 getPosition() const { return position; }
    glm::vec3 getFront() const { return front; }
    float getFOV() const { return fov; }

    void setPosition(const glm::vec3& pos) { position = pos; }
    void setSpeed(float speed) { movementSpeed = speed; }
    void setSensitivity(float sensitivity) { mouseSensitivity = sensitivity; }

private:
    glm::vec3 position;
    glm::vec3 front;
    glm::vec3 up;
    glm::vec3 right;
    glm::vec3 worldUp;

    float yaw;
    float pitch;
    float fov;

    float movementSpeed;
    float mouseSensitivity;
    float zoomSensitivity;

    void updateCameraVectors();
};

}
