package com.sprint.mission.discodeit.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class MultipartFileConverter {

  public byte[] toByteArray(MultipartFile multipartFile) {
    try {
      return multipartFile.getBytes();
    } catch (IOException e) {
      throw new RuntimeException("파일을 byte 배열로 변환하는 데 실패했습니다.");
    }
  }

  public MultipartFile toMultipartFile(byte[] byteArray) {
    return new CustomMultipartFile(byteArray);
  }
}