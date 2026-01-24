#pragma once

#include <string>
#include <fstream>
#include <sstream>
#include <stdexcept>

namespace Engine {

inline std::string loadFileToString(const std::string& filepath) {
    std::ifstream file(filepath);
    if (!file.is_open()) {
        throw std::runtime_error("Failed to open file: " + filepath);
    }

    std::stringstream buffer;
    buffer << file.rdbuf();
    return buffer.str();
}

}
