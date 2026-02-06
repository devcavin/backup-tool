package io.github.devcavin.application.usecase;

import io.github.devcavin.domain.entity.Archive;
import io.github.devcavin.domain.repository.ArchiveRepository;

import java.util.List;
import java.util.UUID;

public class ListArchivesUseCase {
    private final ArchiveRepository archiveRepository;


    public ListArchivesUseCase(ArchiveRepository archiveRepository) {
        this.archiveRepository = archiveRepository;
    }

    public List<Archive> execute(UUID backupJobId) {
        return archiveRepository.findByBackupJobId(backupJobId);
    }
}
