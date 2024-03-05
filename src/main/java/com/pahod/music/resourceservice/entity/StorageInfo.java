package com.pahod.music.resourceservice.entity;

import lombok.Data;

@Data
public class StorageInfo {

  private Integer id;
  private StorageType storageType;
  private String bucket;
  private String path;
}
