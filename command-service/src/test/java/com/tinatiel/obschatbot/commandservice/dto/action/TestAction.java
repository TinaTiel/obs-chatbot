package com.tinatiel.obschatbot.commandservice.dto.action;

import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TestAction implements Action {
  private final UUID id = UUID.randomUUID();
}
