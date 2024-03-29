package com.pahod.music.resourceservice.service;

import com.pahod.music.resourceservice.client.S3StorageClient;
import com.pahod.music.resourceservice.entity.AudioResourceEntity;
import com.pahod.music.resourceservice.entity.StorageType;
import com.pahod.music.resourceservice.exception.ResourceNotFoundException;
import com.pahod.music.resourceservice.repository.ResourceRepository;
import com.pahod.music.resourceservice.web.dto.AudioResource;
import com.pahod.music.resourceservice.web.dto.AudioResourceInfoResponse;
import com.pahod.music.resourceservice.web.dto.AudioResourceSavedResponse;
import com.pahod.music.resourceservice.web.mapper.ResourceMapper;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

  private final ResourceRepository resourceRepository;
  private final S3StorageClient s3StorageClient;
  private final StorageInfoService storageInfoService;
  private final ResourceMapper resourceMapper;

  public AudioResourceSavedResponse uploadAudioResource(MultipartFile audioFile) {
    String fileKey = generateFileKey(audioFile);
    String originalFilename = audioFile.getOriginalFilename();
    String contentType = audioFile.getContentType();
    String bucketName = storageInfoService.getStagingBucket();

    s3StorageClient.storeFile(bucketName, fileKey, audioFile);
    log.debug("File stored with key: {}", fileKey);

    AudioResourceEntity audioResourceEntity =
        AudioResourceEntity.builder()
            .fileName(originalFilename)
            .fileKey(fileKey)
            .bucketName(bucketName)
            .contentType(contentType)
            .build();

    AudioResourceEntity saved = resourceRepository.save(audioResourceEntity);
    log.debug("Resource info saved to DB: {}", saved);

    return resourceMapper.modelToSavedResponse(saved);
  }

  public void resourceProcessed(int resourceId) {
    AudioResourceEntity audioResourceEntity = getAudioResourceEntity(resourceId);

    String bucketName = audioResourceEntity.getBucketName();
    if (!StorageType.STAGING.getValue().equals(bucketName)) {
      String fileKey = audioResourceEntity.getFileKey();
      String permanentBucket = storageInfoService.getPermanentBucket();

      s3StorageClient.moveFile(bucketName, fileKey, permanentBucket, fileKey);

      audioResourceEntity.setBucketName(permanentBucket);
      resourceRepository.save(audioResourceEntity);
    } else {
      log.error("File is not in Staging bucket, but in {}.", bucketName);
    }
  }

  private static String generateFileKey(MultipartFile audioFile) {
    // todo validate key uniqueness
    String originalFilename = audioFile.getOriginalFilename();
    int extensionIndex = Objects.requireNonNull(originalFilename).lastIndexOf(".");
    String name = originalFilename.substring(0, extensionIndex);
    String extension = originalFilename.substring(extensionIndex);

    return name + RandomStringUtils.randomAlphanumeric(10) + extension;
  }

  public AudioResource getResource(int resourceId) {
    AudioResourceEntity audioResourceEntity = getAudioResourceEntity(resourceId);

    String fileKey = audioResourceEntity.getFileKey();
    String bucketName = audioResourceEntity.getBucketName();
    byte[] data = s3StorageClient.fetchFile(fileKey, bucketName);
    AudioResource audioResourceInfoResponse =
        resourceMapper.modelToAudioResponse(audioResourceEntity);
    audioResourceInfoResponse.setData(data);
    return audioResourceInfoResponse;
  }

  public AudioResourceInfoResponse getResourceInfo(int resourceId) {
    AudioResourceEntity audioResourceEntity = getAudioResourceEntity(resourceId);

    return resourceMapper.modelToInfoResponse(audioResourceEntity);
  }

  private AudioResourceEntity getAudioResourceEntity(int resourceId) {
    return resourceRepository
        .findById(resourceId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Couldn't find resource for Id: " + resourceId));
  }

  public List<Integer> deleteResources(List<Integer> idsToDelete) {
    log.debug("IDs to be removed: {}", idsToDelete);
    return idsToDelete.stream()
        .filter(resourceRepository::existsById)
        .peek(resourceRepository::deleteById)
        .toList();
  }
}
