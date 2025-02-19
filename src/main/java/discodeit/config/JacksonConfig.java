package discodeit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;

public class JacksonConfig {
    public static void main(String[] args) {
        try {
            // ObjectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();

            // JavaTimeModule 등록
            objectMapper.registerModule(new JavaTimeModule());

            // 이제 Instant와 같은 Java 8 날짜/시간 객체를 처리할 수 있습니다.
            // 예시: Instant 객체 직렬화
            Instant now = Instant.now();
            String json = objectMapper.writeValueAsString(now);
            System.out.println("Serialized Instant: " + json);

            // 예시: Instant 객체 역직렬화
            Instant deserializedInstant = objectMapper.readValue(json, Instant.class);
            System.out.println("Deserialized Instant: " + deserializedInstant);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
