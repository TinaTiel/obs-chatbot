package com.tinatiel.obschatbot.data.common;

import com.fasterxml.jackson.databind.ser.Serializers.Base;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Setter
@Getter
@MappedSuperclass
public class OwnedEntity extends BaseEntity {

  @Type(type = "org.hibernate.type.UUIDCharType")
  @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
  private UUID owner;// = SystemOwnerService.SYSTEM_ID;

}
