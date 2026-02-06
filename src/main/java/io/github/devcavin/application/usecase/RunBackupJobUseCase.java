package io.github.devcavin.application.usecase;

import io.github.devcavin.domain.entity.BackupJob;
import io.github.devcavin.domain.repository.BackupJobRepository;
import io.github.devcavin.domain.service.BackupService;

import java.util.UUID;

public class RunBackupJobUseCase {
    private final BackupJobRepository backupJobRepository;
    private final BackupService backupService;


    public RunBackupJobUseCase(BackupJobRepository backupJobRepository, BackupService backupService) {
        this.backupJobRepository = backupJobRepository;
        this.backupService = backupService;
    }

    public void execute(UUID backupJobId) {
        BackupJob backupJob = backupJobRepository.findById(backupJobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        backupService.execute(backupJob);
        backupJobRepository.save(backupJob);
    }
}
