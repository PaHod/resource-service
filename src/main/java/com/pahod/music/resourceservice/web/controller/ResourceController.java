package com.pahod.music.resourceservice.web.controller;

import com.pahod.music.resourceservice.entity.AudioResourceEntity;
import com.pahod.music.resourceservice.service.ResourceService;
import com.pahod.music.resourceservice.web.dto.AudioResourceResponse;
import com.pahod.music.resourceservice.web.dto.DeletedResourcesIDs;
import com.pahod.music.resourceservice.web.mapper.ResourceMapper;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

  private final ResourceService resourceService;
  private final ResourceMapper resourceMapper;

  @GetMapping("/ping")
  public ResponseEntity<?> pingPong() {
    return ResponseEntity.ok("resource pong");
  }

  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<?> uploadAudioResource(@RequestParam("file") MultipartFile file) {
    String validationResult = validateAudioFile(file);
    if (!validationResult.isEmpty()) {
      return ResponseEntity.badRequest().body(validationResult);
    }

    AudioResourceEntity resource = resourceService.uploadAudioResource(file);
    AudioResourceResponse audioResourceResponse = resourceMapper.modelToResponse(resource);
    return ResponseEntity.ok(audioResourceResponse);
  }

  @GetMapping("/{resourceId}")
  public ResponseEntity<byte[]> getResource(@PathVariable("resourceId") int resourceId) {
    log.debug("Get resource ID: {}", resourceId);
    AudioResourceEntity resource = resourceService.getResource(resourceId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
    headers.setContentLength(resource.getData().length);
    headers.set("Content-Disposition", "inline; filename=\"" + resource.getFileName() + "\"");

    return new ResponseEntity<>(resource.getData(), headers, HttpStatus.OK);
  }

  @DeleteMapping("/resources")
  public ResponseEntity<DeletedResourcesIDs> deleteResources(@RequestParam String idsParam) {
    log.debug("Delete resources IDs: {}", idsParam);

    if (idsParam.length() >= 200)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID length exceeds limit");

    List<Integer> idsToDelete = Arrays.stream(idsParam.split(",")).map(Integer::parseInt).toList();

    List<Integer> idsOfRemoved = resourceService.deleteResources(idsToDelete);
    return ResponseEntity.ok(new DeletedResourcesIDs(idsOfRemoved));
  }

  private String validateAudioFile(MultipartFile file) {
    if (file.isEmpty()) {
      return "Error: File is empty.";
    }
    System.out.println("content type: " + file.getContentType());
    if (!"audio/mpeg".equals(file.getContentType())) {
      return "File has wrong content type. Should be audio/mpeg";
    }

    String fileName = file.getOriginalFilename();
    if (fileName == null || fileName.isEmpty()) {
      return "Error: Empty file name.";
    }
    log.debug("Validated audio file with name: {}", fileName);

    return "";
  }
}
