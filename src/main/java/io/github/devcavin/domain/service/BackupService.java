package io.github.devcavin.domain.service;

import io.github.devcavin.domain.entity.Archive;
import io.github.devcavin.domain.entity.BackupJob;
import io.github.devcavin.domain.repository.ArchiveRepository;

public class BackupService {
    private final ArchiveCreator archiveCreator;
    private final ArchiveStorage archiveStorage;
    private final ArchiveRepository archiveRepository;

    public BackupService(ArchiveCreator archiveCreator, ArchiveStorage archiveStorage, ArchiveRepository archiveRepository) {
        this.archiveCreator = archiveCreator;
        this.archiveStorage = archiveStorage;
        this.archiveRepository = archiveRepository;
    }

    public void execute(BackupJob backupJob) {
        backupJob.markAsRunning();

        try {
            Archive archive = archiveCreator.createArchive(backupJob);
            archiveStorage.store(archive);
            archiveRepository.save(archive);
            backupJob.markAsCompleted();
        } catch (Exception e) {
            backupJob.markAsFailed();
            throw e;
        }
    }
}
