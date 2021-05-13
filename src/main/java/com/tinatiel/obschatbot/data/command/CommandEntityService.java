package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.data.command.entity.CommandEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommandEntityService {
  CommandEntity save(CommandEntity commandEntity);
  Optional<CommandEntity> findByName(String name);
  List<CommandEntity> findAll();
  void delete(UUID id);
}
