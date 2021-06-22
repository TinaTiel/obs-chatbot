package com.tinatiel.obschatbot.data.localuser.entity;

import com.tinatiel.obschatbot.core.user.Platform;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalUserRepository extends JpaRepository<LocalUserEntity, UUID> {
  List<LocalUserEntity> findByOwner(UUID owner);
  Optional<LocalUserEntity> findByOwnerAndPlatformAndUsername(UUID owner, Platform platform, String username);
  List<LocalUserEntity> findByOwnerAndPlatformAndBroadcasterTrue(UUID owner, Platform platform);
}
