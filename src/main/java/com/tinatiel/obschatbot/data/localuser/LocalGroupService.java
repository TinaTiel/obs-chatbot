package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import java.util.List;
import java.util.UUID;

/**
 * Handles all CRUD behaviors related to UserGroups.
 */
public interface LocalGroupService {
  List<LocalGroupDto> findByOwner(UUID ownerId);
  LocalGroupDto save(LocalGroupDto localGroupDto);
  void delete(UUID id);
}
