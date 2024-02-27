package com.pahod.music.resourceservice.service;

import org.springframework.stereotype.Service;

@Service
public class RabbitService {
  public static final String BINDING_NAME_NEW_AUDIO_FILE_UPLOADED = "new-audio-file-uploaded";

  //  private final StreamBridge streamBridge;

  public void notifyFileStored(Integer id) {
    //    streamBridge.send(BINDING_NAME_NEW_AUDIO_FILE_UPLOADED, audioResourceResponse.getId());

  }

  public String sentMessage(String message) {
    //    streamBridge.send("new-audio-file-uploaded", message);
    return "Message sent";
  }
}
