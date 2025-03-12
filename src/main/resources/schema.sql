-- users 테이블
CREATE TABLE users
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    profile_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_profile FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE SET NULL
);

-- Enum 타입 생성
CREATE TYPE channel_type AS ENUM('PUBLIC', 'PRIVATE');

-- channels 테이블
CREATE TABLE channels
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100),
    description VARCHAR(500),
    type        channel_type NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- messages 테이블
CREATE TABLE messages
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content text,
    channel_id UUID NOT NULL,
    author_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_message_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE ,
    CONSTRAINT fk_message_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

-- user_statuses 테이블
CREATE TABLE user_statuses
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_active_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_status_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)

-- read_statuses 테이블
CREATE TABLE read_statuses
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE,
    channel_id UUID UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_read_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_read_statuses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_read_statuses_channel FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    CONSTRAINT uk_read_statuses UNIQUE (user_id, channel_id)
)

-- binary_contents 테이블
CREATE TABLE binary_contents
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    file_name VARCHAR(255) NOT NULL ,
    size BIGINT NOT NULL ,
    content_type VARCHAR(100) NOT NULL ,
    bytes BYTEA NOT NULL
)

-- message_attachments 테이블
CREATE TABLE message_attachments
(
    message_id UUID,
    attachment_id UUID,
    CONSTRAINT fk_message_attachment_message FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE ,
    CONSTRAINT fk_message_attachment_id FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
)