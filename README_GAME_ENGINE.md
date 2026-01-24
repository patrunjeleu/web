# 3D Game Engine in C++ with OpenGL

A fully functional 3D game engine built from scratch using modern C++ (C++17) and OpenGL 3.3+. Features a complete Entity Component System (ECS), physics engine with collision detection, and an interactive demo showcasing real-time physics simulations.

## Features

### Core Engine Systems
- **Window Management**: GLFW-based windowing with OpenGL context
- **Modern OpenGL 3.3+**: Shader-based rendering pipeline
- **Camera System**: FPS-style camera with smooth controls
- **Entity Component System**: Flexible architecture for game objects
- **Physics Engine**:
  - Rigid body dynamics
  - Collision detection (sphere-sphere, box-box, sphere-box, plane collisions)
  - Gravity and forces
  - Impulse-based collision resolution
  - Configurable restitution and friction
- **Input System**: Keyboard and mouse handling
- **Mesh System**: Procedural geometry generation (cubes, spheres, planes)
- **Lighting**: Phong lighting model (ambient, diffuse, specular)

### Demo Application
The included demo showcases:
- Physics simulation with multiple objects
- Real-time collision detection and response
- Interactive object spawning
- Pyramid of boxes demonstrating stability
- Falling spheres with realistic physics
- FPS camera controls for scene exploration

## Prerequisites

### Windows
- **Visual Studio 2022** (or 2019) with C++ development tools
- **CMake** 3.15 or higher
- **Git** (for downloading dependencies)

## Building the Engine

### Step 1: Download Dependencies

Run the setup script to download GLFW and GLM:

```batch
setup.bat
```

**Important**: You need to manually download GLAD:
1. Go to https://glad.dav1d.de/
2. Configure:
   - **Language**: C/C++
   - **Specification**: OpenGL
   - **API gl**: Version 3.3 (or higher)
   - **Profile**: Core
3. Check **"Generate a loader"**
4. Click **GENERATE**
5. Download the ZIP file
6. Extract:
   - `include/` folder contents → `external/glad/include/`
   - `src/` folder contents → `external/glad/src/`

### Step 2: Build the Project

```batch
build.bat
```

This will:
- Configure the project with CMake
- Build the engine and demo using Visual Studio compiler
- Output executable to `build/bin/Release/GameDemo.exe`

### Step 3: Run the Demo

```batch
run.bat
```

Or directly run:
```batch
build\bin\Release\GameDemo.exe
```

## Controls

### Camera
- **W/A/S/D** - Move forward/left/backward/right
- **Space** - Move up
- **Left Ctrl** - Move down
- **Mouse** - Look around (first-person view)
- **Mouse Scroll** - Zoom in/out

### Interaction
- **R** - Spawn a new sphere with forward velocity
- **ESC** - Toggle mouse lock/unlock
- **Q** - Quit application

## Project Structure

```
GameEngine3D/
├── src/
│   ├── Engine/
│   │   ├── Window.h/cpp          # Window and OpenGL context
│   │   ├── Shader.h/cpp          # Shader compilation and management
│   │   ├── Mesh.h/cpp            # Mesh data and rendering
│   │   ├── Camera.h/cpp          # FPS camera system
│   │   ├── Input.h/cpp           # Input handling
│   │   ├── Utils.h               # Utility functions
│   │   ├── ECS/
│   │   │   ├── Component.h       # Component definitions
│   │   │   ├── Entity.h          # Entity class
│   │   │   └── Scene.h           # Scene management
│   │   └── Physics/
│   │       └── PhysicsSystem.h/cpp  # Physics simulation
│   └── Demo/
│       └── main.cpp              # Demo application
├── assets/
│   └── shaders/
│       ├── basic.vert            # Vertex shader
│       └── basic.frag            # Fragment shader
├── external/                     # Third-party libraries
│   ├── glfw/                     # Window and input
│   ├── glad/                     # OpenGL loader
│   ├── glm/                      # Math library
│   └── stb/                      # Image loading (placeholder)
├── CMakeLists.txt               # Build configuration
├── setup.bat                    # Dependency download script
├── build.bat                    # Build script
└── run.bat                      # Run script
```

## Architecture

### Entity Component System (ECS)

The engine uses a component-based architecture:

```cpp
// Create an entity
Entity* entity = scene.createEntity("MyObject");

// Add components
Transform* transform = entity->addComponent<Transform>();
transform->position = glm::vec3(0.0f, 5.0f, 0.0f);

MeshRenderer* renderer = entity->addComponent<MeshRenderer>();
renderer->mesh = cubeMesh;
renderer->color = glm::vec4(1.0f, 0.0f, 0.0f, 1.0f);

RigidBody* rb = entity->addComponent<RigidBody>();
rb->mass = 1.0f;
rb->useGravity = true;

Collider* collider = entity->addComponent<Collider>();
collider->type = ColliderType::BOX;
```

### Physics System

The physics engine supports:
- **Rigid bodies** with mass, velocity, and forces
- **Colliders** (box, sphere, plane)
- **Gravity** and custom forces
- **Collision resolution** with restitution (bounciness)
- **Kinematic objects** (non-physics controlled)

### Rendering Pipeline

1. Update camera matrices (view, projection)
2. For each entity with MeshRenderer:
   - Calculate model matrix from Transform
   - Set shader uniforms
   - Render mesh geometry

## Customization

### Adding New Objects

```cpp
// Create a new entity
Entity* myObject = scene.createEntity("CustomObject");

// Setup transform
Transform* t = myObject->addComponent<Transform>();
t->position = glm::vec3(x, y, z);
t->scale = glm::vec3(sx, sy, sz);

// Setup rendering
MeshRenderer* r = myObject->addComponent<MeshRenderer>();
r->mesh = yourMesh;
r->color = glm::vec4(r, g, b, a);

// Setup physics
Collider* c = myObject->addComponent<Collider>();
c->type = ColliderType::SPHERE;

RigidBody* rb = myObject->addComponent<RigidBody>();
rb->mass = mass;
rb->restitution = bounciness;
```

### Modifying Physics

```cpp
// Change gravity
physicsSystem.setGravity(glm::vec3(0.0f, -20.0f, 0.0f));

// Apply forces to objects
RigidBody* rb = entity->getComponent<RigidBody>();
rb->addForce(glm::vec3(10.0f, 0.0f, 0.0f));  // Apply force
rb->addImpulse(glm::vec3(0.0f, 5.0f, 0.0f)); // Apply impulse
```

## Technical Details

- **Language**: C++17
- **Graphics API**: OpenGL 3.3 Core Profile
- **Math Library**: GLM (OpenGL Mathematics)
- **Window/Input**: GLFW 3.3.8
- **OpenGL Loader**: GLAD
- **Build System**: CMake 3.15+
- **Compiler**: MSVC (Visual Studio 2022)

## Performance

The engine runs at 60+ FPS with:
- 20+ physics objects
- Real-time collision detection
- Phong lighting calculations
- Full scene rendering

## Future Enhancements

Potential additions:
- [ ] Texture loading and mapping
- [ ] Shadow mapping
- [ ] Particle systems
- [ ] Audio system
- [ ] Model loading (OBJ, GLTF)
- [ ] Post-processing effects
- [ ] UI rendering
- [ ] Advanced physics (constraints, joints)
- [ ] Spatial partitioning (octree/BVH)
- [ ] Multi-threading support

## Troubleshooting

### Build Errors

**CMake not found**:
- Install CMake from https://cmake.org/download/
- Add to system PATH

**GLAD files missing**:
- Make sure you downloaded GLAD from https://glad.dav1d.de/
- Extract to `external/glad/` correctly

**Visual Studio version mismatch**:
- Edit `build.bat` and change the generator:
  - VS 2019: `-G "Visual Studio 16 2019"`
  - VS 2017: `-G "Visual Studio 15 2017"`

### Runtime Errors

**Failed to initialize GLFW/GLAD**:
- Update graphics drivers
- Ensure OpenGL 3.3+ support

**Window doesn't open**:
- Check if another fullscreen app is running
- Verify monitor is connected

## License

This project is provided as-is for educational purposes.

## Credits

Built using:
- **GLFW** - https://www.glfw.org/
- **GLAD** - https://glad.dav1d.de/
- **GLM** - https://github.com/g-truc/glm
- **STB** - https://github.com/nothings/stb

---

**Enjoy building with the engine!**
