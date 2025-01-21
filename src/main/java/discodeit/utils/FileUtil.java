package discodeit.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {
    // 파일 저장
    public static void init(Path directory) {
        Path parentDirectory = directory.getParent();
        // 저장할 경로의 파일 초기화
        if (!Files.exists(parentDirectory)) {
            try {
                Files.createDirectories(parentDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!Files.exists(directory)) {
            try {
                Files.createFile(directory);
                System.out.println("파일 생성 완료");
            } catch (IOException e) {
                throw new RuntimeException("파일 생성 실패: " + e.getMessage(), e);
            }
        } else {
            try {
                FileWriter writer = new FileWriter(String.valueOf(directory));
                writer.write(""); // 내용을 빈 문자열로 덮어씀
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException("파일 초기화 실패: " + e.getMessage(), e);
            }
        }
    }

    public static <T> void save(Path filePath, Map<String, T> data) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // JSON 파일 쓰기
            mapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), data);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Map<String, T> load(Path directory) {
        try {
            File jsonFile = directory.toFile();
            if (jsonFile.length() == 0) {
                System.out.println("파일이 비어 있습니다.");
                return new HashMap<>();
            } else {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(jsonFile, new TypeReference<Map<String, T>>(){});
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 로드 실패: " + e.getMessage(), e);
        }
    }
}
