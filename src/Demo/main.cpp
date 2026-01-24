#include "../Engine/Window.h"
#include "../Engine/Shader.h"
#include "../Engine/Mesh.h"
#include "../Engine/Camera.h"
#include "../Engine/Input.h"
#include "../Engine/Utils.h"
#include "../Engine/ECS/Scene.h"
#include "../Engine/ECS/Component.h"
#include "../Engine/Physics/PhysicsSystem.h"

#include <iostream>
#include <memory>
#include <random>

using namespace Engine;

// Mouse callback data
struct MouseCallbackData {
    Input* input;
    double lastX, lastY;
};

void mouseCallback(GLFWwindow* window, double xpos, double ypos) {
    MouseCallbackData* data = static_cast<MouseCallbackData*>(glfwGetWindowUserPointer(window));
    if (data && data->input) {
        data->input->processMouseMovement(xpos, ypos);
    }
}

void scrollCallback(GLFWwindow* window, double xoffset, double yoffset) {
    MouseCallbackData* data = static_cast<MouseCallbackData*>(glfwGetWindowUserPointer(window));
    if (data && data->input) {
        data->input->processMouseScroll(xoffset, yoffset);
    }
}

Entity* createPhysicsObject(Scene* scene, Mesh* mesh, const glm::vec3& position,
                            const glm::vec3& scale, const glm::vec4& color,
                            ColliderType colliderType, float mass, bool useGravity = true) {
    Entity* entity = scene->createEntity();

    // Transform
    Transform* transform = entity->addComponent<Transform>();
    transform->position = position;
    transform->scale = scale;

    // Mesh Renderer
    MeshRenderer* renderer = entity->addComponent<MeshRenderer>();
    renderer->mesh = mesh;
    renderer->color = color;

    // Collider
    Collider* collider = entity->addComponent<Collider>();
    collider->type = colliderType;
    collider->size = glm::vec3(1.0f);

    // RigidBody
    RigidBody* rb = entity->addComponent<RigidBody>();
    rb->mass = mass;
    rb->useGravity = useGravity;
    rb->restitution = 0.6f;
    rb->friction = 0.5f;

    return entity;
}

int main() {
    try {
        // Create window
        Window window(1280, 720, "3D Game Engine - Physics Demo");

        // Create camera
        Camera camera(glm::vec3(0.0f, 5.0f, 15.0f));
        camera.setSpeed(10.0f);

        // Create input system
        Input input(&window, &camera);
        input.lockMouse(true);

        // Setup mouse callbacks
        MouseCallbackData callbackData;
        callbackData.input = &input;
        glfwSetWindowUserPointer(window.getGLFWWindow(), &callbackData);
        glfwSetCursorPosCallback(window.getGLFWWindow(), mouseCallback);
        glfwSetScrollCallback(window.getGLFWWindow(), scrollCallback);

        // Load shaders
        std::string vertexCode = R"(
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoords;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoords;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    FragPos = vec3(model * vec4(aPos, 1.0));
    Normal = mat3(transpose(inverse(model))) * aNormal;
    TexCoords = aTexCoords;
    gl_Position = projection * view * vec4(FragPos, 1.0);
}
)";

        std::string fragmentCode = R"(
#version 330 core
out vec4 FragColor;

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoords;

uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec4 objectColor;
uniform vec3 lightColor;

void main()
{
    float ambientStrength = 0.3;
    vec3 ambient = ambientStrength * lightColor;

    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    float specularStrength = 0.5;
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = specularStrength * spec * lightColor;

    vec3 result = (ambient + diffuse + specular) * objectColor.rgb;
    FragColor = vec4(result, objectColor.a);
}
)";

        Shader shader(vertexCode, fragmentCode);

        // Create meshes
        Mesh* cubeMesh = Mesh::createCube();
        Mesh* sphereMesh = Mesh::createSphere(24);
        Mesh* planeMesh = Mesh::createPlane(20.0f);

        // Create scene
        Scene scene;

        // Create ground plane (static)
        Entity* ground = scene.createEntity("Ground");
        Transform* groundTransform = ground->addComponent<Transform>();
        groundTransform->position = glm::vec3(0.0f, 0.0f, 0.0f);
        groundTransform->scale = glm::vec3(1.0f);

        MeshRenderer* groundRenderer = ground->addComponent<MeshRenderer>();
        groundRenderer->mesh = planeMesh;
        groundRenderer->color = glm::vec4(0.4f, 0.4f, 0.4f, 1.0f);

        Collider* groundCollider = ground->addComponent<Collider>();
        groundCollider->type = ColliderType::PLANE;

        RigidBody* groundRb = ground->addComponent<RigidBody>();
        groundRb->isKinematic = true;  // Static object

        // Create some cubes and spheres
        std::random_device rd;
        std::mt19937 gen(rd());
        std::uniform_real_distribution<> colorDist(0.3, 1.0);
        std::uniform_real_distribution<> posXDist(-5.0, 5.0);
        std::uniform_real_distribution<> posZDist(-5.0, 5.0);

        // Create a pyramid of boxes
        int pyramidLayers = 4;
        float boxSize = 1.0f;
        for (int layer = 0; layer < pyramidLayers; layer++) {
            int boxesInLayer = pyramidLayers - layer;
            float y = 0.5f + layer * boxSize;

            for (int i = 0; i < boxesInLayer; i++) {
                float x = (i - boxesInLayer / 2.0f) * boxSize;
                glm::vec3 position(x, y, -5.0f);
                glm::vec4 color(colorDist(gen), colorDist(gen), colorDist(gen), 1.0f);

                createPhysicsObject(&scene, cubeMesh, position, glm::vec3(0.9f), color,
                                   ColliderType::BOX, 1.0f);
            }
        }

        // Create some spheres
        for (int i = 0; i < 5; i++) {
            glm::vec3 position(posXDist(gen), 8.0f + i * 2.0f, posZDist(gen));
            glm::vec4 color(colorDist(gen), colorDist(gen), colorDist(gen), 1.0f);

            createPhysicsObject(&scene, sphereMesh, position, glm::vec3(0.8f), color,
                               ColliderType::SPHERE, 1.0f);
        }

        // Create physics system
        PhysicsSystem physics;

        // Timing
        float deltaTime = 0.0f;
        float lastFrame = 0.0f;

        std::cout << "\n=== 3D GAME ENGINE - PHYSICS DEMO ===\n";
        std::cout << "Controls:\n";
        std::cout << "  WASD - Move camera\n";
        std::cout << "  Space/Ctrl - Move up/down\n";
        std::cout << "  Mouse - Look around\n";
        std::cout << "  Mouse Scroll - Zoom\n";
        std::cout << "  ESC - Toggle mouse lock\n";
        std::cout << "  R - Reset scene (spawn sphere)\n";
        std::cout << "  Q - Quit\n";
        std::cout << "====================================\n\n";

        // Game loop
        while (!window.shouldClose()) {
            // Calculate delta time
            float currentFrame = static_cast<float>(glfwGetTime());
            deltaTime = currentFrame - lastFrame;
            lastFrame = currentFrame;

            // Input
            window.pollEvents();

            // Toggle mouse lock with ESC
            if (input.isKeyPressed(GLFW_KEY_ESCAPE)) {
                static bool escPressed = false;
                if (!escPressed) {
                    input.lockMouse(!input.isMouseLocked());
                    escPressed = true;
                }
            } else {
                static bool escPressed = false;
                escPressed = false;
            }

            // Quit with Q
            if (input.isKeyPressed(GLFW_KEY_Q)) {
                glfwSetWindowShouldClose(window.getGLFWWindow(), true);
            }

            // Spawn sphere with R
            if (input.isKeyPressed(GLFW_KEY_R)) {
                static bool rPressed = false;
                if (!rPressed) {
                    glm::vec3 spawnPos = camera.getPosition() + camera.getFront() * 3.0f;
                    glm::vec4 color(colorDist(gen), colorDist(gen), colorDist(gen), 1.0f);
                    Entity* sphere = createPhysicsObject(&scene, sphereMesh, spawnPos,
                                                         glm::vec3(0.8f), color,
                                                         ColliderType::SPHERE, 1.0f);

                    // Give it some forward velocity
                    RigidBody* rb = sphere->getComponent<RigidBody>();
                    rb->velocity = camera.getFront() * 10.0f;

                    rPressed = true;
                }
            } else {
                static bool rPressed = false;
                rPressed = false;
            }

            input.update(deltaTime);

            // Update physics
            physics.update(&scene, deltaTime);

            // Render
            glClearColor(0.1f, 0.1f, 0.15f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            shader.use();

            // Set matrices
            glm::mat4 view = camera.getViewMatrix();
            glm::mat4 projection = camera.getProjectionMatrix(window.getAspectRatio());
            shader.setMat4("view", view);
            shader.setMat4("projection", projection);

            // Set lighting
            glm::vec3 lightPos(10.0f, 10.0f, 10.0f);
            shader.setVec3("lightPos", lightPos);
            shader.setVec3("viewPos", camera.getPosition());
            shader.setVec3("lightColor", glm::vec3(1.0f, 1.0f, 1.0f));

            // Render all entities
            for (const auto& entity : scene.getEntities()) {
                if (!entity->isActive()) continue;

                Transform* transform = entity->getComponent<Transform>();
                MeshRenderer* renderer = entity->getComponent<MeshRenderer>();

                if (transform && renderer && renderer->mesh) {
                    shader.setMat4("model", transform->getModelMatrix());
                    shader.setVec4("objectColor", renderer->color);
                    renderer->mesh->draw();
                }
            }

            window.swapBuffers();
        }

        // Cleanup
        delete cubeMesh;
        delete sphereMesh;
        delete planeMesh;

        std::cout << "Engine shut down successfully.\n";

    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
        return -1;
    }

    return 0;
}
