package com.localvault.service;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.localvault.entities.FileEntity;
import com.localvault.entities.FolderEntity;
import com.localvault.entities.StorageEntity;

public interface FileService {

    public List<FileEntity> fetchFileList(String dirPath) throws Exception;

    /**
     * Fetches a list of all folder entities inside a directory.
     * 
     * @return a list of folder entities
     */
    public List<FolderEntity> fetchFolderList(String dirPath) throws Exception;

    /**
     * Fetches a list of all files inside a directory and saves them to the
     * database.
     * 
     * @param dirPath: path of directory
     * @return: list of saved file entities
     */
    public List<StorageEntity> fetchFileListFromServer(String dirPath) throws IOException;

    /**
     * Fetches a list of files from the database, given a directory.
     * If no files are found, this function searches the server for files in the
     * given directory.
     * 
     * @param dirPath: path of directory
     * @return: a list of file and folder entities
     */
    public List<StorageEntity> fetchFileListFromDB(String dirPath) throws IOException;

    /**
     * Handles the received file chunk.
     * 
     * @param file: MultipartFile object
     * @return: Id of the next expected chunk.
     */
    public long saveChunk(MultipartFile fileChunk, long chunkNr, long totalChunks, String destination);

    /**
     * Save file metadata to database when it has been uploaded.
     */
    public StorageEntity saveMetaDataToDatabase();

    /**
     * Retrieves the file content.
     * 
     * @param dir
     * @return requested files resource
     */
    public Resource getResource(String dir) throws IOException;

    /**
     * Deletes the requested resource.
     * 
     * @param storageEntity Storage Entity to be moved to the recycle bin
     * @return true if deleting the resource was successful
     */
    public boolean deleteResource(StorageEntity storageEntity) throws Exception;
}
