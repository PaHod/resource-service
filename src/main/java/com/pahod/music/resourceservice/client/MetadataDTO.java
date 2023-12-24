package com.pahod.music.resourceservice.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tika.metadata.Metadata;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataDTO {

  private String name;
  private String artist;
  private String album;
  private String length;
  private Integer resourceId;
  private String year;

  public static MetadataDTO fromMetadata(Metadata metadata, Integer resourceId) {
   /*
    content type: audio/mpeg
    resource Id: 15
    xmpDM:genre: Electronic
    xmpDM:album: Rock the Party
    xmpDM:releaseDate: 2021
    xmpDM:artist:
    dc:creator:
    xmpDM:audioCompressor: MP3
    xmpDM:audioChannelType: Stereo
    version: MPEG 3 Layer III Version 1
    xmpDM:logComment: All Rights Reserved to www.A
    xmpDM:audioSampleRate: 44100
    channels: 2
    dc:title: Twerk by Rock the Party
    xmpDM:duration: 99.81580352783203
    Content-Type: audio/mpeg
    samplerate: 44100
    */

    return MetadataDTO.builder()
        .name(metadata.get("dc:title"))
        .artist(metadata.get("xmpDM:artist"))
        .album(metadata.get("xmpDM:album"))
        .length(metadata.get("xmpDM:duration"))
        .resourceId(resourceId)
        .year(metadata.get("xmpDM:releaseDate"))
        .build();
  }
}
