package com.tinatiel.obschatbot.data.command;

import com.tinatiel.obschatbot.data.command.model.CommandDto;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides CRUD and search operations on commands against a database.
 */
public interface CommandEntityService {

  CommandDto save(CommandDto commandEntity) throws DataPersistenceException;

  Optional<CommandDto> findById(UUID id);

  Optional<CommandDto> findByName(String name);

  List<CommandDto> findAll();

  void delete(UUID id) throws DataPersistenceException;
}
