/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.data.command.entity.action;

import com.tinatiel.obschatbot.data.command.entity.action.ActionEntity.Type;
import java.util.UUID;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An Action that allows execution of another Command.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue(Type.EXECUTE_COMMAND)
public class ExecuteCommandActionEntity extends ActionEntity {

  private UUID target;

}
