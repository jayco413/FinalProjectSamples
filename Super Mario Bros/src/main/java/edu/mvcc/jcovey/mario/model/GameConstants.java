package edu.mvcc.jcovey.mario.model;

public class GameConstants {
    public static final double TILE_SIZE = 32.0;
    public static final int VIEWPORT_COLUMNS = 16;
    public static final int VIEWPORT_ROWS = 15;
    public static final int HUD_ROWS = 2;
    public static final double VIEWPORT_WIDTH = TILE_SIZE * VIEWPORT_COLUMNS;
    public static final double VIEWPORT_HEIGHT = TILE_SIZE * VIEWPORT_ROWS;
    public static final double WORLD_HEIGHT = VIEWPORT_HEIGHT;
    public static final double GROUND_TOP = 13.0 * TILE_SIZE;
    public static final double GRAVITY = 1250.0;
    public static final double WALK_SPEED = 210.0;
    public static final double RUN_SPEED = 340.0;
    public static final double ACCELERATION = 1800.0;
    public static final double FRICTION = 2200.0;
    public static final double JUMP_SPEED = 680.0;
    public static final double ENEMY_SPEED = 70.0;
    public static final double MUSHROOM_SPEED = 85.0;
    public static final double STAR_SPEED = 110.0;
    public static final double STAR_BOUNCE_SPEED = -430.0;
    public static final double FIREBALL_SPEED = 280.0;
    public static final double TERMINAL_VELOCITY = 1050.0;

    private GameConstants() {
    }
}
