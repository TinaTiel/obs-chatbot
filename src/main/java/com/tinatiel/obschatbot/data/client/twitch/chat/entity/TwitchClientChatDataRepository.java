package com.tinatiel.obschatbot.data.client.twitch.chat.entity;

import com.tinatiel.obschatbot.data.common.OwnerRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwitchClientChatDataRepository extends
  JpaRepository<TwitchClientChatDataEntity, UUID>,
  OwnerRepository<TwitchClientChatDataEntity> {

}
