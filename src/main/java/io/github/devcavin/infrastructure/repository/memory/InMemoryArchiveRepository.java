package io.github.devcavin.infrastructure.repository.memory;

import io.github.devcavin.domain.entity.Archive;
import io.github.devcavin.domain.repository.ArchiveRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryArchiveRepository implements ArchiveRepository {
    private final Map<UUID, Archive> store = new ConcurrentHashMap<>();
    @Override
    public void save(Archive archive) {
        store.put(archive.getId(), archive);
    }

    @Override
    public Optional<Archive> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Archive> findByBackupJobId(UUID backupJobId) {
        return store.values()
                .stream()
                .filter(a -> a.getBackupJobId().equals(backupJobId))
                .collect(Collectors.toList());
    }
}
