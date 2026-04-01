package edu.mvcc.jcovey.mario.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameModel {
    private static final Path WORLD_1_1_PATH = Path.of("assets", "layouts", "1-1_structural.txt");
    private static final Path WORLD_1_2_PATH = Path.of("assets", "layouts", "1-2_structural.txt");
    private static final Path WORLD_1_3_PATH = Path.of("assets", "layouts", "1-3_vglc_structural.txt");
    private static final Path WORLD_1_2_EXIT_PATH = Path.of("assets", "layouts", "1-2_exit_area.txt");
    private static final Path BONUS_PATH = Path.of("assets", "layouts", "1-1_bonus_area.txt");

    private final MarioModel mario = new MarioModel();
    private final CampaignLevelModel world1_1Level = new CampaignLevelModel("1-1", 1, "overworld", WORLD_1_1_PATH);
    private final CampaignLevelModel world1_2Level = new CampaignLevelModel("1-2", 2, "underground", WORLD_1_2_PATH);
    private final CampaignLevelModel world1_3Level = new CampaignLevelModel("1-3", 3, "overworld", WORLD_1_3_PATH);
    private final LevelModel world1_2ExitLevel = new LevelModel("overworld-exit", WORLD_1_2_EXIT_PATH);
    private final LevelModel bonusLevel = new LevelModel("bonus", BONUS_PATH);
    private final CameraModel camera = new CameraModel();
    private final EnemySystem enemySystem = new EnemySystem(this);
    private final ItemSystem itemSystem = new ItemSystem(this);
    private final BlockSystem blockSystem = new BlockSystem(this);
    private final List<MushroomModel> mushrooms = new ArrayList<>();
    private final List<StarModel> stars = new ArrayList<>();
    private final List<FloatingCoinModel> floatingCoins = new ArrayList<>();
    private final List<BrickFragmentModel> brickFragments = new ArrayList<>();
    private final List<FireballModel> fireballs = new ArrayList<>();
    private final List<FireworkModel> fireworks = new ArrayList<>();
    private final List<String> pendingSoundEffects = new ArrayList<>();
    private final Map<Integer, CampaignLevelModel> coursesByShortcut = new HashMap<>();
    private final Map<String, CampaignLevelModel> coursesByWorldText = new HashMap<>();
    private CampaignLevelModel selectedStartCourse;
    private CampaignLevelModel currentCourse;
    private LevelModel level;
    private int score;
    private int coinsCollected;
    private int lives;
    private int timeRemaining;
    private double timerAccumulator;
    private double deathRespawnDelaySeconds;
    private double deathRespawnTimer;
    private double fireballCooldown;
    private double pipeWarpCooldown;
    private double fireworkLaunchTimer;
    private int pendingFireworks;
    private boolean courseClearActive;
    private boolean fortressEntered;
    private boolean levelComplete;
    private boolean gameOver;
    private boolean levelClearMusicFinished;

    public GameModel() {
        world1_1Level.setNextLevel(world1_2Level);
        world1_2Level.setNextLevel(world1_3Level);
        registerCourse(world1_1Level);
        registerCourse(world1_2Level);
        registerCourse(world1_3Level);
        reset();
    }

    private void registerCourse(CampaignLevelModel course) {
        coursesByShortcut.put(course.getShortcutDigit(), course);
        coursesByWorldText.put(course.getWorldText(), course);
    }

    public void reset() {
        score = 0;
        coinsCollected = 0;
        lives = 3;
        timeRemaining = 400;
        timerAccumulator = 0.0;
        selectedStartCourse = world1_1Level;
        currentCourse = selectedStartCourse;
        world1_1Level.reset();
        world1_2Level.reset();
        world1_3Level.reset();
        world1_2ExitLevel.reset();
        bonusLevel.reset();
        level = selectedStartCourse;
        restartLevel();
        levelComplete = false;
        gameOver = false;
        emitSound("bump");
    }

    private void restartLevel() {
        world1_1Level.reset();
        world1_2Level.reset();
        world1_3Level.reset();
        world1_2ExitLevel.reset();
        bonusLevel.reset();
        currentCourse = selectedStartCourse;
        level = selectedStartCourse;
        mario.reset();
        camera.reset();
        mushrooms.clear();
        stars.clear();
        floatingCoins.clear();
        brickFragments.clear();
        fireballs.clear();
        fireworks.clear();
        fireballCooldown = 0.0;
        pipeWarpCooldown = 0.0;
        fireworkLaunchTimer = 0.0;
        pendingFireworks = 0;
        courseClearActive = false;
        fortressEntered = false;
        deathRespawnTimer = 0.0;
        timeRemaining = 400;
        timerAccumulator = 0.0;
        levelClearMusicFinished = true;
    }

    public void update(double deltaSeconds, InputState inputState) {
        if (gameOver || levelComplete) {
            camera.update(mario.getCenterX(), level.getWorldWidth());
            return;
        }

        mario.update(deltaSeconds);
        mario.addAnimationTime(deltaSeconds);
        updateFireworks(deltaSeconds);
        pipeWarpCooldown = Math.max(0.0, pipeWarpCooldown - deltaSeconds);
        if (courseClearActive) {
            updateCourseClearSequence(deltaSeconds);
            camera.update(mario.getCenterX(), level.getWorldWidth());
            return;
        }
        updatePlatforms(deltaSeconds);
        updateTimer(deltaSeconds);
        blockSystem.update(deltaSeconds);
        updateMario(deltaSeconds, inputState);
        enemySystem.update(deltaSeconds);
        itemSystem.updateMushrooms(deltaSeconds);
        itemSystem.updateStars(deltaSeconds);
        itemSystem.updateFloatingCoins(deltaSeconds);
        itemSystem.updateBrickFragments(deltaSeconds);
        itemSystem.updateFireballs(deltaSeconds);
        enemySystem.resolveMarioContacts();
        enemySystem.resolveFireballContacts();
        itemSystem.resolveCoins();
        itemSystem.resolveCollectibles();
        resolvePipeWarps(inputState);
        resolveLevelGoal();
        camera.update(mario.getCenterX(), level.getWorldWidth());
    }

    private void updateTimer(double deltaSeconds) {
        if (!mario.isAlive() || mario.isClimbingFlag() || courseClearActive) {
            return;
        }
        timerAccumulator += deltaSeconds;
        while (timerAccumulator >= 1.0 && timeRemaining > 0) {
            timerAccumulator -= 1.0;
            timeRemaining--;
        }
        if (timeRemaining <= 0 && mario.isAlive()) {
            timeRemaining = 0;
            killMario();
        }
    }

    private void updateMario(double deltaSeconds, InputState inputState) {
        if (!mario.isAlive()) {
            mario.setSkidding(false);
            mario.setCrouching(false);
            mario.setVelocityY(Math.min(mario.getVelocityY() + (GameConstants.GRAVITY * deltaSeconds), GameConstants.TERMINAL_VELOCITY));
            mario.setY(mario.getY() + (mario.getVelocityY() * deltaSeconds));
            if (mario.getY() > 640.0) {
                deathRespawnTimer = Math.max(0.0, deathRespawnTimer - deltaSeconds);
                if (deathRespawnTimer <= 0.0) {
                    handleDeathResolved();
                }
            }
            return;
        }

        if (mario.isClimbingFlag()) {
            mario.setSkidding(false);
            mario.setCrouching(false);
            mario.setVelocityX(0.0);
            mario.setVelocityY(120.0);
            mario.setY(mario.getY() + (mario.getVelocityY() * deltaSeconds));
            return;
        }

        double targetSpeed = 0.0;
        double maxSpeed = inputState.isRunPressed() ? GameConstants.RUN_SPEED : GameConstants.WALK_SPEED;
        if (inputState.isLeftPressed()) {
            targetSpeed -= maxSpeed;
            mario.setFacingRight(false);
        }
        if (inputState.isRightPressed()) {
            targetSpeed += maxSpeed;
            mario.setFacingRight(true);
        }
        boolean skidding = mario.isOnGround()
            && ((inputState.isLeftPressed() && mario.getVelocityX() > 30.0)
            || (inputState.isRightPressed() && mario.getVelocityX() < -30.0));
        mario.setSkidding(skidding);
        mario.setCrouching(mario.isSuperForm() && mario.isOnGround() && inputState.isDownPressed() && targetSpeed == 0.0);

        if (targetSpeed != 0.0) {
            double deltaVelocity = GameConstants.ACCELERATION * deltaSeconds;
            if (mario.getVelocityX() < targetSpeed) {
                mario.setVelocityX(Math.min(targetSpeed, mario.getVelocityX() + deltaVelocity));
            } else if (mario.getVelocityX() > targetSpeed) {
                mario.setVelocityX(Math.max(targetSpeed, mario.getVelocityX() - deltaVelocity));
            }
        } else {
            double friction = GameConstants.FRICTION * deltaSeconds;
            if (mario.getVelocityX() > 0.0) {
                mario.setVelocityX(Math.max(0.0, mario.getVelocityX() - friction));
            } else if (mario.getVelocityX() < 0.0) {
                mario.setVelocityX(Math.min(0.0, mario.getVelocityX() + friction));
            }
        }

        if (mario.isOnGround() && inputState.consumeJumpPress()) {
            mario.setVelocityY(-GameConstants.JUMP_SPEED);
            mario.setOnGround(false);
            emitSound(mario.isSuperForm() ? "jump_big" : "jump_small");
        }

        if (mario.isFireForm() && inputState.consumeRunPress()) {
            spawnFireball();
        }

        if (!inputState.isJumpPressed() && mario.getVelocityY() < -300.0) {
            mario.setVelocityY(-300.0);
        }

        mario.setVelocityY(Math.min(mario.getVelocityY() + (GameConstants.GRAVITY * deltaSeconds), GameConstants.TERMINAL_VELOCITY));

        mario.setX(mario.getX() + (mario.getVelocityX() * deltaSeconds));
        resolveHorizontalTerrain();
        if (mario.getX() < 0.0) {
            mario.setX(0.0);
            mario.setVelocityX(0.0);
        }
        double maxMarioX = level.getWorldWidth() - mario.getCollisionWidth() - mario.getCollisionInsetLeft();
        if (mario.getX() > maxMarioX) {
            mario.setX(maxMarioX);
            mario.setVelocityX(0.0);
        }

        mario.setOnGround(false);
        mario.setY(mario.getY() + (mario.getVelocityY() * deltaSeconds));
        resolveVerticalTerrain();

        if (mario.getY() > 640.0) {
            killMario();
        }
    }

    private void spawnFireball() {
        if (fireballCooldown > 0.0 || fireballs.size() >= 2) {
            return;
        }
        double spawnX = mario.isFacingRight() ? mario.getX() + mario.getWidth() - 4.0 : mario.getX() - 12.0;
        double spawnY = mario.getY() + 18.0;
        fireballs.add(new FireballModel(spawnX, spawnY, mario.isFacingRight()));
        fireballCooldown = 0.24;
        emitSound("fireball");
    }

    private void updateFireworks(double deltaSeconds) {
        Iterator<FireworkModel> iterator = fireworks.iterator();
        while (iterator.hasNext()) {
            FireworkModel firework = iterator.next();
            firework.update(deltaSeconds);
            if (firework.isExpired()) {
                iterator.remove();
            }
        }
    }

    private void updatePlatforms(double deltaSeconds) {
        for (PlatformModel platform : level.getPlatforms()) {
            double previousTop = platform.getY();
            platform.update(deltaSeconds);
            if (!mario.isAlive() || mario.isClimbingFlag()) {
                continue;
            }
            boolean ridingPlatform = mario.isOnGround()
                && Math.abs(mario.getFeetY() - previousTop) <= 8.0
                && mario.getCollisionBounds().getX() < platform.getX() + platform.getWidth()
                && mario.getCollisionBounds().getX() + mario.getCollisionBounds().getWidth() > platform.getX();
            if (ridingPlatform) {
                mario.setY(mario.getY() + platform.getDeltaY());
            }
        }
    }

    private void resolveLevelGoal() {
        if (!"overworld".equals(level.getAreaId()) && !"overworld-exit".equals(level.getAreaId())) {
            return;
        }
        if (!mario.isAlive()) {
            return;
        }
        FlagPoleModel flagPole = level.getFlagPole();
        if (flagPole == null) {
            return;
        }
        if (!mario.isClimbingFlag() && mario.getCollisionBounds().intersects(flagPole.getZone())) {
            mario.setClimbingFlag(true);
            mario.setVelocityX(0.0);
            mario.setVelocityY(120.0);
            mario.setX(flagPole.getX() - 20.0);
            flagPole.trigger(mario.getY());
            score += getFlagPoleScore(flagPole, mario);
            emitSound("flagpole");
        }
        if (mario.isClimbingFlag()) {
            flagPole.followMario(mario.getY());
            if (mario.getFeetY() >= GameConstants.GROUND_TOP) {
                mario.setClimbingFlag(false);
                mario.setY(GameConstants.GROUND_TOP - mario.getHeight());
                mario.setOnGround(true);
                mario.setVelocityX(100.0);
                mario.setVelocityY(0.0);
                courseClearActive = true;
                levelClearMusicFinished = false;
            } else {
                mario.setY(mario.getY() + 1.5);
            }
        }
    }

    private int getFlagPoleScore(FlagPoleModel flagPole, MarioModel mario) {
        double poleHeight = Math.max(1.0, flagPole.getBottomY() - flagPole.getTopY());
        double normalizedHeight = 1.0 - ((mario.getY() - flagPole.getTopY()) / poleHeight);
        if (normalizedHeight >= 0.8) {
            return 5000;
        }
        if (normalizedHeight >= 0.6) {
            return 2000;
        }
        if (normalizedHeight >= 0.4) {
            return 800;
        }
        if (normalizedHeight >= 0.2) {
            return 400;
        }
        return 100;
    }

    private void resolvePipeWarps(InputState inputState) {
        if (!mario.isAlive() || mario.isClimbingFlag() || pipeWarpCooldown > 0.0 || !mario.isOnGround()) {
            return;
        }
        for (PipeModel pipe : level.getPipes()) {
            if (pipe.getWarpId().isEmpty()) {
                continue;
            }
            if (marioCanEnterPipe(pipe, inputState)) {
                enterPipeWarp(pipe);
                return;
            }
        }
    }

    private boolean marioCanEnterPipe(PipeModel pipe, InputState inputState) {
        if ("horizontal-left".equals(pipe.getOrientation())) {
            if (!inputState.isRightPressed()) {
                return false;
            }
            PhysicsRect sideEntryZone = new PhysicsRect(
                pipe.getX() - 8.0,
                pipe.getY() + (GameConstants.TILE_SIZE * 0.5),
                GameConstants.TILE_SIZE * 1.25,
                GameConstants.TILE_SIZE * 1.5
            );
            return mario.getCollisionBounds().intersects(sideEntryZone);
        }
        if (!inputState.isDownPressed()) {
            return false;
        }
        boolean standingOnTop = Math.abs(mario.getFeetY() - pipe.getY()) <= 6.0;
        double horizontalCenterDistance = Math.abs(mario.getCenterX() - pipe.getCenterX());
        return standingOnTop && horizontalCenterDistance <= 16.0;
    }

    private void enterPipeWarp(PipeModel pipe) {
        emitSound("pipepowerdown");
        if ("secret-entry".equals(pipe.getWarpId())) {
            bonusLevel.reset();
            level = bonusLevel;
            placeMarioInBonusRoom();
        } else if ("secret-exit".equals(pipe.getWarpId())) {
            world1_1Level.reset();
            level = world1_1Level;
            placeMarioAtAreaSpawn(world1_1Level);
        } else if ("exit-area".equals(pipe.getWarpId())) {
            world1_2ExitLevel.reset();
            level = world1_2ExitLevel;
            placeMarioAtAreaSpawn(world1_2ExitLevel);
        }
        mushrooms.clear();
        stars.clear();
        floatingCoins.clear();
        brickFragments.clear();
        fireballs.clear();
        fireballCooldown = 0.0;
        pipeWarpCooldown = 0.45;
        camera.reset();
    }

    private void placeMarioInBonusRoom() {
        placeMarioAtAreaSpawn(level);
    }

    private void placeMarioAtAreaSpawn(LevelModel targetLevel) {
        mario.setVelocityX(0.0);
        mario.setVelocityY(0.0);
        mario.setOnGround(false);
        mario.setX(targetLevel.getSpawnX() - Math.max(0.0, mario.getWidth() - 32.0));
        mario.setY(targetLevel.getSpawnY() - Math.max(0.0, mario.getHeight() - 32.0));
    }

    private void placeMarioOnPipe(PipeModel pipe) {
        mario.setVelocityX(0.0);
        mario.setVelocityY(0.0);
        mario.setOnGround(true);
        mario.setX((pipe.getCenterX() - (mario.getCollisionWidth() / 2.0)) - mario.getCollisionInsetLeft());
        mario.setY(pipe.getY() - mario.getHeight());
    }

    private void resolveHorizontalTerrain() {
        for (PhysicsRect solid : getSolidRects()) {
            if (mario.getCollisionBounds().intersects(solid)) {
                if (mario.getVelocityX() > 0.0) {
                    mario.setX(solid.getX() - mario.getCollisionWidth() - mario.getCollisionInsetLeft());
                } else if (mario.getVelocityX() < 0.0) {
                    mario.setX(solid.getX() + solid.getWidth() - mario.getCollisionInsetLeft());
                }
                mario.setVelocityX(0.0);
            }
        }
    }

    private void resolveVerticalTerrain() {
        for (PhysicsRect solid : level.getTerrain()) {
            if (mario.getCollisionBounds().intersects(solid)) {
                resolveVerticalCollision(solid, null);
            }
        }
        for (BlockModel block : level.getBlocks()) {
            boolean activeForCollision = block.canCollide()
                || (mario.getVelocityY() < 0.0 && block.isRevealableHiddenBlock());
            if (activeForCollision && mario.getCollisionBounds().intersects(block.getBounds())) {
                resolveVerticalCollision(block.getBounds(), block);
            }
        }
        for (PlatformModel platform : level.getPlatforms()) {
            if (mario.getCollisionBounds().intersects(platform.getBounds())) {
                resolveVerticalCollision(platform.getBounds(), null);
            }
        }
    }

    private void resolveVerticalCollision(PhysicsRect solid, BlockModel block) {
        if (mario.getVelocityY() > 0.0) {
            mario.setY(solid.getY() - mario.getCollisionHeight() - mario.getCollisionInsetTop());
            mario.setVelocityY(0.0);
            mario.setOnGround(true);
        } else if (mario.getVelocityY() < 0.0) {
            if (block != null && "brick".equals(block.getType()) && mario.isSuperForm()) {
                mario.setY(solid.getY() + solid.getHeight() - mario.getCollisionInsetTop());
                mario.setVelocityY(140.0);
                blockSystem.activate(block);
                return;
            }
            mario.setY(solid.getY() + solid.getHeight() - mario.getCollisionInsetTop());
            mario.setVelocityY(0.0);
            if (block != null) {
                blockSystem.activate(block);
            }
        }
    }

    List<PhysicsRect> getSolidRects() {
        List<PhysicsRect> solids = new ArrayList<>(level.getTerrain());
        for (BlockModel block : level.getBlocks()) {
            if (block.canCollide()) {
                solids.add(block.getBounds());
            }
        }
        return solids;
    }

    private void killMario() {
        if (!mario.isAlive()) {
            return;
        }
        mario.setAlive(false);
        mario.setVelocityX(0.0);
        mario.setVelocityY(-520.0);
        deathRespawnTimer = Math.max(0.1, deathRespawnDelaySeconds);
        emitSound("death");
    }

    void handleMarioHit() {
        if (mario.isFireForm() || mario.isSuperForm()) {
            double previousHeight = mario.getHeight();
            double previousWidth = mario.getWidth();
            mario.setFireForm(false);
            mario.setSuperForm(false);
            mario.setX(mario.getX() + (previousWidth - mario.getWidth()));
            mario.setY(mario.getY() + (previousHeight - mario.getHeight()));
            mario.startInvincibility(1.0);
            emitSound("pipepowerdown");
            return;
        }
        killMario();
    }

    private void handleDeathResolved() {
        lives--;
        if (lives > 0) {
            restartLevel();
        } else {
            gameOver = true;
            emitSound("gameover");
        }
    }

    public List<String> drainSoundEffects() {
        List<String> drained = new ArrayList<>(pendingSoundEffects);
        pendingSoundEffects.clear();
        return drained;
    }

    EnemySystem enemies() {
        return enemySystem;
    }

    ItemSystem items() {
        return itemSystem;
    }

    LevelModel currentLevel() {
        return level;
    }

    void addScore(int amount) {
        score += amount;
    }

    void addLife() {
        lives++;
    }

    double getFireballCooldown() {
        return fireballCooldown;
    }

    void setFireballCooldown(double fireballCooldown) {
        this.fireballCooldown = fireballCooldown;
    }

    public MarioModel getMario() {
        return mario;
    }

    public LevelModel getLevel() {
        return level;
    }

    public CameraModel getCamera() {
        return camera;
    }

    public List<MushroomModel> getMushrooms() {
        return mushrooms;
    }

    public List<StarModel> getStars() {
        return stars;
    }

    public List<FloatingCoinModel> getFloatingCoins() {
        return floatingCoins;
    }

    public List<BrickFragmentModel> getBrickFragments() {
        return brickFragments;
    }

    public List<FireballModel> getFireballs() {
        return fireballs;
    }

    public List<FireworkModel> getFireworks() {
        return fireworks;
    }

    public String getScoreText() {
        return String.format("%06d", score);
    }

    public String getCoinText() {
        return String.format("%02d", coinsCollected);
    }

    public String getLivesText() {
        return String.format("%02d", lives);
    }

    public String getTimeText() {
        return String.format("%03d", timeRemaining);
    }

    public String getWorldText() {
        return currentCourse.getWorldText();
    }

    public String getBannerText() {
        if (courseClearActive || levelComplete) {
            return "Level Clear";
        }
        if (gameOver) {
            return "Game Over";
        }
        if (!mario.isAlive()) {
            return "Mario Down";
        }
        if ("bonus".equals(level.getAreaId())) {
            return "Bonus Area";
        }
        return "World " + currentCourse.getWorldText();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isLevelComplete() {
        return levelComplete;
    }

    public boolean isCourseClearActive() {
        return courseClearActive;
    }

    public void setDeathRespawnDelaySeconds(double deathRespawnDelaySeconds) {
        this.deathRespawnDelaySeconds = deathRespawnDelaySeconds;
    }

    public void setLevelClearMusicFinished(boolean levelClearMusicFinished) {
        this.levelClearMusicFinished = levelClearMusicFinished;
    }

    public void loadWorld1_1() {
        loadCourseByShortcut(1);
    }

    public void loadWorld1_2() {
        loadCourseByShortcut(2);
    }

    public void loadWorld1_3() {
        loadCourseByShortcut(3);
    }

    public boolean loadCourseByShortcut(int shortcutDigit) {
        CampaignLevelModel course = coursesByShortcut.get(shortcutDigit);
        if (course == null) {
            return false;
        }
        loadCourse(course);
        return true;
    }

    public boolean loadCourseByWorldText(String worldText) {
        CampaignLevelModel course = coursesByWorldText.get(worldText);
        if (course == null) {
            return false;
        }
        loadCourse(course);
        return true;
    }

    private void loadCourse(CampaignLevelModel course) {
        selectedStartCourse = course;
        score = 0;
        coinsCollected = 0;
        lives = 3;
        gameOver = false;
        levelComplete = false;
        restartLevel();
        emitSound("bump");
    }

    void emitSound(String effectKey) {
        pendingSoundEffects.add(effectKey);
    }

    void awardCoin() {
        coinsCollected++;
        if (coinsCollected >= 100) {
            coinsCollected -= 100;
            lives++;
            emitSound("1up");
        }
    }

    private void updateCourseClearSequence(double deltaSeconds) {
        FortressModel fortress = level.getFortress();
        if (fortress == null) {
            levelComplete = true;
            return;
        }
        mario.setFacingRight(true);
        mario.setOnGround(true);
        mario.setY(GameConstants.GROUND_TOP - mario.getHeight());
        if (!fortressEntered) {
            mario.setVelocityX(90.0);
            mario.setX(mario.getX() + (mario.getVelocityX() * deltaSeconds));
            if (mario.getCollisionBounds().intersects(fortress.getDoorwayZone()) || mario.getCenterX() >= fortress.getDoorwayCenterX()) {
                mario.setHidden(true);
                mario.setVelocityX(0.0);
                fortressEntered = true;
                pendingFireworks = getFireworksCountForTimer();
                fireworkLaunchTimer = 0.0;
            }
            return;
        }

        if (pendingFireworks > 0) {
            fireworkLaunchTimer -= deltaSeconds;
            if (fireworkLaunchTimer <= 0.0) {
                launchFirework(fortress, pendingFireworks);
                pendingFireworks--;
                fireworkLaunchTimer = 0.55;
            }
            return;
        }

        if (fireworks.isEmpty() && levelClearMusicFinished) {
            finishCourseClear();
        }
    }

    private void finishCourseClear() {
        CampaignLevelModel nextLevel = currentCourse.getNextLevel();
        if (nextLevel == null) {
            levelComplete = true;
            return;
        }
        advanceToCourse(nextLevel);
    }

    private void advanceToCourse(CampaignLevelModel nextLevel) {
        boolean wasSuperForm = mario.isSuperForm();
        boolean wasFireForm = mario.isFireForm();
        nextLevel.reset();
        bonusLevel.reset();
        currentCourse = nextLevel;
        selectedStartCourse = nextLevel;
        level = nextLevel;
        mario.reset();
        if (wasFireForm) {
            mario.setFireForm(true);
        } else if (wasSuperForm) {
            mario.setSuperForm(true);
        }
        mario.setHidden(false);
        placeMarioAtAreaSpawn(level);
        camera.reset();
        mushrooms.clear();
        stars.clear();
        floatingCoins.clear();
        brickFragments.clear();
        fireballs.clear();
        fireworks.clear();
        fireballCooldown = 0.0;
        pipeWarpCooldown = 0.0;
        fireworkLaunchTimer = 0.0;
        pendingFireworks = 0;
        courseClearActive = false;
        fortressEntered = false;
        timeRemaining = 400;
        timerAccumulator = 0.0;
    }

    private int getFireworksCountForTimer() {
        int lastDigit = timeRemaining % 10;
        if (lastDigit == 1 || lastDigit == 3 || lastDigit == 6) {
            return lastDigit;
        }
        return 0;
    }

    private void launchFirework(FortressModel fortress, int indexFromEnd) {
        int launchedIndex = getFireworksCountForTimer() - indexFromEnd;
        double[] offsets = { 40.0, 92.0, 148.0, 204.0, 260.0, 316.0 };
        double x = fortress.getX() + offsets[Math.min(launchedIndex, offsets.length - 1)];
        x = Math.min(x, level.getWorldWidth() - 28.0);
        double y = fortress.getY() - ((launchedIndex % 2 == 0) ? 84.0 : 132.0);
        fireworks.add(new FireworkModel(x, y));
        score += 500;
        emitSound("firework");
    }
}
