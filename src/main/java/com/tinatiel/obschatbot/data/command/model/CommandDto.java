package com.tinatiel.obschatbot.data.command.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CommandDto {
  private UUID id;
  private String name;
  private boolean disabled;
}
