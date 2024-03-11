package com.pahod.music.resourceservice.client;

import com.pahod.music.resourceservice.entity.StorageInfo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@Retry(name = "storageInfoClient")
public class StorageInfoClient {

  private final String apiURI;
  private final WebClient webClient;
  private final DummyStorageInfoData dummyStorageInfoData;

  public StorageInfoClient(
      @Value("${client.services.storage.endpoint}") String apiURI,
      WebClient.Builder webClientBuilder,
      ReactorLoadBalancerExchangeFilterFunction filterFunction,
      DummyStorageInfoData dummyStorageInfoData) {
    this.apiURI = apiURI;
    this.dummyStorageInfoData = dummyStorageInfoData;
    this.webClient = webClientBuilder.filter(filterFunction).build();
  }

  @CircuitBreaker(name = "getAllStorageTypes", fallbackMethod = "getStorageInfoFallback")
  public List<StorageInfo> getAllStorageTypes() {
    log.info("Get storage types");
    return webClient
        .get()
        .uri(apiURI)
        .retrieve()
        .bodyToFlux(StorageInfo.class)
        .collectList()
        .block();
  }

  public List<StorageInfo> getStorageInfoFallback(Throwable t) {
    log.error(
        "Failed to call getAllStorageTypes with error: {}, use fallback method.", t.getMessage());
    return dummyStorageInfoData.getStorageInfoFallback();
  }
}
