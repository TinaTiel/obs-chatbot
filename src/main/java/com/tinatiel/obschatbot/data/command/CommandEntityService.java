package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import com.tinatiel.obschatbot.data.command.model.CommandDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandEntityService {
  CommandDto save(CommandDto commandEntity);
  Optional<CommandDto> findById(UUID id);
  Optional<CommandDto> findByName(String name);
  List<CommandDto> findAll();
  void delete(UUID id);
}
