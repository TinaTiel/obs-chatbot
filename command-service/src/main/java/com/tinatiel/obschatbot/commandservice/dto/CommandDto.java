package com.tinatiel.obschatbot.commandservice.dto;

import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.ActionSequence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommandDto {
  private String name;
  private ActionSequence actionSequence;
}
