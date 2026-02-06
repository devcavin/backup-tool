package io.github.devcavin.application.usecase;

import io.github.devcavin.domain.entity.BackupJob;
import io.github.devcavin.domain.repository.BackupJobRepository;

import java.util.List;

public class ListBackupJobsUseCase {
    private final BackupJobRepository backupJobRepository;


    public ListBackupJobsUseCase(BackupJobRepository backupJobRepository) {
        this.backupJobRepository = backupJobRepository;
    }

    public List<BackupJob> execute() {
        return backupJobRepository.findAll();
    }
}
