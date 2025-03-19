package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "channel")
public class Channel extends BaseUpdatableEntity {

  private String name;

  @Enumerated(EnumType.STRING)
  @NotNull
  private ChannelType channelType;

  private String description;

  public void update(String newName, String newDescription) {
    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
    }
  }

  public boolean isPrivate() {
    return this.channelType == ChannelType.PRIVATE;
  }

  public boolean isPublic() {
    return this.channelType == ChannelType.PUBLIC;
  }

}