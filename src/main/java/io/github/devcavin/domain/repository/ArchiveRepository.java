package io.github.devcavin.domain.repository;

import io.github.devcavin.domain.entity.Archive;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArchiveRepository {
    void save(Archive archive);

    Optional<Archive> findById(UUID id);

    List<Archive> findByBackupJobId(UUID backupJobId);
}
