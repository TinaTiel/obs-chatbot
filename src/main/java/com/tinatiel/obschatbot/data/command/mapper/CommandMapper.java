package com.tinatiel.obschatbot.data.command.mapper;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import org.mapstruct.Mapper;

@Mapper
public interface CommandMapper {

  CommandDto entityToDto(CommandEntity entity);
  CommandEntity dtoToEntity(CommandDto dto);

}
