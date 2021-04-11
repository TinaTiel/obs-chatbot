package com.tinatiel.obschatbot.core.user.local;

import com.tinatiel.obschatbot.core.user.Platform;
import java.util.Optional;

/**
 * Provides a means to persist, and to later lookup a Local User by their platform and username. We
 * assume a Local User is generally unique by username on a platform.
 */
public interface LocalUserRepository {

  Optional<LocalUser> findByPlatformAndUsername(Platform platform, String username);

  LocalUser save(LocalUser localUser);
}
