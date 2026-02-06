package io.github.devcavin.infrastructure.archive.local;

import io.github.devcavin.domain.entity.Archive;
import io.github.devcavin.domain.entity.BackupJob;
import io.github.devcavin.domain.service.ArchiveCreator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LocalArchiveCreator implements ArchiveCreator {
    @Override
    public Archive createArchive(BackupJob backupJob) {
        try {
            Path sourcePath = backupJob.getSourcePath().getPath();
            Path tempFile = Files.createTempFile("backup-", ".zip");

            try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempFile.toFile()))) {
                Files.walk(sourcePath)
                        .filter(path -> !Files.isDirectory(sourcePath))
                        .forEach(path -> addToZip(sourcePath, path, zos));
            }

            long size = Files.size(tempFile);

            return new Archive(
                    backupJob.getId(),
                    tempFile,
                    size
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to create archive.", e);
        }
    }

    private void addToZip(Path root, Path file, ZipOutputStream zos) {
        try {
            ZipEntry entry = new ZipEntry(root.relativize(root).toString());
            zos.putNextEntry(entry);
            Files.copy(file, zos);
            zos.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
