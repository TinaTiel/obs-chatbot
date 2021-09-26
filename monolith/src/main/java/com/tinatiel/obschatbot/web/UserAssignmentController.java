package com.tinatiel.obschatbot.web;

import com.tinatiel.obschatbot.data.localuser.LocalUserAssignmentService;
import com.tinatiel.obschatbot.data.localuser.model.LocalUserGroupAssignmentDto;
import com.tinatiel.obschatbot.security.owner.OwnerService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = WebConfig.BASE_PATH + "/user-assignment")
@RestController
@RequiredArgsConstructor
public class UserAssignmentController {
  private final LocalUserAssignmentService localUserAssignmentService;

  @PutMapping
  ResponseEntity<Void> saveAssignment(@RequestBody LocalUserGroupAssignmentDto assignmentDto) {
    localUserAssignmentService.addAssignment(assignmentDto);
    return ResponseEntity.ok(null);
  }

  @DeleteMapping
  ResponseEntity<Void> deleteAssignment(@Valid @RequestBody LocalUserGroupAssignmentDto assignmentDto) {
    localUserAssignmentService.removeAssignment(assignmentDto);
    return ResponseEntity.ok(null);
  }

}
