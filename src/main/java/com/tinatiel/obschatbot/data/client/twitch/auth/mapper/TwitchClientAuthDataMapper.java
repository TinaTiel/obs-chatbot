package com.tinatiel.obschatbot.data.client.twitch.auth.mapper;

import com.tinatiel.obschatbot.data.client.twitch.auth.entity.TwitchClientAuthDataEntity;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientDataDto;
import org.mapstruct.Mapper;

@Mapper
public interface TwitchClientAuthDataMapper {

  TwitchClientAuthDataEntity map(TwitchClientDataDto dto);
  TwitchClientDataDto map(TwitchClientAuthDataEntity entity);

}
