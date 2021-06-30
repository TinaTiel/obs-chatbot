package com.tinatiel.obschatbot.data.localuser.entity;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalGroupRepository extends JpaRepository<LocalGroupEntity, UUID> {
  List<LocalGroupEntity> findByOwner(UUID owner);
}
