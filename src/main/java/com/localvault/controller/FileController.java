package com.localvault.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.localvault.entities.StorageEntity;
import com.localvault.service.FileService;

@RestController
@RequestMapping("/api/files")
@CrossOrigin
public class FileController {

    @Autowired
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, List<? extends StorageEntity>>> getFiles(
            @RequestParam(required = false) String dir,
            @RequestParam(value = "files", required = false, defaultValue = "true") boolean filesRequested,
            @RequestParam(value = "folders", required = false, defaultValue = "true") boolean foldersRequested) {
        try {
            Map<String, List<? extends StorageEntity>> storageEntities = new HashMap<>();
            if (foldersRequested)
                storageEntities.put("folders", this.fileService.fetchFolderList(Objects.toString(dir, "")));
            if (filesRequested)
                storageEntities.put("files", this.fileService.fetchFileList(Objects.toString(dir, "")));

            return ResponseEntity.ok(storageEntities);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> getResource(@RequestParam(required = false) String dir) {
        try {
            System.out.println("download request received for: " + dir);
            Resource resource = this.fileService.getResource(dir);
            System.out.println("length: " + resource.contentLength());

            HttpHeaders header = new HttpHeaders();
            header.add("Content-Disposition", "attachment; filename=" + "test.zip");
            header.setContentLength(resource.contentLength());
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(header)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST, consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, Long>> handleFileUploadRequest(
            @RequestHeader Map<String, String> headers,
            @RequestParam("file") MultipartFile fileChunk) {

        long chunkNr = Long.parseLong(headers.get("x-chunk-id"));
        long totalChunks = Long.parseLong(headers.get("x-total-chunks"));
        String dir = headers.get("x-destination-dir");
        System.out.println("Current dir to upload: " + dir);

        long expectedChunkId = -1;
        Map<String, Long> map = new HashMap<>();

        if (chunkNr < totalChunks) {
            expectedChunkId = this.fileService.saveChunk(fileChunk, chunkNr, totalChunks, dir);
            map.put("nextExpectedChunk: ", expectedChunkId);
            if (expectedChunkId == totalChunks) {
                System.out.println("Finished uploading: " + headers.get("x-content-name"));
                return ResponseEntity.ok().build();
            }
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Expected-Chunk-Id", Long.toString(expectedChunkId));

        return new ResponseEntity<>(map, responseHeaders, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public <T extends StorageEntity> ResponseEntity<String> handleDeleteRequest(@RequestBody T payload,
            @RequestParam(required = false) String dir) {
        try {
            System.out.println("delete request received: " + payload);
            System.out.println("delete request received: " + payload.getClass());

            this.fileService.deleteResource(payload);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return ResponseEntity.ok().build();
    }
}
