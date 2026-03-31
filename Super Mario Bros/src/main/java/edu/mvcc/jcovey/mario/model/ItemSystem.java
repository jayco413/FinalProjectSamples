package edu.mvcc.jcovey.mario.model;

import java.util.Iterator;
import java.util.List;

/**
 * Owns mushrooms, stars, floating collectibles, brick fragments, and fireballs.
 */
final class ItemSystem {
    private final GameModel game;

    ItemSystem(GameModel game) {
        this.game = game;
    }

    void updateMushrooms(double deltaSeconds) {
        List<PhysicsRect> solids = game.getSolidRects();
        Iterator<MushroomModel> iterator = game.getMushrooms().iterator();
        while (iterator.hasNext()) {
            MushroomModel mushroom = iterator.next();
            if (!mushroom.isActive()) {
                iterator.remove();
                continue;
            }

            mushroom.setVelocityY(Math.min(mushroom.getVelocityY() + (GameConstants.GRAVITY * deltaSeconds), GameConstants.TERMINAL_VELOCITY));
            mushroom.setX(mushroom.getX() + (mushroom.getVelocityX() * deltaSeconds));
            for (PhysicsRect solid : solids) {
                if (mushroom.getBounds().intersects(solid)) {
                    if (mushroom.getVelocityX() > 0.0) {
                        mushroom.setX(solid.getX() - mushroom.getWidth());
                    } else {
                        mushroom.setX(solid.getX() + solid.getWidth());
                    }
                    mushroom.setVelocityX(-mushroom.getVelocityX());
                }
            }

            mushroom.setY(mushroom.getY() + (mushroom.getVelocityY() * deltaSeconds));
            for (PhysicsRect solid : solids) {
                if (mushroom.getBounds().intersects(solid)) {
                    if (mushroom.getVelocityY() > 0.0) {
                        mushroom.setY(solid.getY() - mushroom.getHeight());
                    } else {
                        mushroom.setY(solid.getY() + solid.getHeight());
                    }
                    mushroom.setVelocityY(0.0);
                }
            }
        }
    }

    void updateStars(double deltaSeconds) {
        List<PhysicsRect> solids = game.getSolidRects();
        Iterator<StarModel> iterator = game.getStars().iterator();
        while (iterator.hasNext()) {
            StarModel star = iterator.next();
            if (!star.isActive()) {
                iterator.remove();
                continue;
            }

            star.setVelocityY(Math.min(star.getVelocityY() + (GameConstants.GRAVITY * deltaSeconds), GameConstants.TERMINAL_VELOCITY));
            star.setX(star.getX() + (star.getVelocityX() * deltaSeconds));
            for (PhysicsRect solid : solids) {
                if (star.getBounds().intersects(solid)) {
                    if (star.getVelocityX() > 0.0) {
                        star.setX(solid.getX() - star.getWidth());
                    } else {
                        star.setX(solid.getX() + solid.getWidth());
                    }
                    star.setVelocityX(-star.getVelocityX());
                }
            }

            star.setY(star.getY() + (star.getVelocityY() * deltaSeconds));
            for (PhysicsRect solid : solids) {
                if (star.getBounds().intersects(solid)) {
                    if (star.getVelocityY() > 0.0) {
                        star.setY(solid.getY() - star.getHeight());
                        star.setVelocityY(GameConstants.STAR_BOUNCE_SPEED);
                    } else {
                        star.setY(solid.getY() + solid.getHeight());
                        star.setVelocityY(120.0);
                    }
                }
            }

            if (star.getX() < -32.0 || star.getX() > game.currentLevel().getWorldWidth() + 32.0 || star.getY() > 640.0) {
                star.setActive(false);
            }
            if (!star.isActive()) {
                iterator.remove();
            }
        }
    }

    void updateFloatingCoins(double deltaSeconds) {
        Iterator<FloatingCoinModel> iterator = game.getFloatingCoins().iterator();
        while (iterator.hasNext()) {
            FloatingCoinModel coin = iterator.next();
            coin.update(deltaSeconds);
            if (coin.isExpired()) {
                iterator.remove();
            }
        }
    }

    void updateBrickFragments(double deltaSeconds) {
        Iterator<BrickFragmentModel> iterator = game.getBrickFragments().iterator();
        while (iterator.hasNext()) {
            BrickFragmentModel fragment = iterator.next();
            fragment.update(deltaSeconds);
            if (fragment.isExpired()) {
                iterator.remove();
            }
        }
    }

    void updateFireballs(double deltaSeconds) {
        game.setFireballCooldown(Math.max(0.0, game.getFireballCooldown() - deltaSeconds));
        List<PhysicsRect> solids = game.getSolidRects();
        Iterator<FireballModel> iterator = game.getFireballs().iterator();
        while (iterator.hasNext()) {
            FireballModel fireball = iterator.next();
            if (!fireball.isActive()) {
                iterator.remove();
                continue;
            }

            fireball.setVelocityY(Math.min(fireball.getVelocityY() + (GameConstants.GRAVITY * deltaSeconds), GameConstants.TERMINAL_VELOCITY));
            fireball.setX(fireball.getX() + (fireball.getVelocityX() * deltaSeconds));
            for (PhysicsRect solid : solids) {
                if (fireball.getBounds().intersects(solid)) {
                    fireball.setActive(false);
                    break;
                }
            }
            if (!fireball.isActive()) {
                iterator.remove();
                continue;
            }

            fireball.setY(fireball.getY() + (fireball.getVelocityY() * deltaSeconds));
            for (PhysicsRect solid : solids) {
                if (fireball.getBounds().intersects(solid)) {
                    if (fireball.getVelocityY() > 0.0) {
                        fireball.setY(solid.getY() - fireball.getHeight());
                        fireball.setVelocityY(-260.0);
                    } else {
                        fireball.setActive(false);
                    }
                    break;
                }
            }

            if (fireball.getX() < -32.0
                || fireball.getX() > game.currentLevel().getWorldWidth() + 32.0
                || fireball.getY() > GameConstants.WORLD_HEIGHT + 64.0) {
                fireball.setActive(false);
            }
            if (!fireball.isActive()) {
                iterator.remove();
            }
        }
    }

    void resolveCollectibles() {
        MarioModel mario = game.getMario();
        if (!mario.isAlive()) {
            return;
        }
        for (MushroomModel mushroom : game.getMushrooms()) {
            if (mushroom.isActive() && mario.getCollisionBounds().intersects(mushroom.getBounds())) {
                mushroom.setActive(false);
                if ("oneup".equals(mushroom.getType())) {
                    game.addLife();
                    game.addScore(1000);
                    game.emitSound("1up");
                } else if ("fireflower".equals(mushroom.getType())) {
                    double previousHeight = mario.getHeight();
                    double previousWidth = mario.getWidth();
                    mario.setFireForm(true);
                    mario.setX(mario.getX() - (mario.getWidth() - previousWidth));
                    mario.setY(mario.getY() - (mario.getHeight() - previousHeight));
                    game.addScore(1000);
                    game.emitSound("powerup");
                } else {
                    double previousHeight = mario.getHeight();
                    double previousWidth = mario.getWidth();
                    mario.setSuperForm(true);
                    mario.setX(mario.getX() - (mario.getWidth() - previousWidth));
                    mario.setY(mario.getY() - (mario.getHeight() - previousHeight));
                    game.addScore(1000);
                    game.emitSound("powerup");
                }
            }
        }
        for (StarModel star : game.getStars()) {
            if (star.isActive() && mario.getCollisionBounds().intersects(star.getBounds())) {
                star.setActive(false);
                mario.startStarPower(10.0);
                game.addScore(1000);
                game.emitSound("item");
            }
        }
    }

    void resolveCoins() {
        MarioModel mario = game.getMario();
        if (!mario.isAlive()) {
            return;
        }
        for (CoinModel coin : game.currentLevel().getCoins()) {
            if (!coin.isCollected() && mario.getCollisionBounds().intersects(coin.getBounds())) {
                coin.setCollected(true);
                game.awardCoin();
                game.addScore(200);
                game.emitSound("coin");
            }
        }
    }

    void bounceMushroomsAboveBlock(BlockModel block) {
        PhysicsRect bumpZone = new PhysicsRect(
            block.getX() - 4.0,
            block.getY() - GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE + 8.0,
            GameConstants.TILE_SIZE + 12.0
        );
        for (MushroomModel mushroom : game.getMushrooms()) {
            if (!mushroom.isActive() || "fireflower".equals(mushroom.getType())) {
                continue;
            }
            if (mushroom.getBounds().intersects(bumpZone)) {
                mushroom.setY(Math.min(mushroom.getY(), block.getY() - mushroom.getHeight() - 4.0));
                mushroom.setVelocityY(-220.0);
                mushroom.setVelocityX(-mushroom.getVelocityX());
            }
        }
    }
}
