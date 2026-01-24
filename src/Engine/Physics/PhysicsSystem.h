#pragma once

#include "../ECS/Scene.h"
#include "../ECS/Component.h"
#include <glm/glm.hpp>

namespace Engine {

struct CollisionInfo {
    Entity* entityA;
    Entity* entityB;
    glm::vec3 normal;
    float penetration;
    glm::vec3 contactPoint;
};

class PhysicsSystem {
public:
    PhysicsSystem();

    void update(Scene* scene, float deltaTime);
    void setGravity(const glm::vec3& g) { gravity = g; }
    glm::vec3 getGravity() const { return gravity; }

private:
    glm::vec3 gravity;

    void integrateForces(Entity* entity, float deltaTime);
    void integrateVelocity(Entity* entity, float deltaTime);
    void detectAndResolveCollisions(Scene* scene);

    bool checkCollision(Entity* a, Entity* b, CollisionInfo& info);
    bool sphereVsSphere(const Transform* t1, const Collider* c1,
                        const Transform* t2, const Collider* c2, CollisionInfo& info);
    bool boxVsBox(const Transform* t1, const Collider* c1,
                  const Transform* t2, const Collider* c2, CollisionInfo& info);
    bool sphereVsBox(const Transform* t1, const Collider* c1,
                     const Transform* t2, const Collider* c2, CollisionInfo& info);
    bool sphereVsPlane(const Transform* t1, const Collider* c1,
                       const Transform* t2, const Collider* c2, CollisionInfo& info);
    bool boxVsPlane(const Transform* t1, const Collider* c1,
                    const Transform* t2, const Collider* c2, CollisionInfo& info);

    void resolveCollision(const CollisionInfo& info);
};

}
