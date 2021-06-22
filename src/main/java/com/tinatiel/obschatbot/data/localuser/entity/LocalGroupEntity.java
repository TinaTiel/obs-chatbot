package com.tinatiel.obschatbot.data.localuser.entity;

import com.tinatiel.obschatbot.data.common.IdEntity;
import com.tinatiel.obschatbot.data.common.OwnerEntity;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
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

}
