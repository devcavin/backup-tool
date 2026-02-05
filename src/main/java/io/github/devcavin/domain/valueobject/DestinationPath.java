package io.github.devcavin.domain.valueobject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Objects;

public final class DestinationPath {

    private final Path path;

    public DestinationPath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            throw new IllegalArgumentException("Destination path cannot be null or blank");
        }

        Path resolvedPath = Paths.get(rawPath)
                .toAbsolutePath()
                .normalize();

        // create the backup directory if not existing
        try {
            if (!Files.exists(resolvedPath)) {
                Files.createDirectories(resolvedPath);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed to create destination directory: " + resolvedPath, e
            );
        }

        if (!Files.isReadable(resolvedPath)) {
            throw new IllegalArgumentException("Destination path is not readable: %s".formatted(resolvedPath));
        }

        if (!Files.isWritable(resolvedPath)) {
            throw new IllegalArgumentException("Destination path is not writable: " + resolvedPath);
        }

        this.path = resolvedPath;
    }

    public Path getPath() {
        return path;
    }

    public String asString() {
        return path.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DestinationPath that)) return false;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
