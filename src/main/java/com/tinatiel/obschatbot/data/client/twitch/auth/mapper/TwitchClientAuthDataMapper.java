package com.tinatiel.obschatbot.data.client.twitch.auth.mapper;

import com.tinatiel.obschatbot.data.client.twitch.auth.entity.TwitchClientAuthDataEntity;
import com.tinatiel.obschatbot.data.client.twitch.auth.model.TwitchClientAuthDataDto;
import org.mapstruct.Mapper;

@Mapper
public interface TwitchClientAuthDataMapper {

  TwitchClientAuthDataEntity map(TwitchClientAuthDataDto dto);
  TwitchClientAuthDataDto map(TwitchClientAuthDataEntity entity);

}
