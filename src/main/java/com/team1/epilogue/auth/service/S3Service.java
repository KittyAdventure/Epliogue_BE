package com.team1.epilogue.auth.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private AmazonS3 s3client;

    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @PostConstruct
    public void init() {
        BasicAWSCredentials creds = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        s3client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(awsRegion))
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
    }

    public String uploadFile(MultipartFile file) {
        File convertFile = convertMultiPartToFile(file);
        String fileName = generateFileName(file);
        s3client.putObject(new PutObjectRequest(bucketName, fileName, convertFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        convertFile.delete();
        return s3client.getUrl(bucketName, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        String fileKey = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(bucketName, fileKey);
    }

    private File convertMultiPartToFile(MultipartFile file) {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("파일 변환 실패", e);
        }
        return convFile;
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    }
}
