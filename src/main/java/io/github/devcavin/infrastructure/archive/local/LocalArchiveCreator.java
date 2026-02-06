package io.github.devcavin.infrastructure.archive.local;

import io.github.devcavin.domain.entity.Archive;
import io.github.devcavin.domain.entity.BackupJob;
import io.github.devcavin.domain.service.ArchiveCreator;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LocalArchiveCreator implements ArchiveCreator {

    @Override
    public Archive createArchive(BackupJob job) {
        Path source = job.getSourcePath().getPath();

        try {
            Path tempFile = Files.createTempFile("backup-", ".zip");

            try (Stream<Path> paths = Files.walk(source);
                 ZipOutputStream zos =
                         new ZipOutputStream(Files.newOutputStream(tempFile))) {

                paths
                        .filter(Files::isRegularFile)
                        .forEach(path -> addToZip(source, path, zos));
            }

            long size = Files.size(tempFile);

            return new Archive(
                    job.getId(),
                    tempFile,
                    job.getDestinationPath().getPath(),
                    size
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to create archive", e);
        }
    }

    private void addToZip(Path root, Path file, ZipOutputStream zos) {
        try {
            String entryName = root
                    .relativize(file)
                    .toString()
                    .replace(File.separatorChar, '/');

            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            Files.copy(file, zos);
            zos.closeEntry();

        } catch (IOException e) {
            throw new RuntimeException("Failed to add file to zip: " + file, e);
        }
    }
}