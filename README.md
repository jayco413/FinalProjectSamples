# JavaProgramming

This repository contains standalone JavaFX game projects intended to be run outside Eclipse as normal desktop programs.

## What Is In Here

- `Avoid Projectiles/`
  A JavaFX game where the goal is to reach 100 points as fast as possible while avoiding Bullet Bills and collecting power-ups.
- `Super Mario Bros/`
  A JavaFX recreation project based on Super Mario Bros.
- `lib/`
  The shared JavaFX SDK used by the projects.
- `AGENTS.md`
  Repository-wide project and course requirements.

## Running The Games

These projects are set up to launch on their own without needing Eclipse.

### Windows

Double-click either of these from the repository root:

- `Windows-Start-AvoidProjectiles.bat`
- `Windows-Start-SuperMario.bat`

### macOS

Finder users can double-click:

- `MacOS-Start-AvoidProjectiles.command`
- `MacOS-Start-SuperMario.command`

If macOS blocks the files the first time, make them executable once:

```bash
chmod +x MacOS-Start-AvoidProjectiles.command MacOS-Start-SuperMario.command
```

### Linux

Linux desktop environments often let you launch executable shell scripts directly. These root launchers are included:

- `Linux-Start-AvoidProjectiles.sh`
- `Linux-Start-SuperMario.sh`

If needed, make them executable once:

```bash
chmod +x Linux-Start-AvoidProjectiles.sh Linux-Start-SuperMario.sh
```

Then run them from the repository root:

```bash
./Linux-Start-AvoidProjectiles.sh
./Linux-Start-SuperMario.sh
```

## Notes

- The projects compile and run from their own startup scripts.
- The root launchers are convenience wrappers that call those project scripts.
- The repository is organized so additional standalone JavaFX games can be added later.
