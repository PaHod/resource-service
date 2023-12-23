package com.pahod.music.resourceservice.client;

import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;

@Service
public class SongClient {

  public void saveMetadata(Metadata metadata, Integer id) {

    System.out.println("resource Id: " + id);
    String[] metadataNames = metadata.names();
    for (String name : metadataNames) {
      System.out.println(name + ": " + metadata.get(name));
    }
  }
}
