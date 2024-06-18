package com.ladeologun.aws_s3.controller;

import com.ladeologun.aws_s3.service.S3FileService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileManagerController {

    private final S3FileService s3FileService;

    public FileManagerController(S3FileService s3FileService) {
        this.s3FileService = s3FileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile (
            @RequestParam("file") MultipartFile file
    ){
        Map<String, String> fileDetails = s3FileService.saveFile(file);
        return ResponseEntity.ok().body(fileDetails);
    }

    @GetMapping("/download/{fileKey}")
    public ResponseEntity<ByteArrayResource> downloadFile (
            @PathVariable("fileKey") String key
    ){
        byte[] bytes = s3FileService.downloadFile(key);
        ByteArrayResource resource = new ByteArrayResource(bytes);

        return ResponseEntity
                .ok()
                .contentLength(bytes.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + key + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileKey}")
    public ResponseEntity<Map<String, String>> uploadFile (
            @PathVariable("fileKey") String key
    ){
        s3FileService.deleteFile(key);
        Map<String, String> response = new HashMap<>();
        response.put("message","success");
        return ResponseEntity.ok().body(response);
    }
}
