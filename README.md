<p align="center">
  <img src="media/banner.png" alt="Banner">
</p>

# Game of Life

**Game of Life** is my Java implementation of the famous cellular automaton created by John Conway. The project simulates the evolution of a population of cells across generations, based on simple rules. The simulation runs on a grid where each cell can be alive or dead, and its state changes depending on the number of live neighbors. My interpretation of Conway's work uses different colors to create several populations developing in the same enviroment.

This repository also contains the tools to package the application into an executable file for different platforms (Mac, Windows).

## Features

- **Multicolor** simulation representing several populations
- **Automatic mode** advances to the next generation automaticcally so you can watch the population evolve as if it were an animation.
- **Simulation control** allows you to skip to the next step of the generational simulation manually (Spacebar).
- **Resizable window** that affects the game grid.
- **Menu Bar** integrated with the Operating System (Windows/MacOS).
- **Save/Load** system.
- **Screenshot** system.
- **Simulation rules** window.
- **Settings** menu:
  - Percentage of popultion coverage
  - Cell size
  - Automatíc Mode speed
  - Show/Hide Grid

## Future Features

- Draw/Erase tools to edit the simulation state

At the moment, there are no plans to introduce any new features. <br>
I may rewrite most of the code into different classes in the future.

## Requirements

Before you begin, ensure you have the following software installed:

- **Java JDK**: Required to compile and run the project.
- Either **Windows** or **MacOS**, depending on the platform you are creating the app for.

## Build Instructions

### 1. Select the platform

Open **build.sh**:

- If you are using Windows: comment the fragment of code that creates the Apple .dmg and leave the .exe
- If you are using MacOS: comment the fragment of code that creates the .exe and leave the .dmg

### 2. Use the Build Script

```bash
chmod +x build.sh
./build.sh
```

### 3. Allow execution

If you are running the program on MacOS you may find an error when you open it. <br><br>
You will have to give your computer permission to run the program from:<br>
``Apple menu`` > ``System Settings`` > ``Privacy & Security`` > ``Security``

On Windows you may need to open it as administrtor.

## License

This project is licensed under the MIT License.

#

**Game of Life** is an open-source project developed by Javier Iregui, 2025.
