package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.core.user.Platform;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserEntity;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserRepository;
import com.tinatiel.obschatbot.data.localuser.mapper.LocalUserMapper;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;

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
    if(localUserDto.getOwner() == null
      || localUserDto.getPlatform() == null
      || localUserDto.getUsername() == null) {
      throw new IllegalArgumentException("User owner, platform, and username are required");
    }
    LocalUserEntity entity = repository.save(mapper.map(localUserDto));
    localUserDto.getGroups().forEach(it -> {
      if(it.getOwner() == null || it.getName() == null) {
        throw new IllegalArgumentException("Group owner and name are required");
      }
//      entity.getGroups().add(mapper.map(it));
    });
    return mapper.map(
      repository.saveAndFlush(entity)
    );
  }

  @Override
  public void delete(UUID id) {

  }
}
