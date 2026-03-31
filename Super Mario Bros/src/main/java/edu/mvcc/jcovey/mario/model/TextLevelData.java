package edu.mvcc.jcovey.mario.model;

import java.util.List;

public class TextLevelData {
    private final List<String> rows;
    private final int columnCount;

    public TextLevelData(List<String> rows, int columnCount) {
        this.rows = rows;
        this.columnCount = columnCount;
    }

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return columnCount;
    }

    public char getCell(int row, int column) {
        if (row < 0 || row >= rows.size() || column < 0 || column >= columnCount) {
            return '-';
        }
        String line = rows.get(row);
        if (column >= line.length()) {
            return '-';
        }
        return line.charAt(column);
    }
}
