package com.tinatiel.obschatbot.data.command.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides JPA CRUD operations on Command entities.
 */
@Repository
public interface CommandEntityRepository extends JpaRepository<CommandEntity, UUID> {

  Optional<CommandEntity> findByName(String name);
  List<CommandEntity> findByOwner(UUID owner);
  Optional<CommandEntity> findByNameAndOwner(String name, UUID owner);
}
