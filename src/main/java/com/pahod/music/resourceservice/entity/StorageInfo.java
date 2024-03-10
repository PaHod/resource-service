package com.pahod.music.resourceservice.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageInfo {

  private Integer id;
  private StorageType storageType;
  private String bucket;
  private String path;
}
