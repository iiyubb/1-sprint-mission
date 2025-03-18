package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message extends BaseUpdatableEntity {

  @ManyToOne()
  @JoinColumn(name = "author_id")
  private User author;

  @ManyToOne()
  @JoinColumn(name = "channel_id")
  @NotNull
  private Channel channel;

  private String content;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(User author, Channel channel, String content) {
    super();
  }

  public void update(String newDetail) {
    if (!newDetail.equals(this.content)) {
      this.content = newDetail;
    }
  }

  public void addAttachment(BinaryContent attachment) {
    if (attachment == null) {
      throw new IllegalArgumentException(); // custom exception
    }
    this.attachments.add(attachment);
    this.updateUpdatedAt();
  }

  public void deleteAttachment(BinaryContent attachment) {
    if (attachment == null) {
      throw new IllegalArgumentException();
    }
    this.attachments.remove(attachment);
    this.updateUpdatedAt();
  }

}
