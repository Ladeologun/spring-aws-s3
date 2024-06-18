package com.ladeologun.aws_s3.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {
    public Map<String, String> saveFile(MultipartFile file);
    public void deleteFile(String key);
    public byte[] downloadFile(String key);
}
