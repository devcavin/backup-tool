package io.github.devcavin.infrastructure.repository.memory;

import io.github.devcavin.domain.entity.BackupJob;
import io.github.devcavin.domain.repository.BackupJobRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBackupJobRepository implements BackupJobRepository {
    private final Map<UUID, BackupJob> store = new ConcurrentHashMap<>();

    @Override
    public void save(BackupJob backupJob) {
        store.put(backupJob.getId(), backupJob);
    }

    @Override
    public Optional<BackupJob> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<BackupJob> findAll() {
        return new ArrayList<>(store.values());
    }
}
