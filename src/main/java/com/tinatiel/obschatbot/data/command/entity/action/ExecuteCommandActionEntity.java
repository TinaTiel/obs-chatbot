/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.data.command.entity.action;

import com.tinatiel.obschatbot.core.action.Action;
import com.tinatiel.obschatbot.core.command.Command;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An Action that allows execution of another Command.
 */
@Getter
@Setter
@NoArgsConstructor
public class ExecuteCommandActionEntity extends ActionEntity {

  private UUID target;

}
