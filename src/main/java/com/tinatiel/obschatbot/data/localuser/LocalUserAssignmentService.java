package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.data.localuser.model.LocalGroupDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserDto;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserGroupAssignmentDto;
import java.util.List;
import java.util.UUID;

public interface LocalUserAssignmentService {
  List<LocalUserDto> findUsersByGroup(UUID localGroupId);
  List<LocalGroupDto> findGroupsByUser(UUID localUserId);
  void addAssignment(LocalUserGroupAssignmentDto assignmentDto);
  void removeAssignment(LocalUserGroupAssignmentDto assignmentDto);
}
