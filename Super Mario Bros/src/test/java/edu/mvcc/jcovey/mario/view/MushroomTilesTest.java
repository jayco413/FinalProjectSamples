package edu.mvcc.jcovey.mario.view;

import edu.mvcc.jcovey.mario.model.TextLevelData;

import java.util.List;

public final class MushroomTilesTest {
    public static void main(String[] args) {
        testTopCodes();
        testBodyCodes();
        testBodyExtensionRows();
        System.out.println("MushroomTilesTest passed");
    }

    private static void testTopCodes() {
        TextLevelData structure = new TextLevelData(List.of("TTT"), 3);
        assertEquals("MX", MushroomTiles.topCode(structure, 0, 0), "left top");
        assertEquals("MY", MushroomTiles.topCode(structure, 0, 1), "middle top");
        assertEquals("MZ", MushroomTiles.topCode(structure, 0, 2), "right top");
    }

    private static void testBodyCodes() {
        TextLevelData structure = new TextLevelData(List.of("ttt"), 3);
        assertEquals("JL", MushroomTiles.bodyCode(structure, 0, 0), "left body");
        assertEquals("JM", MushroomTiles.bodyCode(structure, 0, 1), "middle body");
        assertEquals("JR", MushroomTiles.bodyCode(structure, 0, 2), "right body");
    }

    private static void testBodyExtensionRows() {
        TextLevelData bottomBody = new TextLevelData(
            List.of(
                "---",
                "-t-",
                "---"
            ),
            3
        );
        assertEquals(2, MushroomTiles.extraBodyRowsToDraw(bottomBody, 1, 1), "bottom body should fill through floor row");

        TextLevelData stackedBody = new TextLevelData(
            List.of(
                "-t-",
                "-t-",
                "---"
            ),
            3
        );
        assertEquals(0, MushroomTiles.extraBodyRowsToDraw(stackedBody, 0, 1), "upper body should not extend");
        assertEquals(2, MushroomTiles.extraBodyRowsToDraw(stackedBody, 1, 1), "bottommost body should extend");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected " + expected + " but was " + actual);
        }
    }
}
