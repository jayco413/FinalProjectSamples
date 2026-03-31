package edu.mvcc.jcovey.mario.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TextLevelLoader {
    public TextLevelData load(Path path) {
        try {
            List<String> rows = Files.readAllLines(path)
                .stream()
                .filter(line -> !line.isBlank())
                .toList();
            int maxColumns = 0;
            for (String row : rows) {
                maxColumns = Math.max(maxColumns, row.length());
            }
            return new TextLevelData(rows, maxColumns);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load level text from " + path, exception);
        }
    }
}
