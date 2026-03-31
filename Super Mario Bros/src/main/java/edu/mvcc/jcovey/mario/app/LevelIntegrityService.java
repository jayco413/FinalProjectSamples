package edu.mvcc.jcovey.mario.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Computes SHA-256 digests for level files on a background thread.
 *
 * @author Jason A. Covey
 */
public final class LevelIntegrityService {
    private LevelIntegrityService() {
    }

    /**
     * Computes a short SHA-256 digest label asynchronously.
     *
     * @param path the file to hash
     * @return a future containing a display-ready digest string
     */
    public static CompletableFuture<String> computeDigestLabel(Path path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] bytes = Files.readAllBytes(path);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(bytes);
                String hex = HexFormat.of().formatHex(hash);
                return hex.substring(0, 12).toUpperCase();
            } catch (IOException | NoSuchAlgorithmException exception) {
                throw new CompletionException(exception);
            }
        });
    }
}
