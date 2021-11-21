package com.tinatiel.obschatbot.commandservice.command;

import com.tinatiel.obschatbot.commandservice.actionsequence.ActionSequence;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@With
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommandDto {
  private UUID id;
  private UUID owner;
  private String name;
  private ActionSequence actionSequence;
}
