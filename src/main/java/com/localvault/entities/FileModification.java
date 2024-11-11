package com.localvault.entities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/**
 * CLass that provides methods for modifying files and retrieving data.
 * This includes converting and retrieving metadata, object type, etc.
 */
@Getter
@Setter
public class FileModification {
    private long totalChunks;
    private long nextExpectedChunkId = 0;
    private String name;
    private String location;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm:ss");

    private FileModification() {
    }

    /**
     * Converts a data format to dd.MM.yyyy HH:mm:ss
     * 
     * @param fileTime
     * @return String dd.MM.yyyy HH:mm:ss
     */
    public static String formatDateTime(FileTime fileTime) {
        LocalDateTime localDateTime = fileTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DATE_FORMATTER);
    }

    public void addChunk(MultipartFile fileChunk, long chunkId) {

    }

    public static FileEntity convertToFileEntity(File file) {
        try {
            FileEntity fileEntity = new FileEntity(
                    file.getName(),
                    Objects.toString(file.getParent(), "").replace('\\', '/'),
                    file.length(),
                    FileModification.formatDateTime(Files.getLastModifiedTime(file.toPath())));
            return fileEntity;
        } catch (IOException e) {
            System.out.println("Could not convert java.io.File object to FileEntity object. Reason: " + e.getMessage());
        }
        return null;
    }

    public static File convertToFile(FileEntity fileEntity) {
        return new File(fileEntity.getAbsolutePath());
    }

    public static String getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024) + " mb";
    }

    public static String getFileSizeKiloBytes(File file) {
        return (double) file.length() / 1024 + "  kb";
    }

    public static String getFileSizeBytes(File file) {
        return file.length() + " bytes";
    }

    public List<Long> createChunkQueue() {
        return null;
    }

    public static byte[] getFileContent(String filePath) {
        Path fullPath = Paths.get(filePath);
        try {
            if (fullPath.toFile().isFile())
                return Files.readAllBytes(fullPath);
            else if (fullPath.toFile().isDirectory()) {
                // TODO: If directory => walk
            }

        } catch (IOException e) {
            System.out.println("File " + fullPath.toString() + "does not exists. Error: " + e);
        }
        return new byte[0];
    }

    public Resource getContent(StorageEntity storageEntity) throws IOException {
        Path fullPath = Paths.get(storageEntity.getAbsolutePath());

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(fullPath));
        return resource;
    }

}
