package io.github.devcavin.application.usecase;

import io.github.devcavin.domain.entity.BackupJob;
import io.github.devcavin.domain.repository.BackupJobRepository;
import io.github.devcavin.domain.valueobject.BackupName;
import io.github.devcavin.domain.valueobject.DestinationPath;
import io.github.devcavin.domain.valueobject.SourcePath;

import java.util.UUID;

public class CreateBackupJobUseCase {
    private final BackupJobRepository backupJobRepository;

    public CreateBackupJobUseCase(BackupJobRepository backupJobRepository) {
        this.backupJobRepository = backupJobRepository;
    }

    public UUID execute(
            String name,
            String source,
            String destination
    ) {
        BackupJob backupJob = new BackupJob(
                new BackupName(name),
                new SourcePath(source),
                new DestinationPath(destination)
        );
        backupJobRepository.save(backupJob);
        return backupJob.getId();
    }
}
