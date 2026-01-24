#include "PhysicsSystem.h"
#include <algorithm>
#include <cmath>

namespace Engine {

PhysicsSystem::PhysicsSystem() : gravity(0.0f, -9.81f, 0.0f) {}

void PhysicsSystem::update(Scene* scene, float deltaTime) {
    // Get all entities with physics
    auto entities = scene->getEntitiesWithComponent<RigidBody>();

    // Integrate forces
    for (Entity* entity : entities) {
        integrateForces(entity, deltaTime);
    }

    // Detect and resolve collisions
    detectAndResolveCollisions(scene);

    // Integrate velocities
    for (Entity* entity : entities) {
        integrateVelocity(entity, deltaTime);
    }
}

void PhysicsSystem::integrateForces(Entity* entity, float deltaTime) {
    RigidBody* rb = entity->getComponent<RigidBody>();
    if (!rb || rb->isKinematic) return;

    // Apply gravity
    if (rb->useGravity) {
        rb->force += gravity * rb->mass;
    }

    // F = ma, therefore a = F/m
    glm::vec3 acceleration = rb->force / rb->mass;
    rb->velocity += acceleration * deltaTime;

    // Apply damping (air resistance)
    rb->velocity *= 0.99f;

    // Reset forces
    rb->force = glm::vec3(0.0f);
}

void PhysicsSystem::integrateVelocity(Entity* entity, float deltaTime) {
    RigidBody* rb = entity->getComponent<RigidBody>();
    Transform* transform = entity->getComponent<Transform>();

    if (!rb || !transform || rb->isKinematic) return;

    // Update position
    transform->position += rb->velocity * deltaTime;

    // Update rotation based on angular velocity
    if (glm::length(rb->angularVelocity) > 0.001f) {
        float angle = glm::length(rb->angularVelocity) * deltaTime;
        glm::vec3 axis = glm::normalize(rb->angularVelocity);
        glm::quat rotation = glm::angleAxis(angle, axis);
        transform->rotation = rotation * transform->rotation;
    }
}

void PhysicsSystem::detectAndResolveCollisions(Scene* scene) {
    auto entities = scene->getEntitiesWithComponent<Collider>();

    for (size_t i = 0; i < entities.size(); i++) {
        for (size_t j = i + 1; j < entities.size(); j++) {
            CollisionInfo info;
            if (checkCollision(entities[i], entities[j], info)) {
                resolveCollision(info);
            }
        }
    }
}

bool PhysicsSystem::checkCollision(Entity* a, Entity* b, CollisionInfo& info) {
    Collider* colliderA = a->getComponent<Collider>();
    Collider* colliderB = b->getComponent<Collider>();
    Transform* transformA = a->getComponent<Transform>();
    Transform* transformB = b->getComponent<Transform>();

    if (!colliderA || !colliderB || !transformA || !transformB) return false;

    info.entityA = a;
    info.entityB = b;

    // Dispatch to appropriate collision detection function
    if (colliderA->type == ColliderType::SPHERE && colliderB->type == ColliderType::SPHERE) {
        return sphereVsSphere(transformA, colliderA, transformB, colliderB, info);
    } else if (colliderA->type == ColliderType::BOX && colliderB->type == ColliderType::BOX) {
        return boxVsBox(transformA, colliderA, transformB, colliderB, info);
    } else if (colliderA->type == ColliderType::SPHERE && colliderB->type == ColliderType::BOX) {
        return sphereVsBox(transformA, colliderA, transformB, colliderB, info);
    } else if (colliderA->type == ColliderType::BOX && colliderB->type == ColliderType::SPHERE) {
        bool result = sphereVsBox(transformB, colliderB, transformA, colliderA, info);
        if (result) info.normal = -info.normal;
        return result;
    } else if (colliderA->type == ColliderType::SPHERE && colliderB->type == ColliderType::PLANE) {
        return sphereVsPlane(transformA, colliderA, transformB, colliderB, info);
    } else if (colliderA->type == ColliderType::PLANE && colliderB->type == ColliderType::SPHERE) {
        bool result = sphereVsPlane(transformB, colliderB, transformA, colliderA, info);
        if (result) info.normal = -info.normal;
        return result;
    } else if (colliderA->type == ColliderType::BOX && colliderB->type == ColliderType::PLANE) {
        return boxVsPlane(transformA, colliderA, transformB, colliderB, info);
    } else if (colliderA->type == ColliderType::PLANE && colliderB->type == ColliderType::BOX) {
        bool result = boxVsPlane(transformB, colliderB, transformA, colliderA, info);
        if (result) info.normal = -info.normal;
        return result;
    }

    return false;
}

bool PhysicsSystem::sphereVsSphere(const Transform* t1, const Collider* c1,
                                   const Transform* t2, const Collider* c2,
                                   CollisionInfo& info) {
    glm::vec3 pos1 = t1->position;
    glm::vec3 pos2 = t2->position;
    float radius1 = c1->size.x * t1->scale.x;
    float radius2 = c2->size.x * t2->scale.x;

    glm::vec3 diff = pos2 - pos1;
    float distance = glm::length(diff);
    float radiusSum = radius1 + radius2;

    if (distance < radiusSum) {
        info.normal = glm::normalize(diff);
        info.penetration = radiusSum - distance;
        info.contactPoint = pos1 + info.normal * radius1;
        return true;
    }

    return false;
}

bool PhysicsSystem::boxVsBox(const Transform* t1, const Collider* c1,
                              const Transform* t2, const Collider* c2,
                              CollisionInfo& info) {
    // Simple AABB collision (not oriented)
    glm::vec3 min1 = t1->position - (c1->size * t1->scale * 0.5f);
    glm::vec3 max1 = t1->position + (c1->size * t1->scale * 0.5f);
    glm::vec3 min2 = t2->position - (c2->size * t2->scale * 0.5f);
    glm::vec3 max2 = t2->position + (c2->size * t2->scale * 0.5f);

    bool collisionX = max1.x > min2.x && min1.x < max2.x;
    bool collisionY = max1.y > min2.y && min1.y < max2.y;
    bool collisionZ = max1.z > min2.z && min1.z < max2.z;

    if (collisionX && collisionY && collisionZ) {
        // Calculate penetration and normal
        glm::vec3 overlap;
        overlap.x = std::min(max1.x - min2.x, max2.x - min1.x);
        overlap.y = std::min(max1.y - min2.y, max2.y - min1.y);
        overlap.z = std::min(max1.z - min2.z, max2.z - min1.z);

        // Find the axis of least penetration
        if (overlap.x < overlap.y && overlap.x < overlap.z) {
            info.penetration = overlap.x;
            info.normal = (t2->position.x > t1->position.x) ? glm::vec3(1, 0, 0) : glm::vec3(-1, 0, 0);
        } else if (overlap.y < overlap.z) {
            info.penetration = overlap.y;
            info.normal = (t2->position.y > t1->position.y) ? glm::vec3(0, 1, 0) : glm::vec3(0, -1, 0);
        } else {
            info.penetration = overlap.z;
            info.normal = (t2->position.z > t1->position.z) ? glm::vec3(0, 0, 1) : glm::vec3(0, 0, -1);
        }

        info.contactPoint = t1->position;
        return true;
    }

    return false;
}

bool PhysicsSystem::sphereVsBox(const Transform* t1, const Collider* c1,
                                const Transform* t2, const Collider* c2,
                                CollisionInfo& info) {
    glm::vec3 spherePos = t1->position;
    float radius = c1->size.x * t1->scale.x;

    glm::vec3 boxMin = t2->position - (c2->size * t2->scale * 0.5f);
    glm::vec3 boxMax = t2->position + (c2->size * t2->scale * 0.5f);

    // Find closest point on box to sphere
    glm::vec3 closestPoint = glm::clamp(spherePos, boxMin, boxMax);

    glm::vec3 diff = spherePos - closestPoint;
    float distance = glm::length(diff);

    if (distance < radius) {
        info.normal = glm::normalize(diff);
        info.penetration = radius - distance;
        info.contactPoint = closestPoint;
        return true;
    }

    return false;
}

bool PhysicsSystem::sphereVsPlane(const Transform* t1, const Collider* c1,
                                  const Transform* t2, const Collider* c2,
                                  CollisionInfo& info) {
    glm::vec3 spherePos = t1->position;
    float radius = c1->size.x * t1->scale.x;

    // Plane normal is always up (0, 1, 0)
    glm::vec3 planeNormal = glm::vec3(0.0f, 1.0f, 0.0f);
    float planeY = t2->position.y;

    float distance = spherePos.y - planeY;

    if (distance < radius) {
        info.normal = planeNormal;
        info.penetration = radius - distance;
        info.contactPoint = spherePos - planeNormal * radius;
        return true;
    }

    return false;
}

bool PhysicsSystem::boxVsPlane(const Transform* t1, const Collider* c1,
                               const Transform* t2, const Collider* c2,
                               CollisionInfo& info) {
    glm::vec3 boxMin = t1->position - (c1->size * t1->scale * 0.5f);

    glm::vec3 planeNormal = glm::vec3(0.0f, 1.0f, 0.0f);
    float planeY = t2->position.y;

    if (boxMin.y < planeY) {
        info.normal = planeNormal;
        info.penetration = planeY - boxMin.y;
        info.contactPoint = glm::vec3(t1->position.x, planeY, t1->position.z);
        return true;
    }

    return false;
}

void PhysicsSystem::resolveCollision(const CollisionInfo& info) {
    RigidBody* rbA = info.entityA->getComponent<RigidBody>();
    RigidBody* rbB = info.entityB->getComponent<RigidBody>();
    Transform* transformA = info.entityA->getComponent<Transform>();
    Transform* transformB = info.entityB->getComponent<Transform>();

    if (!rbA && !rbB) return;

    // Calculate relative velocity
    glm::vec3 velA = rbA ? rbA->velocity : glm::vec3(0.0f);
    glm::vec3 velB = rbB ? rbB->velocity : glm::vec3(0.0f);
    glm::vec3 relativeVel = velB - velA;

    float velAlongNormal = glm::dot(relativeVel, info.normal);

    // Don't resolve if velocities are separating
    if (velAlongNormal > 0) return;

    // Calculate restitution (use minimum of both)
    float restitution = 0.5f;
    if (rbA && rbB) {
        restitution = std::min(rbA->restitution, rbB->restitution);
    } else if (rbA) {
        restitution = rbA->restitution;
    } else if (rbB) {
        restitution = rbB->restitution;
    }

    // Calculate impulse scalar
    float massA = rbA && !rbA->isKinematic ? rbA->mass : 0.0f;
    float massB = rbB && !rbB->isKinematic ? rbB->mass : 0.0f;

    float invMassA = (massA > 0.0f) ? 1.0f / massA : 0.0f;
    float invMassB = (massB > 0.0f) ? 1.0f / massB : 0.0f;

    if (invMassA + invMassB == 0.0f) return;

    float j = -(1.0f + restitution) * velAlongNormal;
    j /= invMassA + invMassB;

    glm::vec3 impulse = j * info.normal;

    // Apply impulse
    if (rbA && !rbA->isKinematic) {
        rbA->velocity -= impulse * invMassA;
    }
    if (rbB && !rbB->isKinematic) {
        rbB->velocity += impulse * invMassB;
    }

    // Position correction to prevent sinking
    const float percent = 0.8f;
    const float slop = 0.01f;
    glm::vec3 correction = std::max(info.penetration - slop, 0.0f) / (invMassA + invMassB) * percent * info.normal;

    if (transformA && rbA && !rbA->isKinematic) {
        transformA->position -= correction * invMassA;
    }
    if (transformB && rbB && !rbB->isKinematic) {
        transformB->position += correction * invMassB;
    }
}

}
