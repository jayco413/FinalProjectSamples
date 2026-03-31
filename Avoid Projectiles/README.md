# Avoid Projectiles

This project rewrites the original Avoid Projectiles game into a final-project-ready JavaFX application with:

- an opening `FXML` form whose root is a `BorderPane`
- a `MenuBar` with `File`, `Edit`, and `Help`
- persistent user preferences
- persistent top-10 high scores stored in JSON
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

Objective:

- reach `100` points in as little time as possible
- each Bullet Bill that leaves the screen without hitting Mario awards `1` point
- Starmen add `10` points and temporary invincibility
- Mini Mushrooms add `5` points and temporarily shrink Mario
- top-10 completion times are saved locally in `avoid-projectiles-high-scores.json`

Menus:

- `File > High Scores`
- `File > Exit`
- `Edit > Preferences`
- `Help > Controls`
- `Help > Rules`
- `Help > About`
