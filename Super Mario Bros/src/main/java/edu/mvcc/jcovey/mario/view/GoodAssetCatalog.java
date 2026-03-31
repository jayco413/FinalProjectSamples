package edu.mvcc.jcovey.mario.view;

import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GoodAssetCatalog {
    private static final double SOURCE_UNIT_PIXELS = 200.0;
    private final Map<String, GoodAsset> assetsByCode = new HashMap<>();

    public void load(Path directory) {
        assetsByCode.clear();
        if (!Files.exists(directory)) {
            return;
        }

        try (var stream = Files.walk(directory)) {
            stream.filter(Files::isRegularFile)
                .forEach(path -> {
                    String fileName = path.getFileName().toString();
                    if (fileName.length() < 2) {
                        return;
                    }
                    if ("pipe_2x3.png".equalsIgnoreCase(fileName)) {
                        return;
                    }

                    String code = fileName.substring(0, 2).toUpperCase();
                    Image image = new Image(path.toUri().toString());
                    int widthUnits = Math.max(1, (int) Math.round(image.getWidth() / SOURCE_UNIT_PIXELS));
                    int heightUnits = Math.max(1, (int) Math.round(image.getHeight() / SOURCE_UNIT_PIXELS));
                    assetsByCode.put(code, new GoodAsset(code, image, widthUnits, heightUnits));
                });
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load good assets from " + directory, exception);
        }
    }

    public GoodAsset get(String code) {
        return assetsByCode.get(code);
    }
}
