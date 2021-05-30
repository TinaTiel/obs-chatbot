package com.tinatiel.obschatbot.data.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An user-presentable representation of an user group.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GroupDto {
  private String name;
}
