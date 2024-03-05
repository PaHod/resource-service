package com.pahod.music.resourceservice.service;

import com.pahod.music.resourceservice.client.StorageInfoClient;
import com.pahod.music.resourceservice.entity.StorageInfo;
import com.pahod.music.resourceservice.entity.StorageType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageInfoService {

  private final StorageInfoClient storageInfoClient;

  public String getStagingBucket() {
    return getStorageInfo(StorageType.STAGING).getBucket();
  }

  public String getPermanentBucket() {
    return getStorageInfo(StorageType.PERMANENT).getBucket();
  }

  private StorageInfo getStorageInfo(StorageType storageType) {
    Optional<StorageInfo> optionalStorageInfo =
        storageInfoClient.getAllStorageTypes().stream()
            .filter(info -> storageType.equals(info.getStorageType()))
            .findFirst();

    return optionalStorageInfo.orElseThrow(
        () ->
            new StringIndexOutOfBoundsException(
                "Couldn't find storage info for type: " + storageType));
  }
}
