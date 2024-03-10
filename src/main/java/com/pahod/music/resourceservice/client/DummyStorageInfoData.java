package com.pahod.music.resourceservice.client;

import static com.pahod.music.resourceservice.entity.StorageType.PERMANENT;
import static com.pahod.music.resourceservice.entity.StorageType.STAGING;

import com.pahod.music.resourceservice.entity.StorageInfo;
import com.pahod.music.resourceservice.entity.StorageType;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DummyStorageInfoData {

  // TODO extract values to configuration
  private StorageType storageTypeStaging = STAGING;
  private String bucketStaging = "music-storage-staging";
  private String pathStaging = "staging path";
  private StorageType storageTypePermanent = PERMANENT;
  private String bucketPermanent = "music-storage-permanent";
  private String pathPermanent = "permanent path";

  public List<StorageInfo> getStorageInfoFallback() {

    StorageInfo stagingStorage =
        StorageInfo.builder()
            .id(1)
            .storageType(storageTypeStaging)
            .bucket(bucketStaging)
            .path(pathStaging)
            .build();

    StorageInfo permanentStorage =
        StorageInfo.builder()
            .id(2)
            .storageType(storageTypePermanent)
            .bucket(bucketPermanent)
            .path(pathPermanent)
            .build();

    return List.of(stagingStorage, permanentStorage);
  }
}
