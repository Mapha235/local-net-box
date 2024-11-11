package com.localvault.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("folder")
@NoArgsConstructor
public class FolderEntity extends StorageEntity {
    private int maxDepth;

    public FolderEntity(String name, String parentDir, String lastModifiedDate) {
        this.name = name;
        this.parentDir = parentDir;
        this.lastModifiedDate = lastModifiedDate;
        this.maxDepth = 0;
        this.hashCode = this.getHashCode();
    }
}
