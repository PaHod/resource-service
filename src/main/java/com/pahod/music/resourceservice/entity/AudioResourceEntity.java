package com.pahod.music.resourceservice.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "audio_resource")
public class AudioResourceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  private String fileName;

  private String fileType;

  @Lob private byte[] data;
}
