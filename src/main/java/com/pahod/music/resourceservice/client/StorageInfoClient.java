package com.pahod.music.resourceservice.client;

import com.pahod.music.resourceservice.entity.StorageInfo;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class StorageInfoClient {

  private final String apiURI;
  private final WebClient webClient;

  public StorageInfoClient(
      @Value("${client.services.storage.endpoint}") String apiURI,
      WebClient.Builder webClientBuilder,
      ReactorLoadBalancerExchangeFilterFunction filterFunction) {
    this.apiURI = apiURI;
    this.webClient = webClientBuilder.filter(filterFunction).build();
  }

  public List<StorageInfo> getAllStorageTypes() {
    return webClient
        .get()
        .uri(apiURI)
        .retrieve()
        .bodyToFlux(StorageInfo.class)
        .collectList()
        .block();
  }
}
