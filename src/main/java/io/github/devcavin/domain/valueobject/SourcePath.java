package io.github.devcavin.domain.valueobject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class SourcePath {
    private final Path path;

    public SourcePath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            throw new IllegalArgumentException("Source path cannot be null or blank");
        }

        Path resolvePath = Paths.get(rawPath).toAbsolutePath().normalize();

        if (!Files.exists(resolvePath)) {
            throw new IllegalArgumentException("Source path does not exist: %s".formatted(resolvePath));
        }

        if (!Files.isReadable(resolvePath)) {
            throw new IllegalArgumentException("Source path is not readable: %s".formatted(resolvePath));
        }

        if (!Files.isDirectory(resolvePath)) {
            throw new IllegalArgumentException("Source path is not a directory: %s".formatted(resolvePath));
        }

        if (isDirectoryEmpty(resolvePath)) {
            throw new IllegalArgumentException("Source directory is empty: %s".formatted(resolvePath));
        }

        this.path = resolvePath;
    }

    private boolean isDirectoryEmpty(Path directory) {
        try (Stream<Path> entries = Files.list(directory)) {
            return entries.findFirst().isEmpty();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read source directory: %s".formatted(directory), e);
        }
    }

    public Path getPath() {
        return path;
    }

    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    public boolean isFile() {
        return Files.isRegularFile(path);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof SourcePath that)) return false;
        return path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path);
    }

    @Override
    public String toString() {
        return path.toString();
    }
}