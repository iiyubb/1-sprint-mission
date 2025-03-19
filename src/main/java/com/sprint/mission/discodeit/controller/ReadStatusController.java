package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  @PostMapping
  public ResponseEntity<ReadStatusDto> createReadStatus(
      @RequestBody CreateReadStatusRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(readStatusService.create(request));
  }

  @GetMapping
  public ResponseEntity<List<ReadStatusDto>> getReadStatusByUserId(
      @RequestParam("userId") UUID userId) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatusService.findAllByUserId(userId));
  }

  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusDto> updateReadStatus(
      @PathVariable("readStatusId") UUID readStatusId,
      @RequestBody UpdateReadStatusRequest request) {
    ReadStatusDto readStatus = readStatusService.update(readStatusId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(readStatus);
  }

}
