package com.tinatiel.obschatbot.data.system.entity;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettingsEntity, UUID> {
  Optional<SystemSettingsEntity> findByOwner(UUID owner);
}
