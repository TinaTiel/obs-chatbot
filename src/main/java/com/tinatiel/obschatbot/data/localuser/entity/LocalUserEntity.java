package com.tinatiel.obschatbot.data.localuser.entity;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.common.OwnerEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LocalUserEntity extends OwnerEntity {
  private Platform platform;
  private String username;
  private List<LocalGroupEntity> groups = new ArrayList<>();
  private boolean broadcaster;
}
