package io.github.devcavin.domain.repository;

import io.github.devcavin.domain.entity.BackupJob;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BackupJobRepository {
    void save(BackupJob backupJob);

    Optional<BackupJob> findById(UUID id);

    List<BackupJob> findAll();
}
