package io.github.devcavin.domain.service;

import io.github.devcavin.domain.entity.Archive;
import io.github.devcavin.domain.entity.BackupJob;

public class BackupService {
    private final ArchiveCreator archiveCreator;
    private final ArchiveStorage archiveStorage;


    public BackupService(ArchiveCreator archiveCreator, ArchiveStorage archiveStorage) {
        this.archiveCreator = archiveCreator;
        this.archiveStorage = archiveStorage;
    }

    public void execute(BackupJob backupJob) {
        backupJob.markAsRunning();

        try {
            Archive archive = archiveCreator.createArchive(backupJob);
            archiveStorage.store(archive);
            backupJob.markAsCompleted();
        } catch (Exception e) {
            backupJob.markAsFailed();
            throw e;
        }
    }
}
