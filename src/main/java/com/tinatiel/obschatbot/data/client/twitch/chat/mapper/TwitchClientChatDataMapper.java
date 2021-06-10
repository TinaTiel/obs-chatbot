package com.tinatiel.obschatbot.data.client.twitch.chat.mapper;

import com.tinatiel.obschatbot.data.client.twitch.chat.entity.TwitchClientChatDataEntity;
import com.tinatiel.obschatbot.data.client.twitch.chat.model.TwitchClientChatDataDto;
import org.mapstruct.Mapper;

@Mapper
public interface TwitchClientChatDataMapper {

  TwitchClientChatDataEntity map(TwitchClientChatDataDto dto);
  TwitchClientChatDataDto map(TwitchClientChatDataEntity entity);

}
