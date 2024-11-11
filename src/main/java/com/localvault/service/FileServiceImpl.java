package com.localvault.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.localvault.config.Constants;
import com.localvault.entities.FileEntity;
import com.localvault.entities.FileModification;
import com.localvault.entities.FolderEntity;
import com.localvault.entities.StorageEntity;
import com.localvault.repositories.FileRepository;

import lombok.Data;

@Data
@Service
public class FileServiceImpl implements FileService {
    private String root;

    @Autowired
    private FileRepository<FileEntity> fileRepository;
    @Autowired
    private FileRepository<FolderEntity> folderRepository;

    private final Path rootLocation;
    private String currentPath;

    public FileServiceImpl() {
        this.root = Constants.HOST_ROOT;
        this.rootLocation = Paths.get(this.root);
        this.currentPath = "";
    }

    @Override
    public List<FileEntity> fetchFileList(String dirPath) throws Exception {
        List<FileEntity> output = new ArrayList<>();
        // Reading files in the directory
        Path path = this.getPath(dirPath);

        List<File> files = Files.list(path)
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toList());

        for (File f : files) {
            FileEntity file = FileModification.convertToFileEntity(f);
            file.setParentDir(dirPath);
            output.add(file);
        }
        return output;
    }

    @Override
    public List<FolderEntity> fetchFolderList(String dirPath) throws Exception {
        Path directory = this.getPath(dirPath);

        File[] directories = directory.toFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        List<FolderEntity> folderEntities = new ArrayList<>();
        for (File folder : directories) {
            // Path object used to get file size and last modified date
            Path f = Paths.get(folder.getAbsolutePath());
            FolderEntity folderEntity = new FolderEntity(folder.getName(), dirPath,
                    FileModification.formatDateTime(Files.getLastModifiedTime(f)));

            folderEntities.add(this.folderRepository.save(folderEntity));
        }
        return folderEntities;
    }

    /**
     * Checks if the given directory exists in the local storage.
     * Throws an exception if it does not exist.
     * 
     * @return Path object of the given path
     */
    public Path getPath(String dir) {
        Path path = Paths.get(this.root, dir);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new IllegalArgumentException("Directory " + path + " does not exist");
        }
        return path;
    }

    @Override
    public long saveChunk(MultipartFile fileChunk, long chunkNr, long totalChunks, String destination) {
        try {
            Path source = Paths.get(this.root, destination, fileChunk.getOriginalFilename());
            if (!Files.exists(source)) {
                Files.createFile(source);
            }
            if (chunkNr >= 0) {
                byte[] bytes = fileChunk.getBytes();
                FileOutputStream stream = new FileOutputStream(source.toString(), true);
                stream.write(bytes);
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chunkNr + 1;
    }

    @Override
    public List<StorageEntity> fetchFileListFromServer(String dirPath) throws IOException {
        // Checks if the file/directory exists
        Path directory = this.rootLocation.resolve(dirPath).normalize().toAbsolutePath();
        System.out.println(directory);
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Directory does not exist");
        }
        // Check if the file/directory is empty
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            if (!directoryStream.iterator().hasNext())
                return new ArrayList<>();
        }

        // Collects all files and folders that are contained inside the given directory
        List<String> fileStringList = Files.list(directory)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());

        List<StorageEntity> fileObjectList = new ArrayList<StorageEntity>();
        for (String file : fileStringList) {
            // Path object used to get file size and last modified date
            Path f = Paths.get(directory.toString() + "/" + file);
            FileEntity fileEnt = new FileEntity(file,
                    dirPath,
                    Files.size(f),
                    FileModification.formatDateTime(Files.getLastModifiedTime(f)));

            fileObjectList.add(this.fileRepository.save(fileEnt));
        }
        return fileObjectList;
    }

    @Override
    public List<StorageEntity> fetchFileListFromDB(String dirPath) {
        // Check the database for files given dirPath
        this.currentPath = dirPath;
        List<StorageEntity> fileList = this.fileRepository.findFolderContent(dirPath);

        // Search the server for files if no database entries have been found
        if (fileList.size() == 0) {
            try {
                fileList = this.fetchFileListFromServer(dirPath);

            } catch (Exception e) {
            }
        }
        return fileList;
    }

    @Override
    public StorageEntity saveMetaDataToDatabase() {
        FileEntity file = new FileEntity();
        return fileRepository.save(file);
    }

    @Override
    public Resource getResource(String dir) throws IOException {
        File downloadFile = new File(this.root + dir);
        Path fullPath = Paths.get(this.root + dir);

        ByteArrayResource resource = null;
        if (downloadFile.isFile()) {
            resource = new ByteArrayResource(Files.readAllBytes(fullPath));
        } else if (downloadFile.isDirectory()) {
            // Create a zip file
            List<Path> result;
            try (Stream<Path> walk = Files.walk(fullPath)) {
                result = walk.filter(Files::isRegularFile)
                        .collect(Collectors.toList());
                result.forEach(System.out::println);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
                ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

                for (Path filePath : result) {
                    zipOutputStream.putNextEntry(new ZipEntry(fullPath.relativize(filePath).toString()));
                    FileInputStream fileInputStream = new FileInputStream(filePath.toFile());

                    IOUtils.copy(fileInputStream, zipOutputStream);

                    fileInputStream.close();
                    zipOutputStream.closeEntry();
                }

                if (zipOutputStream != null) {
                    zipOutputStream.finish();
                    zipOutputStream.flush();
                    IOUtils.closeQuietly(zipOutputStream);
                }
                IOUtils.closeQuietly(bufferedOutputStream);
                IOUtils.closeQuietly(byteArrayOutputStream);

                resource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Finished collecting files for download");
        return resource;
    }
}
