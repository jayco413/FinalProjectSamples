package edu.mvcc.jcovey.mario.view;

import edu.mvcc.jcovey.mario.model.BlockModel;
import edu.mvcc.jcovey.mario.model.BrickFragmentModel;
import edu.mvcc.jcovey.mario.model.CoinModel;
import edu.mvcc.jcovey.mario.model.FlagPoleModel;
import edu.mvcc.jcovey.mario.model.FortressModel;
import edu.mvcc.jcovey.mario.model.FloatingCoinModel;
import edu.mvcc.jcovey.mario.model.GameConstants;
import edu.mvcc.jcovey.mario.model.GameModel;
import edu.mvcc.jcovey.mario.model.MushroomModel;
import edu.mvcc.jcovey.mario.model.PlatformModel;
import edu.mvcc.jcovey.mario.model.StarModel;
import edu.mvcc.jcovey.mario.model.TextLevelData;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

final class WorldRenderer {
    private final RenderAssets assets;

    WorldRenderer(RenderAssets assets) {
        this.assets = assets;
    }

    void render(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        drawRepeatingBackground(graphics, cameraX, gameModel.getLevel().getAreaId());
        drawPipes(graphics, gameModel.getLevel().getStructure(), cameraX);
        drawStructuralLayer(graphics, gameModel.getLevel().getStructure(), cameraX);
        drawBlocks(graphics, gameModel, cameraX);
        drawPlatforms(graphics, gameModel, cameraX);
        drawCoins(graphics, gameModel, cameraX);
        drawFloatingCoins(graphics, gameModel, cameraX);
        drawBrickFragments(graphics, gameModel, cameraX);
        drawMushrooms(graphics, gameModel, cameraX);
        drawStars(graphics, gameModel, cameraX);
        drawFlagPole(graphics, gameModel, cameraX);
        drawFortress(graphics, gameModel, cameraX);
    }

    private void drawRepeatingBackground(GraphicsContext graphics, double cameraX, String areaId) {
        Image background = assets.repeatingBackground();
        if ("underground".equals(areaId)) {
            background = assets.undergroundLevelBackground();
        } else if ("bonus".equals(areaId)) {
            background = assets.undergroundBackground();
        }
        if (background == null || background.isError()) {
            return;
        }

        double playfieldY = GameConstants.HUD_ROWS * GameConstants.TILE_SIZE;
        double playfieldHeight = GameConstants.VIEWPORT_HEIGHT - playfieldY;
        double tileWidth = background.getWidth() * (playfieldHeight / background.getHeight());
        double startX = -(cameraX % tileWidth);
        for (double drawX = startX - tileWidth; drawX < GameConstants.VIEWPORT_WIDTH + tileWidth; drawX += tileWidth) {
            graphics.drawImage(background, drawX, playfieldY, tileWidth, playfieldHeight);
        }
    }

    private void drawStructuralLayer(GraphicsContext graphics, TextLevelData structure, double cameraX) {
        GoodAsset floor = assets.goodAssets().get("FB");
        for (int row = 0; row < structure.getRowCount(); row++) {
            for (int column = 0; column < structure.getColumnCount(); column++) {
                char cell = structure.getCell(row, column);
                double drawX = (column * GameConstants.TILE_SIZE) - cameraX;
                double drawY = row * GameConstants.TILE_SIZE;
                if (cell == 'T') {
                    drawMushroomTopTile(graphics, structure, row, column, drawX, drawY);
                    continue;
                }
                if (cell == 't') {
                    drawMushroomBodyTile(graphics, structure, row, column, drawX, drawY);
                    int extraRows = MushroomTiles.extraBodyRowsToDraw(structure, row, column);
                    for (int offset = 1; offset <= extraRows; offset++) {
                            drawMushroomBodyTile(
                                graphics,
                                structure,
                                row,
                                column,
                                drawX,
                                drawY + (offset * GameConstants.TILE_SIZE)
                            );
                    }
                    continue;
                }
                String code = getStructuralAssetCode(cell);
                if (code.isEmpty()) {
                    continue;
                }
                GoodAsset asset = assets.goodAssets().get(code);
                if (asset != null) {
                    graphics.drawImage(asset.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
                    if (floor != null && row == structure.getRowCount() - 1 && cell == '#') {
                        graphics.drawImage(
                            floor.getImage(),
                            drawX,
                            drawY + GameConstants.TILE_SIZE,
                            GameConstants.TILE_SIZE,
                            GameConstants.TILE_SIZE
                        );
                    }
                }
            }
        }
    }

    private void drawMushroomTopTile(GraphicsContext graphics, TextLevelData structure, int row, int column, double drawX, double drawY) {
        drawStructuralTile(graphics, MushroomTiles.topCode(structure, row, column), drawX, drawY);
    }

    private void drawMushroomBodyTile(GraphicsContext graphics, TextLevelData structure, int row, int column, double drawX, double drawY) {
        String code = MushroomTiles.bodyCode(structure, row, column);
        Image image = "JL".equals(code)
            ? assets.mushroomBodyLeft()
            : ("JR".equals(code) ? assets.mushroomBodyRight() : assets.mushroomBodyMiddle());
        if (image != null && !image.isError()) {
            graphics.drawImage(image, drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
        }
    }

    private void drawStructuralTile(GraphicsContext graphics, String code, double drawX, double drawY) {
        GoodAsset asset = assets.goodAssets().get(code);
        if (asset != null) {
            graphics.drawImage(asset.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
        }
    }

    private void drawBlocks(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset brick = assets.goodAssets().get("BR");
        GoodAsset question = assets.goodAssets().get("QB");
        GoodAsset used = assets.goodAssets().get("UB");
        for (BlockModel block : gameModel.getLevel().getBlocks()) {
            if (!block.isVisible()) {
                continue;
            }
            double drawX = block.getX() - cameraX;
            double drawY = block.getY() + block.getRenderOffsetY();
            if (("brick".equals(block.getType()) || "oneupbrick".equals(block.getType())) && brick != null) {
                graphics.drawImage(brick.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            } else if (("coinbrick".equals(block.getType()) || "starbrick".equals(block.getType())) && !block.isUsed() && brick != null) {
                graphics.drawImage(brick.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            } else if ("question".equals(block.getType()) && !block.isUsed() && question != null) {
                graphics.drawImage(question.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            } else if (used != null) {
                graphics.drawImage(used.getImage(), drawX, drawY, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            }
        }
    }

    private void drawStars(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset starAsset = assets.goodAssets().get("ST");
        for (StarModel star : gameModel.getStars()) {
            if (!star.isActive()) {
                continue;
            }
            if (starAsset != null) {
                graphics.drawImage(starAsset.getImage(), star.getX() - cameraX, star.getY(), star.getWidth(), star.getHeight());
            } else {
                drawPlaceholderStar(graphics, star, cameraX);
            }
        }
    }

    private void drawPlaceholderStar(GraphicsContext graphics, StarModel star, double cameraX) {
        double centerX = (star.getX() - cameraX) + (star.getWidth() / 2.0);
        double centerY = star.getY() + (star.getHeight() / 2.0);
        double outer = star.getWidth() * 0.48;
        double inner = outer * 0.45;
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];
        for (int index = 0; index < 10; index++) {
            double angle = (-90.0 + (index * 36.0)) * (Math.PI / 180.0);
            double radius = (index % 2 == 0) ? outer : inner;
            xPoints[index] = centerX + (Math.cos(angle) * radius);
            yPoints[index] = centerY + (Math.sin(angle) * radius);
        }
        graphics.setFill(Color.web("#ffd83d"));
        graphics.fillPolygon(xPoints, yPoints, xPoints.length);
        graphics.setStroke(Color.web("#fff7b8"));
        graphics.setLineWidth(1.5);
        graphics.strokePolygon(xPoints, yPoints, xPoints.length);
    }

    private void drawMushrooms(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset mushroomAsset = assets.goodAssets().get("MU");
        GoodAsset oneUpAsset = assets.goodAssets().get("OU");
        GoodAsset fireFlowerAsset = assets.goodAssets().get("FF");
        for (MushroomModel mushroom : gameModel.getMushrooms()) {
            if (!mushroom.isActive()) {
                continue;
            }
            GoodAsset assetToUse = "oneup".equals(mushroom.getType())
                ? oneUpAsset
                : ("fireflower".equals(mushroom.getType()) ? fireFlowerAsset : mushroomAsset);
            if (assetToUse != null) {
                graphics.drawImage(assetToUse.getImage(), mushroom.getX() - cameraX, mushroom.getY(), mushroom.getWidth(), mushroom.getHeight());
            } else {
                graphics.drawImage(
                    assets.atlas().getMushroomSprite(),
                    mushroom.getX() - cameraX,
                    mushroom.getY(),
                    mushroom.getWidth(),
                    mushroom.getHeight()
                );
            }
        }
    }

    private void drawPlatforms(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset platformAsset = assets.goodAssets().get("PL");
        for (PlatformModel platform : gameModel.getLevel().getPlatforms()) {
            double drawX = platform.getX() - cameraX;
            if (platformAsset != null) {
                graphics.drawImage(platformAsset.getImage(), drawX, platform.getY(), platform.getWidth(), platform.getHeight());
            } else {
                graphics.setFill(Color.web("#c47a2c"));
                graphics.fillRect(drawX, platform.getY(), platform.getWidth(), platform.getHeight());
                graphics.setFill(Color.web("#8a4f16"));
                graphics.fillRect(drawX, platform.getY() + platform.getHeight() - 4.0, platform.getWidth(), 4.0);
            }
        }
    }

    private void drawCoins(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset coinAsset = assets.goodAssets().get("CO");
        if (coinAsset == null) {
            return;
        }
        for (CoinModel coin : gameModel.getLevel().getCoins()) {
            if (!coin.isCollected()) {
                graphics.drawImage(coinAsset.getImage(), coin.getX() - cameraX, coin.getY(), coin.getWidth(), coin.getHeight());
            }
        }
    }

    private void drawFloatingCoins(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset coinAsset = assets.goodAssets().get("CO");
        for (FloatingCoinModel coin : gameModel.getFloatingCoins()) {
            if (coin.isSparkling()) {
                drawCoinSparkle(graphics, coin, cameraX);
            } else if (coinAsset != null) {
                graphics.drawImage(coinAsset.getImage(), coin.getX() - cameraX, coin.getY(), GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            }
        }
    }

    private void drawCoinSparkle(GraphicsContext graphics, FloatingCoinModel coin, double cameraX) {
        double centerX = (coin.getX() - cameraX) + (GameConstants.TILE_SIZE / 2.0);
        double centerY = coin.getY() + (GameConstants.TILE_SIZE / 2.0);
        double radius = 10.0 + (coin.getSparkleTimer() * 28.0);
        graphics.setStroke(Color.web("#fff2a8"));
        graphics.setLineWidth(3.0);
        graphics.strokeLine(centerX - radius, centerY, centerX + radius, centerY);
        graphics.strokeLine(centerX, centerY - radius, centerX, centerY + radius);
        graphics.setStroke(Color.web("#fffdf0"));
        graphics.setLineWidth(2.0);
        graphics.strokeLine(centerX - (radius * 0.7), centerY - (radius * 0.7), centerX + (radius * 0.7), centerY + (radius * 0.7));
        graphics.strokeLine(centerX - (radius * 0.7), centerY + (radius * 0.7), centerX + (radius * 0.7), centerY - (radius * 0.7));
    }

    private void drawBrickFragments(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        GoodAsset brickAsset = assets.goodAssets().get("BR");
        if (brickAsset == null) {
            return;
        }
        double sourceHalfWidth = brickAsset.getImage().getWidth() / 2.0;
        double sourceHalfHeight = brickAsset.getImage().getHeight() / 2.0;
        for (BrickFragmentModel fragment : gameModel.getBrickFragments()) {
            graphics.drawImage(
                brickAsset.getImage(),
                fragment.getSourceColumn() * sourceHalfWidth,
                fragment.getSourceRow() * sourceHalfHeight,
                sourceHalfWidth,
                sourceHalfHeight,
                fragment.getX() - cameraX,
                fragment.getY(),
                GameConstants.TILE_SIZE / 2.0,
                GameConstants.TILE_SIZE / 2.0
            );
        }
    }

    private String getStructuralAssetCode(char cell) {
        if (cell == '#') {
            return "FB";
        }
        if (cell == 'S') {
            return "MS";
        }
        return "";
    }

    private void drawFlagPole(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        FlagPoleModel flagPole = gameModel.getLevel().getFlagPole();
        if (flagPole == null) {
            return;
        }
        GoodAsset poleBody = assets.goodAssets().get("LP");
        GoodAsset poleCap = assets.goodAssets().get("LC");
        GoodAsset flag = assets.goodAssets().get("FG");
        double drawX = flagPole.getX() - cameraX;
        if (poleCap != null) {
            graphics.drawImage(poleCap.getImage(), drawX, flagPole.getTopY(), GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
        } else {
            graphics.setFill(Color.web("#d8ffd0"));
            graphics.fillOval(drawX + 8.0, flagPole.getTopY() + 6.0, 16.0, 16.0);
        }
        for (double y = flagPole.getTopY() + GameConstants.TILE_SIZE; y < flagPole.getBottomY(); y += GameConstants.TILE_SIZE) {
            if (poleBody != null) {
                graphics.drawImage(poleBody.getImage(), drawX, y, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
            } else {
                graphics.setFill(Color.web("#78d050"));
                graphics.fillRect(drawX + 13.0, y, 6.0, GameConstants.TILE_SIZE);
            }
        }
        double flagX = drawX - GameConstants.TILE_SIZE;
        if (flag != null) {
            graphics.drawImage(flag.getImage(), flagX, flagPole.getFlagY(), GameConstants.TILE_SIZE * 2.0, GameConstants.TILE_SIZE);
        } else {
            graphics.setFill(Color.WHITE);
            graphics.fillPolygon(
                new double[] { flagX + 28.0, flagX + 6.0, flagX + 28.0 },
                new double[] { flagPole.getFlagY() + 6.0, flagPole.getFlagY() + 16.0, flagPole.getFlagY() + 26.0 },
                3
            );
        }
    }

    private void drawFortress(GraphicsContext graphics, GameModel gameModel, double cameraX) {
        FortressModel fortress = gameModel.getLevel().getFortress();
        if (fortress == null) {
            return;
        }
        GoodAsset fortressAsset = assets.goodAssets().get("FO");
        double drawX = fortress.getX() - cameraX;
        if (fortressAsset != null) {
            graphics.drawImage(fortressAsset.getImage(), drawX, fortress.getY(), fortress.getWidth(), fortress.getHeight());
        } else {
            graphics.setFill(Color.web("#6b3f1f"));
            graphics.fillRect(drawX, fortress.getY(), fortress.getWidth(), fortress.getHeight());
        }
    }

    private void drawPipes(GraphicsContext graphics, TextLevelData structure, double cameraX) {
        GoodAsset pipeAsset = assets.goodAssets().get("TP");
        if (pipeAsset == null) {
            return;
        }
        Image image = pipeAsset.getImage();
        double sliceHeight = image.getHeight() / 3.0;
        drawVerticalPipes(graphics, structure, image, cameraX, sliceHeight);
        drawSingleColumnPipeShafts(graphics, structure, image, cameraX, sliceHeight);
        drawHorizontalPipes(graphics, structure, cameraX, image, sliceHeight);
    }

    private void drawPipeSegment(GraphicsContext graphics, Image image, double cameraX, int column, int row, double sourceY, double sliceHeight) {
        graphics.drawImage(
            image,
            0.0,
            sourceY,
            image.getWidth(),
            sliceHeight,
            (column * GameConstants.TILE_SIZE) - cameraX,
            row * GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE * 2.0,
            GameConstants.TILE_SIZE
        );
    }

    private void drawHorizontalPipeCell(GraphicsContext graphics, int column, int row, double cameraX, int sourceColumn, int sourceRow) {
        Image image = assets.rotatedHorizontalPipe();
        if (image == null) {
            return;
        }
        double cellWidth = image.getWidth() / 3.0;
        double cellHeight = image.getHeight() / 2.0;
        graphics.drawImage(
            image,
            sourceColumn * cellWidth,
            sourceRow * cellHeight,
            cellWidth,
            cellHeight,
            (column * GameConstants.TILE_SIZE) - cameraX,
            row * GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE
        );
    }

    private void drawVerticalPipes(GraphicsContext graphics, TextLevelData structure, Image image, double cameraX, double sliceHeight) {
        for (int row = 0; row < structure.getRowCount(); row++) {
            for (int column = 0; column < structure.getColumnCount() - 1; column++) {
                String pair = readPair(structure, row, column);
                if (isVisibleVerticalPipeStart(structure, row, column, pair)) {
                    drawVerticalPipeFromRow(graphics, structure, image, cameraX, sliceHeight, row, column, pair);
                }
            }
        }
    }

    private boolean isVisibleVerticalPipeStart(TextLevelData structure, int row, int column, String pair) {
        if ("[]".equals(pair) || "{]".equals(pair)) {
            return true;
        }
        if (!"pP".equals(pair) && !"eE".equals(pair)) {
            return false;
        }
        if (row == 0) {
            return true;
        }
        String abovePair = readPair(structure, row - 1, column);
        return !"[]".equals(abovePair) && !"{]".equals(abovePair) && !"pP".equals(abovePair);
    }

    private void drawVerticalPipeFromRow(
        GraphicsContext graphics,
        TextLevelData structure,
        Image image,
        double cameraX,
        double sliceHeight,
        int startRow,
        int column,
        String startPair
    ) {
        for (int row = startRow; row < structure.getRowCount(); row++) {
            String pair = row == startRow ? startPair : readPair(structure, row, column);
            if ("[]".equals(pair) || "{]".equals(pair)) {
                drawPipeSegment(graphics, image, cameraX, column, row, 0.0, sliceHeight);
            } else if ("pP".equals(pair)) {
                drawPipeSegment(graphics, image, cameraX, column, row, sliceHeight, sliceHeight);
            } else {
                if ("eE".equals(pair)) {
                    drawPipeSegment(graphics, image, cameraX, column, row, sliceHeight * 2.0, sliceHeight);
                }
                break;
            }
        }
    }

    private void drawSingleColumnPipeShafts(GraphicsContext graphics, TextLevelData structure, Image image, double cameraX, double sliceHeight) {
        double halfWidth = image.getWidth() / 2.0;
        for (int row = 0; row < structure.getRowCount(); row++) {
            for (int column = 0; column < structure.getColumnCount(); column++) {
                if (structure.getCell(row, column) != 'p') {
                    continue;
                }
                if (column + 1 < structure.getColumnCount() && structure.getCell(row, column + 1) == 'P') {
                    continue;
                }
                if (column > 0) {
                    char leftCell = structure.getCell(row, column - 1);
                    if (leftCell == 'H') {
                        drawHorizontalPipeCell(graphics, column, row, cameraX, 1, 0);
                    } else if (leftCell == 'h') {
                        drawHorizontalPipeCell(graphics, column, row, cameraX, 1, 1);
                    }
                }
                graphics.drawImage(
                    image,
                    0.0,
                    sliceHeight,
                    halfWidth,
                    sliceHeight,
                    (column * GameConstants.TILE_SIZE) - cameraX,
                    row * GameConstants.TILE_SIZE,
                    GameConstants.TILE_SIZE,
                    GameConstants.TILE_SIZE
                );
            }
        }
    }

    private String readPair(TextLevelData structure, int row, int column) {
        return "" + structure.getCell(row, column) + structure.getCell(row, column + 1);
    }

    private void drawHorizontalPipes(GraphicsContext graphics, TextLevelData structure, double cameraX, Image image, double sliceHeight) {
        for (int row = 0; row < structure.getRowCount() - 1; row++) {
            for (int column = 0; column < structure.getColumnCount(); column++) {
                char top = structure.getCell(row, column);
                char bottom = structure.getCell(row + 1, column);
                if ((top != '<' && top != ')') || bottom != '>') {
                    continue;
                }
                drawHorizontalPipeCell(graphics, column, row, cameraX, 0, 0);
                drawHorizontalPipeCell(graphics, column, row + 1, cameraX, 0, 1);
                int segmentColumn = column + 1;
                while (segmentColumn < structure.getColumnCount()) {
                    char segmentTop = structure.getCell(row, segmentColumn);
                    char segmentBottom = structure.getCell(row + 1, segmentColumn);
                    if (segmentTop == 'H' && segmentBottom == 'h') {
                        drawHorizontalPipeCell(graphics, segmentColumn, row, cameraX, 1, 0);
                        drawHorizontalPipeCell(graphics, segmentColumn, row + 1, cameraX, 1, 1);
                    } else if (segmentTop == 'F' && segmentBottom == 'f') {
                        if (row > 0 && structure.getCell(row - 1, segmentColumn) == 'p') {
                            drawSingleColumnPipeCell(graphics, image, cameraX, sliceHeight, segmentColumn, row);
                        }
                        drawHorizontalPipeCell(graphics, segmentColumn, row, cameraX, 2, 0);
                        drawHorizontalPipeCell(graphics, segmentColumn, row + 1, cameraX, 2, 1);
                        break;
                    } else {
                        break;
                    }
                    segmentColumn++;
                }
            }
        }
    }

    private void drawSingleColumnPipeCell(GraphicsContext graphics, Image image, double cameraX, double sliceHeight, int column, int row) {
        double halfWidth = image.getWidth() / 2.0;
        graphics.drawImage(
            image,
            0.0,
            sliceHeight,
            halfWidth,
            sliceHeight,
            (column * GameConstants.TILE_SIZE) - cameraX,
            row * GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE
        );
    }
}
