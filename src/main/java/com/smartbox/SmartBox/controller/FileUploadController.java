package com.smartbox.SmartBox.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Make sure directory exists
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(UPLOAD_DIR, filename);

            // Save file
            Files.write(filepath, file.getBytes());

            // Generate accessible URL
            String fileUrl = "http://localhost:8081/uploads/" + filename;

            return new ResponseEntity<>(fileUrl, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
