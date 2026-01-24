#pragma once

#include "Component.h"
#include <memory>
#include <unordered_map>
#include <typeindex>
#include <string>

namespace Engine {

class Entity {
public:
    Entity(unsigned int id, const std::string& name = "Entity")
        : id(id), name(name), active(true) {}

    unsigned int getID() const { return id; }
    const std::string& getName() const { return name; }
    void setName(const std::string& n) { name = n; }

    bool isActive() const { return active; }
    void setActive(bool a) { active = a; }

    template<typename T, typename... Args>
    T* addComponent(Args&&... args) {
        std::type_index typeIdx(typeid(T));
        auto component = std::make_unique<T>(std::forward<Args>(args)...);
        T* ptr = component.get();
        components[typeIdx] = std::move(component);
        return ptr;
    }

    template<typename T>
    T* getComponent() {
        std::type_index typeIdx(typeid(T));
        auto it = components.find(typeIdx);
        if (it != components.end()) {
            return static_cast<T*>(it->second.get());
        }
        return nullptr;
    }

    template<typename T>
    bool hasComponent() {
        std::type_index typeIdx(typeid(T));
        return components.find(typeIdx) != components.end();
    }

    template<typename T>
    void removeComponent() {
        std::type_index typeIdx(typeid(T));
        components.erase(typeIdx);
    }

private:
    unsigned int id;
    std::string name;
    bool active;
    std::unordered_map<std::type_index, std::unique_ptr<Component>> components;
};

}
