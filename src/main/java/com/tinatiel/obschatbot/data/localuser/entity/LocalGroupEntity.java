package com.tinatiel.obschatbot.data.localuser.entity;

import com.tinatiel.obschatbot.data.common.IdEntity;
import com.tinatiel.obschatbot.data.common.OwnerEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@NoArgsConstructor
@Data
@Entity
public class LocalGroupEntity extends IdEntity {

  @Type(type = "org.hibernate.type.UUIDCharType")
  @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
  private UUID owner;
  private String name;

  @ManyToMany(cascade = {
    CascadeType.PERSIST,
    CascadeType.MERGE
  })
  @JoinTable(name = "local_user_group",
    joinColumns = @JoinColumn(name = "local_user_id"),
    inverseJoinColumns = @JoinColumn(name = "local_group_id")
  )
  private Set<LocalUserEntity> users = new HashSet<>();
}
