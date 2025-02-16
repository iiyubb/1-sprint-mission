package discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private UUID id;
    private Instant createdAt;

    private String filename;
    private BinaryContentType type;
    private Long filesize;
    private byte[] bytes;

    // 생성자
    protected BinaryContent() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    public BinaryContent(String filename, BinaryContentType type, Long filesize, byte[] bytes) {
        this();
        this.filename = filename;
        this.type = type;
        this.filesize = filesize;
        this.bytes = bytes;
    }


}
