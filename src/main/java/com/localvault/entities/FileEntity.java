package com.localvault.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("file")
@NoArgsConstructor
public class FileEntity extends StorageEntity {
    @Column(name = "size")
    private long size;

    public FileEntity(String name, String parentDir) {
        this.name = name;
        this.parentDir = parentDir;
        this.size = 0;
    }

    public FileEntity(String name, String parentDir, long size, String lastModifiedDate) {
        this.name = name;
        this.parentDir = parentDir;
        this.size = size;
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return this.size + super.toString();
    }
}