package io.github.devcavin;

import io.github.devcavin.application.usecase.CreateBackupJobUseCase;
import io.github.devcavin.application.usecase.ListBackupJobsUseCase;
import io.github.devcavin.application.usecase.RunBackupJobUseCase;
import io.github.devcavin.domain.repository.ArchiveRepository;
import io.github.devcavin.domain.repository.BackupJobRepository;
import io.github.devcavin.domain.service.ArchiveCreator;
import io.github.devcavin.domain.service.ArchiveStorage;
import io.github.devcavin.domain.service.BackupService;
import io.github.devcavin.infrastructure.archive.local.LocalArchiveCreator;
import io.github.devcavin.infrastructure.archive.local.LocalArchiveStorage;
import io.github.devcavin.infrastructure.repository.memory.InMemoryArchiveRepository;
import io.github.devcavin.infrastructure.repository.memory.InMemoryBackupJobRepository;

import java.util.UUID;

public class App {

    public static void main(String[] args) {

        // Repositories
        BackupJobRepository jobRepository =
                new InMemoryBackupJobRepository();

        ArchiveRepository archiveRepository =
                new InMemoryArchiveRepository();

        // Infrastructure services
        ArchiveCreator archiveCreator =
                new LocalArchiveCreator();

        ArchiveStorage archiveStorage =
                new LocalArchiveStorage();

        // Domain service
        BackupService backupService =
                new BackupService(
                        archiveCreator,
                        archiveStorage
                );

        // Use cases
        CreateBackupJobUseCase createJob =
                new CreateBackupJobUseCase(jobRepository);

        RunBackupJobUseCase runJob =
                new RunBackupJobUseCase(
                        jobRepository,
                        backupService
                );

        ListBackupJobsUseCase listJobs =
                new ListBackupJobsUseCase(jobRepository);

        // Demo
        UUID jobId = createJob.execute(
                "demo-backup",
                "/home/cavin/Documents/demo-backup",
                "/home/cavin/backups"
        );

        runJob.execute(jobId);

        listJobs.execute()
                .forEach(job ->
                        System.out.println(
                                job.getBackupName() +
                                        " -> " +
                                        job.getStatus()
                        )
                );
    }
}
