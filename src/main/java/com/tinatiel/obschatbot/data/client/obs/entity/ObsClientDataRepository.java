package com.tinatiel.obschatbot.data.client.obs.entity;

import com.tinatiel.obschatbot.data.common.OwnerRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObsClientDataRepository extends
  JpaRepository<ObsClientSettingsEntity, UUID>,
  OwnerRepository<ObsClientSettingsEntity> {
}
