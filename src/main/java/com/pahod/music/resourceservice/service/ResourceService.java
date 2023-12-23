package com.pahod.music.resourceservice.service;

import com.pahod.music.resourceservice.client.SongClient;
import com.pahod.music.resourceservice.entity.AudioResourceEntity;
import com.pahod.music.resourceservice.exception.FileParsingException;
import com.pahod.music.resourceservice.exception.ResourceNotFoundException;
import com.pahod.music.resourceservice.repository.ResourceRepository;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.LyricsHandler;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

  private final ResourceRepository resourceRepository;
  private final SongClient songClient;

  private static Metadata parseMetadata(MultipartFile audioFile) {
    Mp3Parser mp3Parser = new Mp3Parser();
    BodyContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    ParseContext parseContext = new ParseContext();

    try (InputStream inputStream = audioFile.getInputStream()) {
      mp3Parser.parse(inputStream, handler, metadata, parseContext);

      LyricsHandler lyrics = new LyricsHandler(inputStream, handler);
      while (lyrics.hasLyrics()) {
        System.out.println(lyrics);
      }
    } catch (IOException | SAXException | TikaException e) {
      throw new FileParsingException("Failed to parse metadata.");
    }

    return metadata;
  }

  public AudioResourceEntity uploadAudioResource(MultipartFile audioFile) {
    AudioResourceEntity audioResourceEntity = getAudioResourceEntity(audioFile);
    AudioResourceEntity saved = resourceRepository.save(audioResourceEntity);
    log.debug("Saved audio file to DB: {}", saved);

    Metadata metadata = parseMetadata(audioFile);
    songClient.saveMetadata(metadata, audioResourceEntity.getId());

    return saved;
  }

  private AudioResourceEntity getAudioResourceEntity(MultipartFile audioFile) {
    try {
      AudioResourceEntity audioResourceEntity = new AudioResourceEntity();
      audioResourceEntity.setFileName(audioFile.getOriginalFilename());
      audioResourceEntity.setFileType(audioFile.getContentType());
      audioResourceEntity.setData(audioFile.getBytes());
      return audioResourceEntity;
    } catch (IOException e) {
      throw new FileParsingException("Failed to get file content.");
    }
  }

  public AudioResourceEntity getResource(int id) {
    return resourceRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Couldn't find resource for Id: " + id));
  }

  public AudioResourceEntity updateResource(AudioResourceEntity audioResourceEntity) {
    return null;
  }

  public void deleteResource(int id) {}
}
