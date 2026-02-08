package io.github.devcavin.infrastructure.scheduler;

import io.github.devcavin.application.usecase.RunBackupJobUseCase;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledBackupService {
    private final ScheduledExecutorService scheduler;
    private final RunBackupJobUseCase runBackupJobUseCase;

    public ScheduledBackupService(RunBackupJobUseCase runBackupJobUseCase) {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.runBackupJobUseCase = runBackupJobUseCase;
    }

    /**
     * Schedule a backup job to run at fixed intervals
     * @param jobId The backup job ID to run
     * @param intervalMinutes How often to run the backup (in minutes)
     */
    public void scheduleBackup(UUID jobId, long intervalMinutes) {
        System.out.println("Scheduling backup job " + jobId + " to run every " + intervalMinutes + " minutes");

        scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        System.out.println("\n" + "=".repeat(50));
                        System.out.println("Running scheduled backup: " + jobId);
                        System.out.println("Time: " + java.time.Instant.now());
                        System.out.println("=".repeat(50));
                        runBackupJobUseCase.execute(jobId);
                        System.out.println("Scheduled backup completed successfully");
                    } catch (IllegalStateException e) {
                        System.err.println("Backup validation failed: " + e.getMessage());
                        System.err.println("This backup will be skipped. Next run in " + intervalMinutes + " minutes.");
                    } catch (Exception e) {
                        System.err.println("Scheduled backup failed: " + e.getMessage());
                        e.printStackTrace();
                    }
                },
                0, // Initial delay (0 = run immediately)
                intervalMinutes,
                TimeUnit.MINUTES
        );
    }

    /**
     * Shutdown the scheduler gracefully
     */
    public void shutdown() {
        System.out.println("Shutting down backup scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}