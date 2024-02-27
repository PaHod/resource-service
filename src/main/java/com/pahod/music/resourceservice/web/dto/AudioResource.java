package com.pahod.music.resourceservice.web.dto;

import lombok.Data;

@Data
public class AudioResource {

  private Integer id;
  private String fileName;
  private String fileKey;
  private String bucketName;
  private String contentType;
  private byte[] data;
}
