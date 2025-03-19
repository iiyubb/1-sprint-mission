package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepo;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  public BinaryContentDto create(MultipartFile multipartFile) {
    BinaryContent binaryContent = BinaryContent.of(
        multipartFile.getOriginalFilename(),
        multipartFile.getSize(),
        multipartFile.getContentType());

    return binaryContentMapper.toDto(binaryContentRepo.save(binaryContent));
  }

  @Override
  public BinaryContentDto findById(UUID binaryContentId) {
    return binaryContentRepo.findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("[error] 해당하는 바이너리 컨텐츠 ID가 존재하지 않습니다."));
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    return binaryContentRepo.findAllByIdIn(binaryContentIds)
        .stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Override
  public void delete(UUID binaryContentId) {
    if (!binaryContentRepo.existsById(binaryContentId)) {
      throw new NoSuchElementException("[error] 해당하는 바이너리 컨텐츠 ID가 존재하지 않습니다.");
    }
    binaryContentRepo.deleteById(binaryContentId);
  }
}
