package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserRepository;
import com.tinatiel.obschatbot.data.localuser.mapper.LocalUserMapper;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

@Slf4j
@Transactional // we are always pulling user's groups
public class LocalUserServiceImpl implements LocalUserService {

  private final LocalUserRepository repository;
  private final LocalUserMapper mapper;

  public LocalUserServiceImpl(
    LocalUserRepository repository,
    LocalUserMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Optional<LocalUserDto> findById(UUID id) {
    return repository.findById(id).flatMap(it -> Optional.of(mapper.map(it)));
  }

  @Override
  public List<LocalUserDto> findByOwner(UUID ownerId) {
    return repository.findByOwner(ownerId).stream()
      .map(mapper::map)
      .collect(Collectors.toList());
  }

  @Override
  public Optional<LocalUserDto> findByOwnerAndPlatformAndUsername(UUID ownerId,
    Platform platform, String username) {
    return repository.findByOwnerAndPlatformAndUsername(ownerId, platform, username)
      .flatMap(it -> Optional.of(mapper.map(it)));
  }

  @Override
  public Optional<LocalUserDto> findBroadcasterForOwnerAndPlatform(UUID ownerId,
    Platform platform) {
    return repository.findByOwnerAndPlatformAndBroadcasterTrue(ownerId, platform).stream()
      .map(mapper::map)
      .findFirst();
  }

  @Override
  public LocalUserDto save(LocalUserDto localUserDto) {
    if(localUserDto.getOwner() == null
      || localUserDto.getPlatform() == null
      || localUserDto.getUsername() == null
      || localUserDto.getUsername().trim().isEmpty()
    ) {
      throw new IllegalArgumentException("User owner, platform, and username are required");
    }

    // Verify no broadcaster already exists for this platform
    List<LocalUserEntity> existingBroadcasters = repository.findByOwnerAndPlatformAndBroadcasterTrue(
      localUserDto.getOwner(), localUserDto.getPlatform());
    if(!existingBroadcasters.isEmpty() && localUserDto.isBroadcaster()) {
      log.debug("Found existing broadcasters: " + existingBroadcasters);
      throw new DataPersistenceException("Can only have one broadcaster per platform");
    }

    // Try to save
    try {
      return mapper.map(
        repository.saveAndFlush(repository.save(mapper.map(localUserDto)))
      );
    } catch (Exception e) {
      if(e instanceof DataIntegrityViolationException
        && e.getCause() != null
        && e.getCause() instanceof ConstraintViolationException
        && ((ConstraintViolationException) e.getCause()).getConstraintName().contains("UNIQUE_USER")) {
        throw new DataPersistenceException("User on platform already has that username", e);
      }
      throw new DataPersistenceException("Could not save user, unexpected error", e);
    }

  }

  @Override
  public void delete(UUID id) {
    // Find existing
    repository.findById(id).ifPresentOrElse(
      found -> {
        log.debug("Deleting user with id " + id);

        // update the references
        found.getGroups().forEach(group -> group.getUsers().remove(found));

        // delete
        repository.delete(found);
        repository.flush();
        log.debug("User deleted");
      },
      () -> {
        log.debug("No user found with id " + id);
        // do nothing if not found
      }
    );
  }
}
