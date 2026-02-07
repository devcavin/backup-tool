package io.github.devcavin.infrastructure.scheduler;

import io.github.devcavin.application.usecase.RunBackupJobUseCase;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledBackupService {
    private final ScheduledExecutorService scheduler;
    private final RunBackupJobUseCase  runBackupJobUseCase;


    public ScheduledBackupService(RunBackupJobUseCase runBackupJobUseCase) {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.runBackupJobUseCase = runBackupJobUseCase;
    }

    /**
     * Schedule a backup job to run at fixed intervals
     * @param jobId The backup job id to run
     * @param intervalMinutes How often to run the job (in minutes)
     */

    public void scheduleBackup(UUID jobId, long intervalMinutes) {
        System.out.printf("Scheduling backup job with id: %s to run every %d minutes\n", jobId, intervalMinutes);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.printf("Running the scheduled backup job with id: %s\n", jobId);
                runBackupJobUseCase.execute(jobId);
                System.out.println("Scheduled backup completed successfully");
            } catch (Exception e) {
                System.err.printf("Scheduled backup job with id %s failed",  jobId);
                e.printStackTrace(); // will add logging later
            }
        },
                0,  // delay 0 which run immediately
                intervalMinutes,
                TimeUnit.MINUTES);
    }

    /**
     * Shutting down the scheduler gracefully
     */
    public void shutdown() {
        System.out.println("Shutting down the scheduler...");
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            // throw new RuntimeException(e);
        }
    }
}
