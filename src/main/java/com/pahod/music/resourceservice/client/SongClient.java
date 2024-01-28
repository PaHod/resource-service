package com.pahod.music.resourceservice.client;

import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SongClient {

  public static final String LOAD_BALANCER_PREFIX = "lb://";
  private final String songsApiURI;
  private final WebClient webClient;

  public SongClient(
      @Value("${song.service.endpoint}") String songsApiURI,
      @Value("${song.service.name}") String serviceName,
      WebClient.Builder webClientBuilder) {
    this.songsApiURI = songsApiURI;
    this.webClient = webClientBuilder.baseUrl(LOAD_BALANCER_PREFIX + serviceName).build();
  }

  public void saveMetadata(Metadata metadata, Integer audioResourceId) {

    MetadataDTO metadataDTO = MetadataDTO.fromMetadata(metadata, audioResourceId);

    webClient
        .post()
        .uri(songsApiURI)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(metadataDTO)
        .retrieve()
        .bodyToMono(Void.class)
        .block();
  }
}
