# Avoid Projectiles

This project rewrites the original Avoid Projectiles game into a final-project-ready JavaFX application with:

- an opening `FXML` form whose root is a `BorderPane`
- a `MenuBar` with `File`, `Edit`, and `Help`
- persistent user preferences
- `Controls`, `Rules`, and `About` windows
- an MVC structure that keeps game state in model classes

Run on Windows:

```powershell
powershell -ExecutionPolicy Bypass -File .\Start-AvoidProjectiles.ps1
```

Run on Linux or macOS:

```bash
./Start-AvoidProjectiles.sh
```

Controls:

- `Up`, `Down`, `Left`, `Right`: move Mario
- `Start Game` button: begin a run

Menus:

- `File > Exit`
- `Edit > Preferences`
- `Help > Controls`
- `Help > Rules`
- `Help > About`
