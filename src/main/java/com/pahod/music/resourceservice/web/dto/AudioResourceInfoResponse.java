package com.pahod.music.resourceservice.web.dto;

import lombok.Data;

@Data
public class AudioResourceInfoResponse {
  private Integer id;
  private String fileName;
  private String fileKey;
  private String bucketName;
  private String contentType;
}
