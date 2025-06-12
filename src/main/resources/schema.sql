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
    id            uuid PRIMARY KEY,
    created_at    timestamp with time zone NOT NULL,
    file_name     varchar(255)             NOT NULL,
    size          bigint                   NOT NULL,
    content_type  varchar(100)             NOT NULL,
    upload_status varchar(20)              NOT NULL DEFAULT 'WAITING'
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
    id                   uuid PRIMARY KEY,
    created_at           timestamp with time zone NOT NULL,
    updated_at           timestamp with time zone,
    user_id              uuid                     NOT NULL,
    channel_id           uuid                     NOT NULL,
    last_read_at         timestamp with time zone NOT NULL,
    notification_enabled BOOLEAN                  NOT NULL DEFAULT TRUE,
    UNIQUE (user_id, channel_id)
);

-- jwt_sessions
CREATE TABLE jwt_sessions
(
    id              UUID PRIMARY KEY,
    user_id         UUID                     NOT NULL,
    access_token    TEXT                     NOT NULL,
    refresh_token   TEXT                     NOT NULL,
    expiration_time TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE,

    UNIQUE (access_token),
    UNIQUE (refresh_token)
);

-- 성능 최적화를 위한 인덱스들
CREATE INDEX idx_jwt_sessions_user_id ON jwt_sessions (user_id);
CREATE INDEX idx_jwt_sessions_expiration_time ON jwt_sessions (expiration_time);
CREATE INDEX idx_jwt_sessions_user_expiration ON jwt_sessions (user_id, expiration_time);

-- async_task_failures
CREATE TABLE async_task_failures
(
    id                UUID primary key,
    request_id        VARCHAR(255)             NOT NULL,
    task_type         VARCHAR(100)             NOT NULL,
    binary_content_id UUID                     NULL,
    error_message     TEXT                     NULL,
    retry_count       INT                      NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE
);

-- notifications
CREATE TABLE notifications
(
    id          UUID primary key,
    receiver_id UUID                     NOT NULL,
    type        VARCHAR(50)              NOT NULL,
    target_id   UUID,
    title       VARCHAR(255),
    content     TEXT,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_receiver_created ON notifications (receiver_id, created_at DESC);

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