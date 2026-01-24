#pragma once

#include "Entity.h"
#include <vector>
#include <memory>
#include <algorithm>

namespace Engine {

class Scene {
public:
    Scene() : nextEntityID(0) {}

    Entity* createEntity(const std::string& name = "Entity") {
        auto entity = std::make_unique<Entity>(nextEntityID++, name);
        Entity* ptr = entity.get();
        entities.push_back(std::move(entity));
        return ptr;
    }

    void destroyEntity(Entity* entity) {
        entities.erase(
            std::remove_if(entities.begin(), entities.end(),
                [entity](const std::unique_ptr<Entity>& e) {
                    return e.get() == entity;
                }),
            entities.end()
        );
    }

    Entity* findEntity(const std::string& name) {
        for (auto& entity : entities) {
            if (entity->getName() == name) {
                return entity.get();
            }
        }
        return nullptr;
    }

    const std::vector<std::unique_ptr<Entity>>& getEntities() const {
        return entities;
    }

    template<typename T>
    std::vector<Entity*> getEntitiesWithComponent() {
        std::vector<Entity*> result;
        for (auto& entity : entities) {
            if (entity->hasComponent<T>()) {
                result.push_back(entity.get());
            }
        }
        return result;
    }

private:
    std::vector<std::unique_ptr<Entity>> entities;
    unsigned int nextEntityID;
};

}
