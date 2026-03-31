package edu.mvcc.jcovey.mario.model;

import java.util.List;

/**
 * Owns enemy movement and enemy-related collision rules.
 */
final class EnemySystem {
    private final GameModel game;

    EnemySystem(GameModel game) {
        this.game = game;
    }

    void update(double deltaSeconds) {
        LevelModel level = game.currentLevel();
        List<PhysicsRect> solids = game.getSolidRects();
        for (EnemyModel enemy : level.getEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }
            if (enemy.isInDefeatAnimation()) {
                enemy.updateDefeatAnimation(deltaSeconds);
                continue;
            }
            if (!enemy.isActivated() && shouldActivate(enemy)) {
                enemy.setActivated(true);
            }
            if (!enemy.isActivated()) {
                continue;
            }
            if (enemy.isFlattened()) {
                enemy.tickFlattened(deltaSeconds);
                continue;
            }

            enemy.updateParakoopa(deltaSeconds);
            enemy.setVelocityY(Math.min(enemy.getVelocityY() + (GameConstants.GRAVITY * deltaSeconds), GameConstants.TERMINAL_VELOCITY));
            enemy.setX(enemy.getX() + (enemy.getVelocityX() * deltaSeconds));

            for (PhysicsRect solid : solids) {
                if (enemy.getBounds().intersects(solid)) {
                    if (enemy.getVelocityX() > 0.0) {
                        enemy.setX(solid.getX() - enemy.getWidth());
                    } else {
                        enemy.setX(solid.getX() + solid.getWidth());
                    }
                    enemy.setVelocityX(-enemy.getVelocityX());
                }
            }
            resolveEnemyToEnemyHorizontalCollisions(enemy);

            enemy.setY(enemy.getY() + (enemy.getVelocityY() * deltaSeconds));
            enemy.setOnGround(false);
            for (PhysicsRect solid : solids) {
                if (enemy.getBounds().intersects(solid)) {
                    if (enemy.getVelocityY() > 0.0) {
                        enemy.setY(solid.getY() - enemy.getHeight());
                        enemy.setOnGround(true);
                    } else {
                        enemy.setY(solid.getY() + solid.getHeight());
                    }
                    enemy.setVelocityY(0.0);
                }
            }

            if (enemy.getX() < 0.0 || enemy.getX() > level.getWorldWidth() || enemy.getY() > 640.0) {
                enemy.setAlive(false);
            }
        }
    }

    void resolveMarioContacts() {
        MarioModel mario = game.getMario();
        if (!mario.isAlive() || mario.isInvincible()) {
            return;
        }

        for (EnemyModel enemy : game.currentLevel().getEnemies()) {
            if (!enemy.isAlive() || !enemy.isActivated() || enemy.isFlattened() || enemy.isInDefeatAnimation()) {
                continue;
            }
            if (!mario.getCollisionBounds().intersects(enemy.getBounds())) {
                continue;
            }

            boolean stomp = mario.getVelocityY() > 0.0 && mario.getFeetY() - enemy.getY() < 20.0;
            if (mario.hasStarPower()) {
                enemy.setAlive(false);
                game.addScore(200);
                game.emitSound("kickkill");
            } else if (stomp) {
                if (enemy.isParakoopa()) {
                    enemy.knockDownToKoopa();
                } else {
                    enemy.flatten();
                }
                mario.setVelocityY(-300.0);
                game.addScore(100);
                game.emitSound("stomp");
            } else {
                game.handleMarioHit();
                return;
            }
        }
    }

    void resolveFireballContacts() {
        for (FireballModel fireball : game.getFireballs()) {
            if (!fireball.isActive()) {
                continue;
            }
            for (EnemyModel enemy : game.currentLevel().getEnemies()) {
                if (!enemy.isAlive() || !enemy.isActivated() || enemy.isFlattened() || enemy.isInDefeatAnimation()) {
                    continue;
                }
                if (fireball.getBounds().intersects(enemy.getBounds())) {
                    enemy.defeatByFireball();
                    fireball.setActive(false);
                    game.addScore(200);
                    game.emitSound("kickkill");
                    break;
                }
            }
        }
    }

    void bounceEnemiesAboveBlock(BlockModel block) {
        PhysicsRect bumpZone = new PhysicsRect(
            block.getX() - 4.0,
            block.getY() - GameConstants.TILE_SIZE,
            GameConstants.TILE_SIZE + 8.0,
            GameConstants.TILE_SIZE + 12.0
        );
        for (EnemyModel enemy : game.currentLevel().getEnemies()) {
            if (!enemy.isAlive() || enemy.isFlattened() || enemy.isInDefeatAnimation()) {
                continue;
            }
            if (!enemy.getBounds().intersects(bumpZone)) {
                continue;
            }
            enemy.defeatByBump();
            game.addScore(100);
            game.emitSound("kickkill");
        }
    }

    private boolean shouldActivate(EnemyModel enemy) {
        double cameraLeft = game.getCamera().getX();
        double cameraRight = cameraLeft + GameConstants.VIEWPORT_WIDTH;
        return enemy.getX() < cameraRight && enemy.getX() + enemy.getWidth() > cameraLeft;
    }

    private void resolveEnemyToEnemyHorizontalCollisions(EnemyModel movingEnemy) {
        for (EnemyModel otherEnemy : game.currentLevel().getEnemies()) {
            if (otherEnemy == movingEnemy) {
                continue;
            }
            if (!otherEnemy.isAlive() || !otherEnemy.isActivated() || otherEnemy.isFlattened() || otherEnemy.isInDefeatAnimation()) {
                continue;
            }
            if (!movingEnemy.getBounds().intersects(otherEnemy.getBounds())) {
                continue;
            }

            if (movingEnemy.getVelocityX() > 0.0) {
                movingEnemy.setX(otherEnemy.getX() - movingEnemy.getWidth());
            } else if (movingEnemy.getVelocityX() < 0.0) {
                movingEnemy.setX(otherEnemy.getX() + otherEnemy.getWidth());
            }
            movingEnemy.setVelocityX(-movingEnemy.getVelocityX());
            otherEnemy.setVelocityX(-otherEnemy.getVelocityX());
            return;
        }
    }
}
