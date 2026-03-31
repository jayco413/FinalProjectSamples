package edu.mvcc.jcovey.mario.tools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * Splits the Mario sprite sheets into individual cropped pose PNGs.
 */
public final class MarioSpriteSheetExporter {
    private static final double ALPHA_THRESHOLD = 0.10;

    private MarioSpriteSheetExporter() {
    }

    public static void main(String[] args) throws IOException {
        exportSheet(
            Path.of("assets", "game_art", "mario", "small", "small_mario.png"),
            new String[] { "idle", "run1", "run2", "run3", "jump", "skid" },
            false,
            "small"
        );
        exportSheet(
            Path.of("assets", "game_art", "mario", "big", "big_mario.png"),
            new String[] { "idle", "run1", "run3", "run2", "jump", "crouch" },
            true,
            "big"
        );
        exportSheet(
            Path.of("assets", "game_art", "mario", "fire", "fire_mario.png"),
            new String[] { "idle", "run1", "run3", "run2", "jump", "crouch" },
            true,
            "fire"
        );
    }

    private static void exportSheet(Path sourcePath, String[] poseNames, boolean cleanCheckerboard, String prefix) throws IOException {
        BufferedImage source = ImageIO.read(sourcePath.toFile());
        if (source == null) {
            throw new IOException("Failed to load image: " + sourcePath);
        }
        if (cleanCheckerboard) {
            source = removeCheckerboardBackground(source);
        }

        int columns = 3;
        int rows = 2;
        int cellWidth = source.getWidth() / columns;
        int cellHeight = source.getHeight() / rows;
        Path outputDir = sourcePath.getParent();
        Files.createDirectories(outputDir);

        for (int i = 0; i < poseNames.length; i++) {
            int column = i % columns;
            int row = i / columns;
            BufferedImage cell = source.getSubimage(column * cellWidth, row * cellHeight, cellWidth, cellHeight);
            BufferedImage cropped = cropToOpaqueBounds(cell);
            Path outputPath = outputDir.resolve(prefix + "_" + poseNames[i] + ".png");
            ImageIO.write(cropped, "png", outputPath.toFile());
            System.out.println(outputPath);
        }
    }

    private static BufferedImage cropToOpaqueBounds(BufferedImage source) {
        int minX = source.getWidth();
        int minY = source.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int alpha = (source.getRGB(x, y) >>> 24) & 0xFF;
                if ((alpha / 255.0) > ALPHA_THRESHOLD) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return source;
        }

        BufferedImage cropped = new BufferedImage((maxX - minX) + 1, (maxY - minY) + 1, BufferedImage.TYPE_INT_ARGB);
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                cropped.setRGB(x - minX, y - minY, source.getRGB(x, y));
            }
        }
        return cropped;
    }

    private static BufferedImage removeCheckerboardBackground(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage cleaned = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        boolean[][] visited = new boolean[height][width];
        int[][] queue = new int[width * height][2];
        int head = 0;
        int tail = 0;

        for (int x = 0; x < width; x++) {
            tail = enqueueIfBackground(source, visited, queue, tail, x, 0);
            tail = enqueueIfBackground(source, visited, queue, tail, x, height - 1);
        }
        for (int y = 0; y < height; y++) {
            tail = enqueueIfBackground(source, visited, queue, tail, 0, y);
            tail = enqueueIfBackground(source, visited, queue, tail, width - 1, y);
        }

        while (head < tail) {
            int x = queue[head][0];
            int y = queue[head][1];
            head++;

            tail = enqueueIfBackground(source, visited, queue, tail, x + 1, y);
            tail = enqueueIfBackground(source, visited, queue, tail, x - 1, y);
            tail = enqueueIfBackground(source, visited, queue, tail, x, y + 1);
            tail = enqueueIfBackground(source, visited, queue, tail, x, y - 1);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = source.getRGB(x, y);
                if (visited[y][x]) {
                    cleaned.setRGB(x, y, 0x00000000);
                } else {
                    cleaned.setRGB(x, y, argb);
                }
            }
        }
        return cleaned;
    }

    private static int enqueueIfBackground(
        BufferedImage source,
        boolean[][] visited,
        int[][] queue,
        int tail,
        int x,
        int y
    ) {
        if (x < 0 || y < 0 || x >= source.getWidth() || y >= source.getHeight() || visited[y][x]) {
            return tail;
        }
        if (!isCheckerboardColor(new Color(source.getRGB(x, y), true))) {
            return tail;
        }
        visited[y][x] = true;
        queue[tail][0] = x;
        queue[tail][1] = y;
        return tail + 1;
    }

    private static boolean isCheckerboardColor(Color color) {
        if (color.getAlpha() < 252) {
            return false;
        }
        double red = color.getRed() / 255.0;
        double green = color.getGreen() / 255.0;
        double blue = color.getBlue() / 255.0;
        double maxChannel = Math.max(red, Math.max(green, blue));
        double minChannel = Math.min(red, Math.min(green, blue));
        return maxChannel >= 0.90 && (maxChannel - minChannel) <= 0.08;
    }
}
