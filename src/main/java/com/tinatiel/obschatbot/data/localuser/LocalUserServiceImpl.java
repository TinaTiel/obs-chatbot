package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LocalUserServiceImpl implements LocalUserService {

  @Override
  public List<LocalUserDto> findByOwner(UUID ownerId) {
    return null;
  }

  @Override
  public Optional<LocalUserDto> findByOwnerAndPlatformAndUsername(UUID ownerId,
    Platform platform, String username) {
    return Optional.empty();
  }

  @Override
  public Optional<LocalUserDto> findBroadcasterForOwnerAndPlatform(UUID ownerId,
    Platform platform) {
    return Optional.empty();
  }

  @Override
  public LocalUserDto save(LocalUserDto localUserDto) {
    return null;
  }

  @Override
  public void delete(UUID id) {

  }
}
