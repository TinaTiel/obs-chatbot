package com.tinatiel.obschatbot.commandservice.service;

import com.tinatiel.obschatbot.commandservice.dto.CommandArgs;
import com.tinatiel.obschatbot.commandservice.dto.CommandDto;
import com.tinatiel.obschatbot.commandservice.dto.action.Action;
import java.util.List;

/**
 * Takes a command and, using the specified commandArgs, generates a list of
 * actions recursively from the command's action sequences. Note that ExecuteSequence
 * actions should be derefrenced to a list of actions; it is not the responsibility
 * of this service to lookup the actions for a commandId, handle cyclical references, etc.
 */
public interface ActionGeneratorService {
  List<Action> generate(CommandDto commandDto, CommandArgs commandArgs);
}
