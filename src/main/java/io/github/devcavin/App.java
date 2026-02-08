package io.github.devcavin;

import io.github.devcavin.application.usecase.CreateBackupJobUseCase;
import io.github.devcavin.application.usecase.ListArchivesUseCase;
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
import io.github.devcavin.infrastructure.scheduler.ScheduledBackupService;

import java.util.UUID;

public class App {

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        String sourcePath = args[0];
        String destinationPath = args[1];
        long intervalMinutes = args.length >= 3 ? Long.parseLong(args[2]) : 0;

        // Repositories
        BackupJobRepository jobRepository = new InMemoryBackupJobRepository();
        ArchiveRepository archiveRepository = new InMemoryArchiveRepository();

        // Infrastructure services
        ArchiveCreator archiveCreator = new LocalArchiveCreator();
        ArchiveStorage archiveStorage = new LocalArchiveStorage();

        // Domain service
        BackupService backupService = new BackupService(archiveCreator, archiveStorage, archiveRepository);

        // Use cases
        CreateBackupJobUseCase createJob = new CreateBackupJobUseCase(jobRepository);
        RunBackupJobUseCase runJob = new RunBackupJobUseCase(jobRepository, backupService);
        ListBackupJobsUseCase listJobs = new ListBackupJobsUseCase(jobRepository);
        ListArchivesUseCase listArchives = new ListArchivesUseCase(archiveRepository);

        // Create backup job
        System.out.println("Creating backup job...");
        System.out.println("Source: " + sourcePath);
        System.out.println("Destination: " + destinationPath);

        UUID jobId;
        try {
            jobId = createJob.execute("automated-backup", sourcePath, destinationPath);
            System.out.println("Backup job created with ID: " + jobId);
        } catch (IllegalArgumentException e) {
            System.err.println("\n=== ERROR ===");
            System.err.println(e.getMessage());
            System.err.println("\nPlease check:");
            System.err.println("  - Source directory exists and is readable");
            System.err.println("  - Source directory is not empty");
            System.err.println("  - Destination directory is writable");
            System.exit(1);
            return; // Never reached, but keeps compiler happy
        }

        if (intervalMinutes > 0) {
            // Scheduled mode
            System.out.println("\n=== SCHEDULED BACKUP MODE ===");
            System.out.println("Backup will run every " + intervalMinutes + " minutes");
            System.out.println("Press Ctrl+C to stop\n");

            ScheduledBackupService scheduler = new ScheduledBackupService(runJob);
            scheduler.scheduleBackup(jobId, intervalMinutes);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutdown signal received...");
                scheduler.shutdown();
                printJobStatus(listJobs);
                printArchiveHistory(listArchives, jobId);
            }));

            // Keep the application running
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            // One-time backup mode
            System.out.println("\n=== ONE-TIME BACKUP MODE ===");
            runJob.execute(jobId);
            System.out.println();
            printJobStatus(listJobs);
            printArchiveHistory(listArchives, jobId);
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar backup-tool.jar <source> <destination> [interval_minutes]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  source            - Directory to backup");
        System.out.println("  destination       - Directory where backups will be stored");
        System.out.println("  interval_minutes  - (Optional) Run backup every N minutes. If omitted, runs once.");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  One-time backup:");
        System.out.println("    java -jar backup-tool.jar /home/user/documents /home/user/backups");
        System.out.println();
        System.out.println("  Scheduled backup (every 5 minutes):");
        System.out.println("    java -jar backup-tool.jar /home/user/documents /home/user/backups 5");
    }

    private static void printJobStatus(ListBackupJobsUseCase listJobs) {
        System.out.println("=== BACKUP STATUS ===");
        listJobs.execute().forEach(job ->
                System.out.printf("%s -> %s (Created: %s)%n",
                        job.getBackupName(),
                        job.getStatus(),
                        job.getCreatedAt()
                )
        );
    }

    private static void printArchiveHistory(ListArchivesUseCase listArchives, UUID jobId) {
        System.out.println("\n=== ARCHIVE HISTORY ===");
        var archives = listArchives.execute(jobId);
        if (archives.isEmpty()) {
            System.out.println("No archives created yet.");
        } else {
            archives.forEach(archive ->
                    System.out.printf("Archive: %s%n  Size: %.2f MB%n  Created: %s%n%n",
                            archive.getPath().getFileName(),
                            archive.getSizeInBytes() / (1024.0 * 1024.0),
                            archive.getCreationAt()
                    )
            );
        }
    }
}