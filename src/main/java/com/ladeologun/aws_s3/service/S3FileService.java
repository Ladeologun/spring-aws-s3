package com.ladeologun.aws_s3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class S3FileService implements FileService{

    @Value("${aws.bucketName}")
    private String bucketName;
    private final S3Client s3Client;
    public S3FileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public Map<String, String> saveFile(MultipartFile file) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String key = UUID.randomUUID().toString() + "-" + System.currentTimeMillis() + "."+ fileExtension;
        try{
            PutObjectResponse response =  s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .key(key)
                    .build(), RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            Map<String, String> fileDetails  =  new HashMap<>();
            fileDetails.put("key", key);
            fileDetails.put("fileUrl", getFileUrl(key));
            return fileDetails;
        } catch (IOException e){
            throw new RuntimeException("unable to upload picture");
        }
    }

    @Override
    public byte[] downloadFile(String key) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(key)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();

            return data;

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw new RuntimeException("unable to download picture");
        }

    }

    @Override
    public void deleteFile(String key) {
        try{
             DeleteObjectResponse response = s3Client.deleteObject(DeleteObjectRequest.builder()
                    .key(key)
                    .bucket(bucketName)
                    .build());
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

    }


    private String getFileUrl(String fileKey) {
        S3Utilities s3Utilities = s3Client.utilities();
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
        URL url = s3Utilities.getUrl(request);
        return url.toString();
    }

    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
