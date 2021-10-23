package com.tinatiel.obschatbot.commandservice.dto;

import com.tinatiel.obschatbot.commandservice.dto.actionsequence.ActionSequence;
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
