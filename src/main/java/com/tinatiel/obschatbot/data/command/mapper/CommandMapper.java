package com.tinatiel.obschatbot.data.command.mapper;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import java.util.UUID;
import org.mapstruct.Mapper;

/**
 * Maps between Command entities (in the data layer) and DTOs (in the presentation layer).
 */
@Mapper(imports = UUID.class)
public interface CommandMapper {

  CommandDto entityToDto(CommandEntity entity);

  CommandEntity dtoToEntity(CommandDto dto);

}
