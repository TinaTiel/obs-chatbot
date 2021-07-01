package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.data.localuser.entity.LocalGroupRepository;
import com.tinatiel.obschatbot.data.localuser.entity.LocalUserRepository;
import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserGroupAssignmentDto;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
public class LocalUserAssignmentServiceImpl implements LocalUserAssignmentService {

  private final LocalUserRepository localUserRepository;
  private final LocalGroupRepository localGroupRepository;

  public LocalUserAssignmentServiceImpl(
    LocalUserRepository localUserRepository,
    LocalGroupRepository localGroupRepository) {
    this.localUserRepository = localUserRepository;
    this.localGroupRepository = localGroupRepository;
  }

  @Override
  public void addAssignment(LocalUserGroupAssignmentDto assignmentDto) {
    validate(assignmentDto);

    localUserRepository.findById(assignmentDto.getLocalUserId()).ifPresentOrElse(
      foundUser -> {
        localGroupRepository.findById(assignmentDto.getLocalGroupId()).ifPresentOrElse(
          foundGroup -> {
            foundUser.getGroups().add(foundGroup);
            foundGroup.getUsers().add(foundUser);
            localUserRepository.saveAndFlush(foundUser);
          },
          () -> {
            // do nothing
          }
        );
      },
      () -> {
        // do nothing
      }
    );
  }

  @Override
  public void removeAssignment(LocalUserGroupAssignmentDto assignmentDto) {
    validate(assignmentDto);

    localUserRepository.findById(assignmentDto.getLocalUserId()).ifPresentOrElse(
      foundUser -> {
        localGroupRepository.findById(assignmentDto.getLocalGroupId()).ifPresentOrElse(
          foundGroup -> {
            foundUser.getGroups().remove(foundGroup);
            foundGroup.getUsers().remove(foundUser);
            localUserRepository.saveAndFlush(foundUser);
          },
          () -> {
            // do nothing
          }
        );
      },
      () -> {
        // do nothing
      }
    );

  }

  private void validate(LocalUserGroupAssignmentDto dto) {
    if(dto.getLocalUserId() == null || dto.getLocalGroupId() == null) {
      throw new IllegalArgumentException("User and Group id are required");
    }
  }
}
