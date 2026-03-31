package edu.mvcc.jcovey.mario.model;

/**
 * Owns block animation and block-hit side effects.
 */
final class BlockSystem {
    private final GameModel game;

    BlockSystem(GameModel game) {
        this.game = game;
    }

    void update(double deltaSeconds) {
        for (BlockModel block : game.currentLevel().getBlocks()) {
            block.update(deltaSeconds);
        }
    }

    void activate(BlockModel block) {
        if ("brick".equals(block.getType()) && game.getMario().isSuperForm()) {
            block.breakBlock();
            spawnBrickFragments(block);
            collectCoinAboveBrokenBrick(block);
            game.addScore(50);
            game.emitSound("brick");
            game.items().bounceMushroomsAboveBlock(block);
            game.enemies().bounceEnemiesAboveBlock(block);
            return;
        }
        block.startBump();
        game.items().bounceMushroomsAboveBlock(block);
        game.enemies().bounceEnemiesAboveBlock(block);
        if ("hidden".equals(block.getType()) && !block.isVisible()) {
            game.addScore(1000);
            game.emitSound("item");
        } else if (block.shouldAwardCoin()) {
            game.getFloatingCoins().add(new FloatingCoinModel(block.getX(), block.getY() - GameConstants.TILE_SIZE));
            game.awardCoin();
            game.addScore(200);
            game.emitSound("coin");
        } else {
            game.emitSound("bump");
        }
        if (block.shouldSpawnMushroom()) {
            String powerType = game.getMario().isSuperForm() ? "fireflower" : "power";
            game.getMushrooms().add(new MushroomModel(block.getX() + 2.0, block.getY() - 28.0, powerType));
            game.emitSound("item");
        }
        if (block.shouldSpawnOneUp()) {
            game.getMushrooms().add(new MushroomModel(block.getX() + 2.0, block.getY() - 28.0, "oneup"));
            game.emitSound("item");
        }
        if (block.shouldSpawnStar()) {
            game.getStars().add(new StarModel(block.getX() + 2.0, block.getY() - 28.0));
            game.emitSound("item");
        }
        block.registerHit();
    }

    private void spawnBrickFragments(BlockModel block) {
        double x = block.getX();
        double y = block.getY();
        double half = GameConstants.TILE_SIZE / 2.0;
        game.getBrickFragments().add(new BrickFragmentModel(x + 4.0, y + 4.0, -110.0, -250.0, 0, 0));
        game.getBrickFragments().add(new BrickFragmentModel(x + half, y + 4.0, 110.0, -250.0, 1, 0));
        game.getBrickFragments().add(new BrickFragmentModel(x + 4.0, y + half, -90.0, -150.0, 0, 1));
        game.getBrickFragments().add(new BrickFragmentModel(x + half, y + half, 90.0, -150.0, 1, 1));
    }

    private void collectCoinAboveBrokenBrick(BlockModel block) {
        PhysicsRect coinZone = new PhysicsRect(
            block.getX(),
            block.getY() - GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE
        );
        for (CoinModel coin : game.currentLevel().getCoins()) {
            if (coin.isCollected() || !coin.getBounds().intersects(coinZone)) {
                continue;
            }
            coin.setCollected(true);
            game.getFloatingCoins().add(new FloatingCoinModel(coin.getX(), coin.getY()));
            game.awardCoin();
            game.addScore(200);
            game.emitSound("coin");
            return;
        }
    }
}
