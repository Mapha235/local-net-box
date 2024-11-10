package com.localvault.entities;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class FileChunk {
    private MultipartFile fileChunk;
    private long id;
    
}
