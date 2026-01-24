#pragma once

#include <glad/glad.h>
#include <glm/glm.hpp>
#include <vector>

namespace Engine {

struct Vertex {
    glm::vec3 position;
    glm::vec3 normal;
    glm::vec2 texCoords;
};

class Mesh {
public:
    Mesh(const std::vector<Vertex>& vertices, const std::vector<unsigned int>& indices);
    ~Mesh();

    void draw() const;

    static Mesh* createCube();
    static Mesh* createSphere(int segments = 32);
    static Mesh* createPlane(float size = 10.0f);

private:
    GLuint VAO, VBO, EBO;
    unsigned int indexCount;

    void setupMesh(const std::vector<Vertex>& vertices, const std::vector<unsigned int>& indices);
};

}
