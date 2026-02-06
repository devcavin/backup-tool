package io.github.devcavin.domain.service;

import io.github.devcavin.domain.entity.Archive;
import io.github.devcavin.domain.entity.BackupJob;

public interface ArchiveCreator {
    Archive createArchive(BackupJob backupJob);
}
