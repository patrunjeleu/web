#pragma once

#include <glad/glad.h>
#include <glm/glm.hpp>
#include <string>

namespace Engine {

class Shader {
public:
    Shader(const std::string& vertexSource, const std::string& fragmentSource);
    ~Shader();

    void use() const;
    void setBool(const std::string& name, bool value) const;
    void setInt(const std::string& name, int value) const;
    void setFloat(const std::string& name, float value) const;
    void setVec3(const std::string& name, const glm::vec3& value) const;
    void setVec4(const std::string& name, const glm::vec4& value) const;
    void setMat4(const std::string& name, const glm::mat4& value) const;

    GLuint getID() const { return programID; }

private:
    GLuint programID;
    GLuint compileShader(GLenum type, const std::string& source);
    void checkCompileErrors(GLuint shader, const std::string& type);
};

}
