#pragma once

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/quaternion.hpp>

namespace Engine {

// Component base (for type identification)
struct Component {
    virtual ~Component() = default;
};

// Transform Component
struct Transform : public Component {
    glm::vec3 position = glm::vec3(0.0f);
    glm::quat rotation = glm::quat(1.0f, 0.0f, 0.0f, 0.0f);
    glm::vec3 scale = glm::vec3(1.0f);

    glm::mat4 getModelMatrix() const {
        glm::mat4 model = glm::mat4(1.0f);
        model = glm::translate(model, position);
        model = model * glm::mat4_cast(rotation);
        model = glm::scale(model, scale);
        return model;
    }

    void setRotationEuler(float pitch, float yaw, float roll) {
        rotation = glm::quat(glm::vec3(glm::radians(pitch), glm::radians(yaw), glm::radians(roll)));
    }
};

// Mesh Renderer Component
struct MeshRenderer : public Component {
    class Mesh* mesh = nullptr;
    glm::vec4 color = glm::vec4(1.0f, 1.0f, 1.0f, 1.0f);
    bool castShadows = true;
};

// RigidBody Component (for physics)
struct RigidBody : public Component {
    glm::vec3 velocity = glm::vec3(0.0f);
    glm::vec3 angularVelocity = glm::vec3(0.0f);
    float mass = 1.0f;
    float restitution = 0.5f;  // Bounciness (0-1)
    float friction = 0.5f;
    bool useGravity = true;
    bool isKinematic = false;  // If true, not affected by physics

    // Computed values
    glm::vec3 force = glm::vec3(0.0f);
    glm::vec3 torque = glm::vec3(0.0f);

    void addForce(const glm::vec3& f) {
        if (!isKinematic) {
            force += f;
        }
    }

    void addImpulse(const glm::vec3& impulse) {
        if (!isKinematic) {
            velocity += impulse / mass;
        }
    }
};

// Collider Component
enum class ColliderType {
    BOX,
    SPHERE,
    PLANE
};

struct Collider : public Component {
    ColliderType type = ColliderType::BOX;
    glm::vec3 size = glm::vec3(1.0f);  // For box: width, height, depth; For sphere: radius in x
    bool isTrigger = false;
};

}
