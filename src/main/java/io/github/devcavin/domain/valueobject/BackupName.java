package io.github.devcavin.domain.valueobject;

import java.util.Objects;

public class BackupName {
    private final String name;

    public BackupName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Backup name cannot be blank");
        }

        this.name = name;
    }

    public String getBackupName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof BackupName that)) {
            return false;
        }
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
