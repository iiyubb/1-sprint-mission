package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping(value = "/{id}")
  public ResponseEntity<BinaryContentDto> findBinaryContent(@PathVariable UUID id) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(binaryContentService.findById(id));
  }

  @GetMapping(value = "")
  public ResponseEntity<List<BinaryContentDto>> findBinaryContents(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIdList) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(binaryContentService.findAllByIdIn(binaryContentIdList));
  }

  @GetMapping("{id}/download")
  public ResponseEntity<Resource> getBinaryContent(@PathVariable UUID id) {
    return binaryContentStorage.download(binaryContentService.findById(id));
  }
}
