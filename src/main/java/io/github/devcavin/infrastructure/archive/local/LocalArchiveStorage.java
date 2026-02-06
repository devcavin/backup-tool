package io.github.devcavin.infrastructure.archive.local;

import io.github.devcavin.domain.entity.Archive;
import io.github.devcavin.domain.service.ArchiveStorage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LocalArchiveStorage implements ArchiveStorage {
    @Override
    public void store(Archive archive) {
        try {
            Path destinationDirectory = archive.getDestinationDirectory();
            Files.createDirectories(destinationDirectory);

            String filename = String.format("backup-%d.zip", System.currentTimeMillis());
            Path finalPath = destinationDirectory.resolve(filename);

            Files.move(archive.getPath(), finalPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store archive", e);
        }
    }
}