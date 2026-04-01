package edu.mvcc.jcovey.mario.view;

import edu.mvcc.jcovey.mario.model.TextLevelData;

final class MushroomTiles {
    private MushroomTiles() {
    }

    static String topCode(TextLevelData structure, int row, int column) {
        boolean hasLeft = structure.getCell(row, column - 1) == 'T';
        boolean hasRight = structure.getCell(row, column + 1) == 'T';
        if (!hasLeft && hasRight) {
            return "MX";
        }
        if (hasLeft && !hasRight) {
            return "MZ";
        }
        return "MY";
    }

    static String bodyCode(TextLevelData structure, int row, int column) {
        boolean hasLeft = structure.getCell(row, column - 1) == 't';
        boolean hasRight = structure.getCell(row, column + 1) == 't';
        if (!hasLeft) {
            return "JL";
        }
        if (!hasRight) {
            return "JR";
        }
        return "JM";
    }

    static int extraBodyRowsToDraw(TextLevelData structure, int row, int column) {
        if (structure.getCell(row + 1, column) == 't') {
            return 0;
        }
        return structure.getRowCount() - row;
    }
}
