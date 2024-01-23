package com.pahod.music.resourceservice.client;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

// import javax.servlet.http.HttpServletResponse;

@Slf4j
public class StorageService {

  private final S3Client s3Client;
  private final String bucketName;

  @Autowired
  public StorageService(
      @Value("${aws.region}") String awsRegion, @Value("${aws.bucketName}") String bucketName) {
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
      return response.eTag();

    } catch (IOException e) {
      e.printStackTrace();
      return "Failed to upload file. Error: " + e.getMessage();
    }
  }

  public MultipartFile fetchFile(String fileKey, String bucketName) {
    //    MultipartFile audioFile = <retrieve file here>;

    //    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
    //            .bucket(bucketName)
    //            .key(fileKey)
    //            .build();
    //
    //    ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
    //    GetObjectResponse objectInputStream = s3Object.response();
    //    InputStreamResource resource = new InputStreamResource(objectInputStream);
    //
    //    return audioFile;
  }
}
