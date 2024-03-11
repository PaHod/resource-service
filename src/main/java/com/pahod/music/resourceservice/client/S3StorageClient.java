package com.pahod.music.resourceservice.client;

import io.github.resilience4j.retry.annotation.Retry;
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
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Slf4j
@Service
@Retry(name = "s3Client")
public class S3StorageClient {

  private final S3Client s3Client;

  @Autowired
  public S3StorageClient(@Value("${cloud.aws.region.static}") String awsRegion) {
    s3Client = S3Client.builder().region(Region.of(awsRegion)).build();
  }

  public String storeFile(String bucketName, String fileKey, MultipartFile audioFile) {
    log.info("Store file {} to bucket {}", fileKey, bucketName);
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
      return response.eTag();

    } catch (IOException e) {
      e.printStackTrace();
      return "Failed to upload file. Error: " + e.getMessage();
    }
  }

  public void moveFile(
          String sourceBucketName,
          String sourceKey,
          String destinationBucketName,
          String destinationKey) {
    log.info("Move file {} to bucket {}", sourceKey, destinationBucketName);

    CopyObjectRequest copyObjRequest =
            CopyObjectRequest.builder()
                    .sourceBucket(sourceBucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(destinationBucketName)
                    .destinationKey(destinationKey)
                    .build();
    s3Client.copyObject(copyObjRequest);
    DeleteObjectRequest deleteObjectRequest =
            DeleteObjectRequest.builder().bucket(sourceBucketName).key(sourceKey).build();
    s3Client.deleteObject(deleteObjectRequest);
  }

  public byte[] fetchFile(String fileKey, String bucketName) {
    log.info("Get file {} from bucket {}", fileKey, bucketName);

    GetObjectRequest objectRequest =
            GetObjectRequest.builder().bucket(bucketName).key(fileKey).build();

    ResponseBytes<GetObjectResponse> responseResponseBytes =
            s3Client.getObjectAsBytes(objectRequest);

    return responseResponseBytes.asByteArray();
  }
}
