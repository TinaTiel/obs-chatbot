package com.tinatiel.obschatbot.data.localuser;

import com.tinatiel.obschatbot.data.localuser.model.LocalUserGroupAssignmentDto;

public interface LocalUserAssignmentService {
  void addAssignment(LocalUserGroupAssignmentDto assignmentDto);
  void removeAssignment(LocalUserGroupAssignmentDto assignmentDto);
}
