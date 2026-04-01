package edu.mvcc.jcovey.mario.view;

import java.nio.file.Files;
import java.nio.file.Path;

public final class MushroomAssetPathsTest {
    public static void main(String[] args) {
        assertExists(Path.of("assets", "game_art", "tiles", "JL_mushroom_body_left_1x1.png"));
        assertExists(Path.of("assets", "game_art", "tiles", "JM_mushroom_body_middle_1x1.png"));
        assertExists(Path.of("assets", "game_art", "tiles", "JR_mushroom_body_right_1x1.png"));
        assertMissing(Path.of("assets", "game_art", "tiles", "PL_mushroom_body_bottom_left_1x1.png"));
        System.out.println("MushroomAssetPathsTest passed");
    }

    private static void assertExists(Path path) {
        if (!Files.exists(path)) {
            throw new AssertionError("Expected asset to exist: " + path);
        }
    }

    private static void assertMissing(Path path) {
        if (Files.exists(path)) {
            throw new AssertionError("Expected old conflicting asset to be removed: " + path);
        }
    }
}
