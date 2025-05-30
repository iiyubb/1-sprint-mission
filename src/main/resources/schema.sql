-- 테이블
-- User
CREATE TABLE users
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    username   varchar(50) UNIQUE       NOT NULL,
    email      varchar(100) UNIQUE      NOT NULL,
    password   varchar(60)              NOT NULL,
    profile_id uuid,
    role       varchar(100)             NOT NULL
);

-- BinaryContent
CREATE TABLE binary_contents
(
    id           uuid PRIMARY KEY,
    created_at   timestamp with time zone NOT NULL,
    file_name    varchar(255)             NOT NULL,
    size         bigint                   NOT NULL,
    content_type varchar(100)             NOT NULL
);

-- Channel
CREATE TABLE channels
(
    id          uuid PRIMARY KEY,
    created_at  timestamp with time zone NOT NULL,
    updated_at  timestamp with time zone,
    name        varchar(100),
    description varchar(500),
    type        varchar(10)              NOT NULL
);

-- Message
CREATE TABLE messages
(
    id         uuid PRIMARY KEY,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone,
    content    text,
    channel_id uuid                     NOT NULL,
    author_id  uuid
);

-- Message.attachments
CREATE TABLE message_attachments
(
    message_id    uuid,
    attachment_id uuid,
    PRIMARY KEY (message_id, attachment_id)
);

-- ReadStatus
CREATE TABLE read_statuses
(
    id           uuid PRIMARY KEY,
    created_at   timestamp with time zone NOT NULL,
    updated_at   timestamp with time zone,
    user_id      uuid                     NOT NULL,
    channel_id   uuid                     NOT NULL,
    last_read_at timestamp with time zone NOT NULL,
    UNIQUE (user_id, channel_id)
);

CREATE TABLE jwt_sessions
(
    id                       UUID PRIMARY KEY,
    user_id                  UUID                     NOT NULL,
    access_token             TEXT                     NOT NULL,
    refresh_token            TEXT                     NOT NULL,
    access_token_expires_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    refresh_token_expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_active                BOOLEAN                  NOT NULL DEFAULT true,
    created_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- 외래키 제약조건
    CONSTRAINT fk_jwt_sessions_user_id
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- 성능 최적화를 위한 인덱스들
CREATE INDEX idx_jwt_sessions_user_id ON jwt_sessions (user_id);
CREATE INDEX idx_jwt_sessions_access_token ON jwt_sessions (access_token);
CREATE INDEX idx_jwt_sessions_refresh_token ON jwt_sessions (refresh_token);
CREATE INDEX idx_jwt_sessions_is_active ON jwt_sessions (is_active);
CREATE INDEX idx_jwt_sessions_expires_at ON jwt_sessions (access_token_expires_at, refresh_token_expires_at);

-- 사용자당 하나의 활성 세션만 허용하는 부분 유니크 인덱스
CREATE UNIQUE INDEX idx_jwt_sessions_user_active
    ON jwt_sessions (user_id)
    WHERE is_active = true;

-- updated_at 자동 업데이트를 위한 트리거 함수
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- updated_at 자동 업데이트 트리거
CREATE TRIGGER update_jwt_sessions_updated_at
    BEFORE UPDATE
    ON jwt_sessions
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- 제약 조건
-- User (1) -> BinaryContent (1)
ALTER TABLE users
    ADD CONSTRAINT fk_user_binary_content
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL;

-- Message (N) -> Channel (1)
ALTER TABLE messages
    ADD CONSTRAINT fk_message_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE;

-- Message (N) -> Author (1)
ALTER TABLE messages
    ADD CONSTRAINT fk_message_user
        FOREIGN KEY (author_id)
            REFERENCES users (id)
            ON DELETE SET NULL;

-- MessageAttachment (1) -> BinaryContent (1)
ALTER TABLE message_attachments
    ADD CONSTRAINT fk_message_attachment_binary_content
        FOREIGN KEY (attachment_id)
            REFERENCES binary_contents (id)
            ON DELETE CASCADE;

-- ReadStatus (N) -> User (1)
ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_status_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

-- ReadStatus (N) -> User (1)
ALTER TABLE read_statuses
    ADD CONSTRAINT fk_read_status_channel
        FOREIGN KEY (channel_id)
            REFERENCES channels (id)
            ON DELETE CASCADE;