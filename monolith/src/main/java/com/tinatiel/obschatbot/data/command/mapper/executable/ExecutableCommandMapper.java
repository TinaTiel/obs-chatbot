package com.tinatiel.obschatbot.data.command.mapper.executable;

import com.tinatiel.obschatbot.core.command.Command;
import com.tinatiel.obschatbot.data.command.model.CommandDto;

public interface ExecutableCommandMapper {

  Command map(CommandDto dto);

}
