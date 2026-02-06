package io.github.devcavin.domain.entity;

import io.github.devcavin.domain.enums.BackupStatus;
import io.github.devcavin.domain.valueobject.BackupName;
import io.github.devcavin.domain.valueobject.DestinationPath;
import io.github.devcavin.domain.valueobject.SourcePath;

import java.time.Instant;
import java.util.UUID;

public class BackupJob {
    private final UUID id;
    private final BackupName backupName;
    private final SourcePath sourcePath;
    private final DestinationPath destinationPath;
    private BackupStatus status;
    private final Instant createdAt;


    public BackupJob(BackupName backupName, SourcePath sourcePath, DestinationPath destinationPath) {
        this.id = UUID.randomUUID();
        this.backupName = backupName;
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.status = BackupStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public BackupName getBackupName() {
        return backupName;
    }

    public SourcePath getSourcePath() {
        return sourcePath;
    }

    public DestinationPath getDestinationPath() {
        return destinationPath;
    }

    public BackupStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void markAsRunning() {
        if (status != BackupStatus.PENDING) {
            throw new IllegalArgumentException("Backup status can only start at PENDING state");
        }
        this.status = BackupStatus.RUNNING;
    }

    public void markAsCompleted() {
        if (status != BackupStatus.RUNNING) {
            throw new IllegalArgumentException("Backup status can only complete for RUNNING state");
        }
        this.status = BackupStatus.COMPLETED;
    }

    public void markAsFailed() {
        if (status != BackupStatus.RUNNING) {
            throw new IllegalArgumentException("Backup status can only fail for RUNNING state");
        }
        this.status = BackupStatus.FAILED;
    }
}
