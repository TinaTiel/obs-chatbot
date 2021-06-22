package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import java.util.List;
import java.util.UUID;

public class LocalGroupServiceImpl implements LocalGroupService {

  @Override
  public List<LocalGroupDto> findByOwner(UUID ownerId) {
    return null;
  }

  @Override
  public LocalGroupDto save(LocalGroupDto localGroupDto) {
    return null;
  }

  @Override
  public void delete(UUID id) {

  }
}
