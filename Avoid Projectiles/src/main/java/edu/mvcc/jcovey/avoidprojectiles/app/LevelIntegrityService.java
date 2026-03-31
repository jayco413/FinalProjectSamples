package edu.mvcc.jcovey.avoidprojectiles.app;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.CompletableFuture;

/**
 * Computes a SHA-256 digest for bundled game assets.
 *
 * <p>This provides a small course-appropriate example of cryptography and
 * background work without changing gameplay behavior.</p>
 *
 * @author Jason A. Covey
 */
public final class LevelIntegrityService {
    private static final String[] ASSET_PATHS = {
        "/edu/mvcc/jcovey/avoidprojectiles/assets/images/333223.jpg",
        "/edu/mvcc/jcovey/avoidprojectiles/assets/images/bulletbill.png",
        "/edu/mvcc/jcovey/avoidprojectiles/assets/images/mario.png",
        "/edu/mvcc/jcovey/avoidprojectiles/assets/images/minimushroom.png",
        "/edu/mvcc/jcovey/avoidprojectiles/assets/images/starman.png",
        "/edu/mvcc/jcovey/avoidprojectiles/assets/media/smb_pipe.mp3",
        "/edu/mvcc/jcovey/avoidprojectiles/assets/media/starmantheme.mp3",
        "/edu/mvcc/jcovey/avoidprojectiles/assets/media/whoa.mp3"
    };

    private LevelIntegrityService() {
    }

    /**
     * Computes the bundled-asset digest on a background thread.
     *
     * @return a future that completes with the digest text
     */
    public static CompletableFuture<String> computeDigestAsync() {
        return CompletableFuture.supplyAsync(LevelIntegrityService::computeDigest);
    }

    private static String computeDigest() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (String assetPath : ASSET_PATHS) {
                updateDigest(digest, assetPath);
            }
            return HexFormat.of().formatHex(digest.digest()).substring(0, 16).toUpperCase();
        } catch (NoSuchAlgorithmException | IOException exception) {
            return "Unavailable";
        }
    }

    private static void updateDigest(MessageDigest digest, String assetPath) throws IOException {
        try (InputStream inputStream = LevelIntegrityService.class.getResourceAsStream(assetPath)) {
            if (inputStream == null) {
                throw new IOException("Missing resource: " + assetPath);
            }

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                digest.update(buffer, 0, bytesRead);
            }
        }
    }
}
