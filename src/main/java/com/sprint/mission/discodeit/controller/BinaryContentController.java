package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> getBinaryContent(
      @PathVariable("binaryContentId") UUID contentId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContentService.find(contentId));
  }

  @GetMapping
  public ResponseEntity<List<BinaryContent>> getAllBinaryContent(
      @RequestBody List<UUID> contentIds) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContentService.findAllByIdIn(contentIds));
  }
}
