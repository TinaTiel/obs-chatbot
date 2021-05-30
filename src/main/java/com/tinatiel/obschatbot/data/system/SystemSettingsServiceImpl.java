package com.tinatiel.obschatbot.data.system;

import com.tinatiel.obschatbot.core.user.User;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsEntity;
import com.tinatiel.obschatbot.data.system.entity.SystemSettingsRepository;
import com.tinatiel.obschatbot.data.system.mapper.SystemSettingsMapper;
import com.tinatiel.obschatbot.data.system.model.SystemSettingsDto;

public class SystemSettingsServiceImpl implements SystemSettingsService {

  private final SystemSettingsRepository repository;
  private final SystemSettingsMapper mapper;

  public SystemSettingsServiceImpl(
    SystemSettingsRepository repository,
    SystemSettingsMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public SystemSettingsDto save(SystemSettingsDto dto) {
    return null;
  }

  @Override
  public SystemSettingsDto getForSystem() {
    SystemSettingsEntity entity = repository.findByOwner(User.SYSTEM_PRINCIPAL_NAME)
      .orElse(new SystemSettingsEntity());
    return mapper.map(entity);
  }
}
