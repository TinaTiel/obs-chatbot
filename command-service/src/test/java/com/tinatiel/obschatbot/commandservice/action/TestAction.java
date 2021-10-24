package com.tinatiel.obschatbot.commandservice.action;

import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TestAction implements Action {
  private final UUID id = UUID.randomUUID();
}
