package com.tinatiel.obschatbot.data.localuser.entity;

import com.tinatiel.obschatbot.data.common.IdEntity;
import com.tinatiel.obschatbot.data.common.OwnerEntity;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

@NoArgsConstructor
@Data
@Table(
  uniqueConstraints = @UniqueConstraint(name = "UNIQUE_GROUP", columnNames = {"owner", "name"})
)
@Entity
public class LocalGroupEntity extends IdEntity {

  @Type(type = "org.hibernate.type.UUIDCharType")
  @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
  private UUID owner;
  private String name;

  @ToString.Exclude
  @ManyToMany(mappedBy = "groups")
  private Set<LocalUserEntity> users = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocalGroupEntity that = (LocalGroupEntity) o;
    return owner.equals(that.owner) && name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(owner, name);
  }
}
