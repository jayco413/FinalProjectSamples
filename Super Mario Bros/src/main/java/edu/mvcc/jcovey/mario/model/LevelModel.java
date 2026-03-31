package edu.mvcc.jcovey.mario.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LevelModel {
    private final String areaId;
    private final Path levelPath;
    private final List<PhysicsRect> terrain = new ArrayList<>();
    private final List<BlockModel> blocks = new ArrayList<>();
    private final List<CoinModel> coins = new ArrayList<>();
    private final List<EnemyModel> enemies = new ArrayList<>();
    private final List<PipeModel> pipes = new ArrayList<>();
    private final List<PlatformModel> platforms = new ArrayList<>();
    private final TextLevelLoader loader = new TextLevelLoader();
    private TextLevelData structure;
    private PhysicsRect flagPoleZone;
    private PhysicsRect goalZone;
    private FlagPoleModel flagPole;
    private FortressModel fortress;
    private double worldWidth;
    private double spawnX;
    private double spawnY;

    public LevelModel(String areaId, Path levelPath) {
        this.areaId = areaId;
        this.levelPath = levelPath;
        reset();
    }

    public void reset() {
        terrain.clear();
        blocks.clear();
        coins.clear();
        enemies.clear();
        pipes.clear();
        platforms.clear();
        structure = loader.load(levelPath);
        worldWidth = structure.getColumnCount() * GameConstants.TILE_SIZE;
        spawnX = 80.0;
        spawnY = 384.0;

        int flagColumn = -1;
        int flagTopRow = Integer.MAX_VALUE;
        int flagBottomRow = -1;
        int fortressLeft = Integer.MAX_VALUE;
        int fortressRight = -1;
        int fortressTop = Integer.MAX_VALUE;
        int fortressBottom = -1;
        int doorwayLeft = Integer.MAX_VALUE;
        int doorwayRight = -1;
        int doorwayTop = Integer.MAX_VALUE;
        int doorwayBottom = -1;
        for (int row = 0; row < structure.getRowCount(); row++) {
            for (int column = 0; column < structure.getColumnCount(); column++) {
                char cell = structure.getCell(row, column);
                double x = column * GameConstants.TILE_SIZE;
                double y = row * GameConstants.TILE_SIZE;

                if (cell == '#' || cell == 'T' || cell == 'S') {
                    terrain.add(new PhysicsRect(x, y, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE));
                } else if (isPipeCell(cell) || isWallPipeCell(row, column)) {
                    terrain.add(new PhysicsRect(x, y, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE));
                } else if (cell == 'B') {
                    blocks.add(new BlockModel(x, y, "brick", 0, false, false, false, true));
                } else if (cell == '?') {
                    blocks.add(new BlockModel(x, y, "question", 1, false, false, false, true));
                } else if (cell == 'M') {
                    blocks.add(new BlockModel(x, y, "question", 0, true, false, false, true));
                } else if (cell == 'Q') {
                    if ("bonus".equals(areaId) && row < 2) {
                        spawnX = x;
                        spawnY = y;
                    } else {
                        blocks.add(new BlockModel(x, y, "used", 0, false, false, false, true));
                    }
                } else if (cell == 'R') {
                    if ("overworld".equals(areaId) || "overworld-exit".equals(areaId)) {
                        spawnX = x;
                        spawnY = y;
                    }
                } else if (cell == '+') {
                    blocks.add(new BlockModel(x, y, "hidden1up", 0, false, true, false, false));
                } else if (cell == '!') {
                    blocks.add(new BlockModel(x, y, "oneupbrick", 0, false, true, false, true));
                } else if (cell == 'O') {
                    blocks.add(new BlockModel(x, y, "coinbrick", 10, false, false, false, true));
                } else if (cell == '*') {
                    blocks.add(new BlockModel(x, y, "starbrick", 0, false, false, true, true));
                } else if (cell == 'c') {
                    coins.add(new CoinModel(x, y));
                } else if (cell == 'g') {
                    enemies.add(new EnemyModel("goomba", x + 2.0, y + 4.0));
                } else if (cell == 'k') {
                    enemies.add(new EnemyModel("parakoopa", x + 2.0, y + 4.0));
                } else if (cell == '|') {
                    if (flagColumn < 0) {
                        flagColumn = column;
                    }
                    flagTopRow = Math.min(flagTopRow, row);
                    flagBottomRow = Math.max(flagBottomRow, row);
                } else if (cell == 'X' || cell == 'x') {
                    fortressLeft = Math.min(fortressLeft, column);
                    fortressRight = Math.max(fortressRight, column);
                    fortressTop = Math.min(fortressTop, row);
                    fortressBottom = Math.max(fortressBottom, row);
                    if (cell == 'x') {
                        doorwayLeft = Math.min(doorwayLeft, column);
                        doorwayRight = Math.max(doorwayRight, column);
                        doorwayTop = Math.min(doorwayTop, row);
                        doorwayBottom = Math.max(doorwayBottom, row);
                    }
                }
            }
        }

        populatePipes();
        populatePlatforms();

        if (isGoalArea()) {
            if (flagColumn < 0) {
                flagColumn = structure.getColumnCount() - 5;
            }
            if (flagTopRow == Integer.MAX_VALUE) {
                flagTopRow = 3;
                flagBottomRow = 12;
            }
            double poleX = flagColumn * GameConstants.TILE_SIZE;
            double poleTopY = flagTopRow * GameConstants.TILE_SIZE;
            double poleBottomY = (flagBottomRow + 1) * GameConstants.TILE_SIZE;
            flagPole = new FlagPoleModel(poleX, poleTopY, poleBottomY);
            flagPoleZone = flagPole.getZone();
            if (fortressRight >= fortressLeft && doorwayRight >= doorwayLeft) {
                fortress = new FortressModel(
                    fortressLeft * GameConstants.TILE_SIZE,
                    fortressTop * GameConstants.TILE_SIZE,
                    (fortressRight - fortressLeft) + 1,
                    (fortressBottom - fortressTop) + 1,
                    doorwayLeft * GameConstants.TILE_SIZE,
                    doorwayTop * GameConstants.TILE_SIZE,
                    (doorwayRight - doorwayLeft) + 1,
                    (doorwayBottom - doorwayTop) + 1
                );
                goalZone = fortress.getDoorwayZone();
            } else {
                fortress = null;
                goalZone = new PhysicsRect(poleX + (GameConstants.TILE_SIZE * 2.0), GameConstants.TILE_SIZE * 8.0, GameConstants.TILE_SIZE * 5.0, GameConstants.TILE_SIZE * 5.0);
            }
            return;
        }

        flagPole = null;
        fortress = null;
        flagPoleZone = new PhysicsRect(-1000.0, -1000.0, 1.0, 1.0);
        goalZone = new PhysicsRect(-1000.0, -1000.0, 1.0, 1.0);
    }

    private void populatePipes() {
        for (int row = 0; row < structure.getRowCount() - 1; row++) {
            for (int column = 0; column < structure.getColumnCount(); column++) {
                char top = structure.getCell(row, column);
                char bottom = structure.getCell(row + 1, column);
                if ((top == '<' || top == ')') && bottom == '>') {
                    int lengthInTiles = 1;
                    int scanColumn = column + 1;
                    while (scanColumn < structure.getColumnCount()) {
                        char scanTop = structure.getCell(row, scanColumn);
                        char scanBottom = structure.getCell(row + 1, scanColumn);
                        if (scanTop == 'H' && scanBottom == 'h') {
                            lengthInTiles++;
                            scanColumn++;
                            continue;
                        }
                        if (scanTop == 'F' && scanBottom == 'f') {
                            lengthInTiles++;
                        }
                        break;
                    }
                    pipes.add(new PipeModel(
                        column * GameConstants.TILE_SIZE,
                        row * GameConstants.TILE_SIZE,
                        lengthInTiles,
                        getHorizontalWarpId(top),
                        "horizontal-left"
                    ));
                }
            }
        }
        for (int row = 0; row < structure.getRowCount(); row++) {
            for (int column = 0; column < structure.getColumnCount() - 1; column++) {
                String topPair = readPair(row, column);
                String bodyPair = getBodyPair(topPair);
                if (bodyPair.isEmpty()) {
                    continue;
                }

                int heightInTiles = 1;
                int scanRow = row + 1;
                while (scanRow < structure.getRowCount() && bodyPair.equals(readPair(scanRow, column))) {
                    heightInTiles++;
                    scanRow++;
                }
                if (scanRow < structure.getRowCount() && "eE".equals(readPair(scanRow, column))) {
                    heightInTiles++;
                }

                pipes.add(new PipeModel(
                    column * GameConstants.TILE_SIZE,
                    row * GameConstants.TILE_SIZE,
                    heightInTiles,
                    getWarpId(topPair),
                    "vertical-up"
                ));
            }
        }
    }

    private boolean isPipeCell(char cell) {
        return cell == '[' || cell == ']' || cell == '{'
            || cell == 'p' || cell == 'P' || cell == 'e' || cell == 'E'
            || cell == '<' || cell == '>' || cell == ')' || cell == 'h'
            || cell == 'H' || cell == 'f' || cell == 'F';
    }

    private boolean isWallPipeCell(int row, int column) {
        return false;
    }

    private String readPair(int row, int column) {
        return "" + structure.getCell(row, column) + structure.getCell(row, column + 1);
    }

    private String getBodyPair(String topPair) {
        if ("[]".equals(topPair)) {
            return "pP";
        }
        if ("{]".equals(topPair)) {
            return "pP";
        }
        return "";
    }

    private String getWarpId(String topPair) {
        if ("{]".equals(topPair)) {
            return "secret-entry";
        }
        return "";
    }

    private String getHorizontalWarpId(char top) {
        if (top != ')') {
            return "";
        }
        if ("bonus".equals(areaId)) {
            return "secret-exit";
        }
        if ("underground".equals(areaId)) {
            return "exit-area";
        }
        return "";
    }

    public List<PhysicsRect> getTerrain() {
        return terrain;
    }

    public List<BlockModel> getBlocks() {
        return blocks;
    }

    public List<CoinModel> getCoins() {
        return coins;
    }

    public List<EnemyModel> getEnemies() {
        return enemies;
    }

    public List<PipeModel> getPipes() {
        return pipes;
    }

    public List<PlatformModel> getPlatforms() {
        return platforms;
    }

    public PipeModel findPipeByWarpId(String warpId) {
        for (PipeModel pipe : pipes) {
            if (warpId.equals(pipe.getWarpId())) {
                return pipe;
            }
        }
        return null;
    }

    public TextLevelData getStructure() {
        return structure;
    }

    public PhysicsRect getFlagPoleZone() {
        return flagPoleZone;
    }

    public PhysicsRect getGoalZone() {
        return goalZone;
    }

    public FlagPoleModel getFlagPole() {
        return flagPole;
    }

    public double getWorldWidth() {
        return worldWidth;
    }

    public String getAreaId() {
        return areaId;
    }

    public Path getLevelPath() {
        return levelPath;
    }

    public double getSpawnX() {
        return spawnX;
    }

    public double getSpawnY() {
        return spawnY;
    }

    public FortressModel getFortress() {
        return fortress;
    }

    private boolean isGoalArea() {
        return "overworld".equals(areaId) || "overworld-exit".equals(areaId);
    }

    private void populatePlatforms() {
        for (int row = 0; row < structure.getRowCount(); row++) {
            int column = 0;
            while (column < structure.getColumnCount()) {
                char cell = structure.getCell(row, column);
                if (cell != 'D' && cell != 'U') {
                    column++;
                    continue;
                }

                int runStart = column;
                while (column < structure.getColumnCount() && structure.getCell(row, column) == cell) {
                    column++;
                }
                double x = runStart * GameConstants.TILE_SIZE;
                double startY = (row * GameConstants.TILE_SIZE) + (GameConstants.TILE_SIZE * 0.25);
                platforms.add(new PlatformModel(x, startY, cell == 'D'));
            }
        }
    }
}
