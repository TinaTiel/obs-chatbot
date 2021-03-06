/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.data.command.model.action;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * An Action that allows execution of another Command.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ExecuteCommandActionDto extends ActionDto {

  private UUID target;

}
