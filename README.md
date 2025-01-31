# Game of Life

**Game of Life** is my Java implementation of the famous cellular automaton created by John Conway. The project simulates the evolution of a population of cells across generations, based on simple rules. The simulation runs on a grid where each cell can be alive or dead, and its state changes depending on the number of live neighbors.

This repository also contains the tools to package the application into an executable file for different platforms (Mac, Windows).

## Features

- **Automatic  mode** advances to the next generation automaticcally so you can watch the population evolve as if it were an animation.
- **Manual mode** allows you to control each step of the generational simulation of the game.
- Menu Bar integrated with the Operating System

## Future Features

- Resizable window that affects the game grid
- Multicolor simulation mode
- Save/Load system
- Screenshot system
- Settings menu:
  - Percentage of popultion coverage
  - Grid size
  - Automatíc Mode speed
  - Game colors

## Requirements

Before you begin, ensure you have the following software installed:

- **Java JDK**: Required to compile and run the project.
- Either **Windows** or **MacOS**, depending on the platform you are creating the app for.

## Build Instructions

### 1. Select the platform

Open to **build.sh**:

- If you are using Windows: comment the fragment of code that creates the Apple .dmg and leave the .exe
- If you are using MacOS: comment the fragment of code that creates the .exe and leave the .dmg

### 2. Use the Build Script

```bash
chmod +x build.sh
./build.sh
```

## License

This project is licensed under the MIT License.

#

**Game of Life** is an open-source project developed by Javier Iregui.
