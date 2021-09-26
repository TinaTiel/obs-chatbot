package com.tinatiel.obschatbot.data.command.mapper.executable;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.data.command.model.action.ActionDto;

public interface ExecutableActionMapper {
  Action map(ActionDto dto);
}
