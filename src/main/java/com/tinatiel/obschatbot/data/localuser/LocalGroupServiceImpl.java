package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.data.error.DataPersistenceException;
import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupRepository;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserRepository;
import com.tinatiel.obschatbot.data.localuser.mapper.LocalGroupMapper;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

@Transactional
@Slf4j
public class LocalGroupServiceImpl implements LocalGroupService {

  private final LocalGroupRepository repository;
  private final LocalGroupMapper mapper;

  public LocalGroupServiceImpl(
    LocalGroupRepository repository,
    LocalGroupMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public Optional<LocalGroupDto> findById(UUID id) {
    return repository.findById(id).flatMap(it -> Optional.of(mapper.map(it)));
  }

  @Override
  public List<LocalGroupDto> findByOwner(UUID ownerId) {
    return repository.findByOwner(ownerId).stream()
      .map(mapper::map)
      .collect(Collectors.toList());
  }

  @Override
  public LocalGroupDto save(LocalGroupDto localGroupDto) {
    if(localGroupDto == null
      || localGroupDto.getOwner() == null
      || localGroupDto.getName() == null
      || localGroupDto.getName().trim().isEmpty()
    ) {
      throw new IllegalArgumentException("Owner and Name are required");
    }
    try {
      return mapper.map(
        repository.saveAndFlush(mapper.map(localGroupDto))
      );
    } catch (Throwable e) {
      if (e instanceof DataIntegrityViolationException
        && e.getCause() != null
        && e.getCause() instanceof ConstraintViolationException
        && ((ConstraintViolationException) e.getCause()).getConstraintName()
        .contains("UNIQUE_GROUP")) {
        throw new DataPersistenceException("Group already has that name", e);
      }
      throw new DataPersistenceException("Could not save group, unexpected error", e);
    }
  }

  @Override
  public void delete(UUID id) {
    repository.findById(id).ifPresentOrElse(
      found -> {
        log.debug("Deleting group with id " + id);
        // Update the references
        found.getUsers().forEach(user -> user.getGroups().remove(found));
        found.getUsers().clear();

        // Delete
        repository.delete(found);
        repository.flush();
        log.debug("Group deleted");
      },
      () -> {
        log.debug("No group found with id " + id);
        // do nothing
      }
    );
  }

}
