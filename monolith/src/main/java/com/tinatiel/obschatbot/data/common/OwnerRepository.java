package com.tinatiel.obschatbot.data.common;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository<T> {
  Optional<T> findByOwner(UUID owner);
}
