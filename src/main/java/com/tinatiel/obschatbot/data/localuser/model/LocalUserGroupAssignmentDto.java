package com.tinatiel.obschatbot.data.localuser.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LocalUserGroupAssignmentDto {
  UUID localUserId;
  UUID localGroupId;
}
