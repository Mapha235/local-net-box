package com.localvault.entities;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StorageEntityID implements Serializable {
    private long id;
    private String name;
    private String parentDir;

    // default constructor

    public StorageEntityID(long id, String name, String parentDir) {
        this.name = name;
        this.parentDir = parentDir;
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof StorageEntityID fileObj))
            return false;
        return this.id == fileObj.id && this.name == fileObj.name && Objects.equals(this.parentDir, fileObj.parentDir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.parentDir);
    }
}
