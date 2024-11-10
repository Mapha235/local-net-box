package com.localvault.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.localvault.entities.StorageEntity;

@Repository

public interface FileRepository<T extends StorageEntity> extends CrudRepository<T, Long> {
    @Query("from FileEntity")
    List<StorageEntity> findAllFiles();

    @Query("SELECT f FROM FileEntity f WHERE f.parentDir = ?1")
    List<StorageEntity> findFolderContent(String rootDir);

    @Query("SELECT f FROM FileEntity f WHERE f.parentDir = ?1 AND f.name = ?2")
    List<StorageEntity> findFile(String rootDir, String fileName);
}