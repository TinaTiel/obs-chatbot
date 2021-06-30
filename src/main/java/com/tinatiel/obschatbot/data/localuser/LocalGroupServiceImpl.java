package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupRepository;
import com.tinatiel.obschatbot.data.localuser.mapper.LocalGroupMapper;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    return mapper.map(
      repository.save(mapper.map(localGroupDto))
    );
  }

  @Override
  public void delete(UUID id) {

  }
}
