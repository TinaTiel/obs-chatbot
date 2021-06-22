package com.tinatiel.obschatbot.data.localuser.entity;

import java.util.List;
import java.util.UUID;

public interface LocalGroupRepository {
  List<LocalGroupEntity> findByOwner(UUID owner);
}
