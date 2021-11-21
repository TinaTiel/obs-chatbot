/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.commandservice.action.system;


import com.tinatiel.obschatbot.commandservice.action.Action;
import com.tinatiel.obschatbot.commandservice.actionsequence.ActionSequence;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

/**
 * An Action that allows execution of an action sequence, the sequence being derived
 * either directly or indirectly from the ID of another command.
 */
@With
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@ToString
public class ExecuteSequenceAction implements Action {
  private UUID commandId;
  private ActionSequence actionSequence;
}
