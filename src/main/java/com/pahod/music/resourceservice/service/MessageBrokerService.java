package com.pahod.music.resourceservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageBrokerService {

  private final RabbitMQProducer rabbitMQProducer;

  public void notifyFileStored(Integer id) {
    rabbitMQProducer.notifyFileStored(id);
  }

  public String sentMessage(String message) {
    rabbitMQProducer.sendMessage(message);
    return "Message sent: " + message;
  }
}
