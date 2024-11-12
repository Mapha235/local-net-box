package com.localvault.entities;

import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeName("file")
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
        this.hashCode = this.getHashCode();
    }

    public FileEntity(String name, String parentDir, long size, String lastModifiedDate) {
        this.name = name;
        this.parentDir = parentDir;
        this.size = size;
        this.lastModifiedDate = lastModifiedDate;
        this.hashCode = this.getHashCode();
    }

    @Override
    public String toString() {
        return "FileEntity(" + this.size + ", " + super.toString();
    }
}