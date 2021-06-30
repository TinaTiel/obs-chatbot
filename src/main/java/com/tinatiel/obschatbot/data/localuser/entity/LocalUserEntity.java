package com.tinatiel.obschatbot.data.localuser.entity;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.common.IdEntity;
import com.tinatiel.obschatbot.data.common.OwnerEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@NoArgsConstructor
@Data
@Entity
public class LocalUserEntity extends IdEntity {

  @Type(type = "org.hibernate.type.UUIDCharType")
  @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
  private UUID owner;
  private Platform platform;
  private String username;

  @ManyToMany(cascade = {
    CascadeType.PERSIST,
    CascadeType.MERGE
  })
  @JoinTable(name = "local_user_group",
    joinColumns = @JoinColumn(name = "local_user_id"),
    inverseJoinColumns = @JoinColumn(name = "local_group_id")
  )
  private Set<LocalGroupEntity> groups = new HashSet<>();
  private boolean broadcaster;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocalUserEntity entity = (LocalUserEntity) o;
    return owner.equals(entity.owner) && platform == entity.platform && username
      .equals(entity.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(owner, platform, username);
  }
}
