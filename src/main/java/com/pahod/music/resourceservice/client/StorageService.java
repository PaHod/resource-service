package com.pahod.music.resourceservice.client;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Slf4j
@Service
public class StorageService {

  private final S3Client s3Client;
  private final String bucketName;

  @Autowired
  public StorageService(
      @Value("${cloud.aws.region.static}") String awsRegion,
      @Value("${cloud.aws.bucket.name}") String bucketName) {
    s3Client = S3Client.builder().region(Region.of(awsRegion)).build();
    this.bucketName = bucketName;
  }

  public String storeFile(MultipartFile audioFile, String fileKey) {
    try {
      PutObjectRequest objectRequest =
          PutObjectRequest.builder()
              .bucket(bucketName)
              .key(fileKey)
              .contentType(audioFile.getContentType())
              .build();

      PutObjectResponse response =
          s3Client.putObject(objectRequest, RequestBody.fromBytes(audioFile.getBytes()));

      log.debug("File uploaded successfully. Etag: {}", response.eTag());
      return bucketName;

    } catch (IOException e) {
      e.printStackTrace();
      return "Failed to upload file. Error: " + e.getMessage();
    }
  }

  public byte[] fetchFile(String fileKey, String bucketName) {

    GetObjectRequest objectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(fileKey).build();

    ResponseBytes<GetObjectResponse> responseResponseBytes =
        s3Client.getObjectAsBytes(objectRequest);

    return responseResponseBytes.asByteArray();
  }
}
