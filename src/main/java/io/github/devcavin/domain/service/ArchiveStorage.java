package io.github.devcavin.domain.service;

import io.github.devcavin.domain.entity.Archive;

public interface ArchiveStorage {
    void store(Archive archive);
}
