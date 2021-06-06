package com.tinatiel.obschatbot.data.client.twitch.mapper;

import com.tinatiel.obschatbot.data.client.twitch.entity.TwitchClientDataEntity;
import com.tinatiel.obschatbot.data.client.twitch.model.TwitchClientDataDto;
import org.mapstruct.Mapper;

@Mapper
public interface TwitchClientDataMapper {

  TwitchClientDataEntity map(TwitchClientDataDto dto);
  TwitchClientDataDto map(TwitchClientDataEntity entity);

}
