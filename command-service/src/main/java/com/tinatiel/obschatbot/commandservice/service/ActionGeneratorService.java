package com.tinatiel.obschatbot.commandservice.service;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.List;

public interface ActionGeneratorService {
  List<Action> generate(CommandDto commandDto, CommandArgs commandArgs);
}
