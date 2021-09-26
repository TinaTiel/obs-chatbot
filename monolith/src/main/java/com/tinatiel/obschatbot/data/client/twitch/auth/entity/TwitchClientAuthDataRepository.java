package com.tinatiel.obschatbot.data.client.twitch.auth.entity;

import com.tinatiel.obschatbot.data.common.OwnerRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwitchClientAuthDataRepository extends
  JpaRepository<TwitchClientAuthDataEntity, UUID>,
  OwnerRepository<TwitchClientAuthDataEntity> {

}
