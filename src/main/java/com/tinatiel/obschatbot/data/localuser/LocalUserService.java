package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides a means to persist, and to later lookup a Local User by their platform and username. We
 * assume a Local User is generally unique by username on a platform.
 */
public interface LocalUserService {

  List<LocalUserDto> findByOwner(UUID ownerId);

  Optional<LocalUserDto> findByOwnerAndPlatformAndUsername(UUID ownerId, Platform platform, String username);

  Optional<LocalUserDto> findBroadcasterForOwnerAndPlatform(UUID ownerId,
    Platform platform);

  LocalUserDto save(LocalUserDto localUserDto);

  void delete(UUID id);

}
