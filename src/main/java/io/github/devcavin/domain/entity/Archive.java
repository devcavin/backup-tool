package io.github.devcavin.domain.entity;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Archive {
    private final UUID id;
    private final UUID backupJobId;
    private final Path path;
    private final Long sizeInBytes;
    private final Instant creationAt;


    public Archive(UUID backupJobId, Path path, Long sizeInBytes) {
        this.id = UUID.randomUUID();
        this.backupJobId = Objects.requireNonNull(backupJobId);
        this.path = path;
        this.sizeInBytes = sizeInBytes;
        this.creationAt = Instant.now();
    }

    public Path getPath() {
        return path;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public Instant getCreationAt() {
        return creationAt;
    }

    public UUID getBackupJobId() {
        return backupJobId;
    }

    public UUID getId() {
        return id;
    }
}
