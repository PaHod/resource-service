package com.pahod.music.resourceservice.service;

import com.pahod.music.resourceservice.client.StorageService;
import com.pahod.music.resourceservice.entity.AudioResourceEntity;
import com.pahod.music.resourceservice.exception.FileParsingException;
import com.pahod.music.resourceservice.exception.ResourceNotFoundException;
import com.pahod.music.resourceservice.repository.ResourceRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.LyricsHandler;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

  private final ResourceRepository resourceRepository;
  private final StorageService storageService;

  public AudioResourceEntity uploadAudioResource(MultipartFile audioFile) {
    String fileKey = generateFileKey(audioFile);
    String originalFilename = audioFile.getOriginalFilename();
    String contentType = audioFile.getContentType();

    String bucketName = storageService.storeFile(audioFile, fileKey);
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

    return saved;
  }
  @NotNull
  private static String generateFileKey(MultipartFile audioFile) {
    // todo validate key uniqueness
    return audioFile.getOriginalFilename() + RandomStringUtils.randomAlphanumeric(10);
  }

  public AudioResourceEntity getResource(int resourceId) {
    AudioResourceEntity audioResourceEntity =
        resourceRepository
            .findById(resourceId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Couldn't find resource for Id: " + resourceId));

    String fileName = audioResourceEntity.getFileName();
    String fileKey = audioResourceEntity.getFileKey();
    String bucketName = audioResourceEntity.getBucketName();
    String contentType = audioResourceEntity.getContentType();

    storageService.fetchFile(fileKey, storageService.bucketName);

    return audioResourceEntity;
  }

  public List<Integer> deleteResources(List<Integer> idsToDelete) {
    log.debug("IDs to be removed: {}", idsToDelete);
    return idsToDelete.stream()
        .filter(resourceRepository::existsById)
        .peek(resourceRepository::deleteById)
        .toList();
  }
}
