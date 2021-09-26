package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Handles all CRUD behaviors related to UserGroups.
 */
public interface LocalGroupService {
  Optional<LocalGroupDto> findById(UUID id);
  List<LocalGroupDto> findByOwner(UUID ownerId);
  LocalGroupDto save(LocalGroupDto localGroupDto);
  void delete(UUID id);
}
