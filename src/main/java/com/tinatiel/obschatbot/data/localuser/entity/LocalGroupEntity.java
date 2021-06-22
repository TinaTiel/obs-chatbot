package com.tinatiel.obschatbot.data.localuser.entity;

import com.tinatiel.obschatbot.data.common.OwnerEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LocalGroupEntity extends OwnerEntity {
  private String name;
}
