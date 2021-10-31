/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.commandservice.dto.action;


import com.tinatiel.obschatbot.commandservice.dto.action.actionsequence.ActionSequence;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * An Action that allows execution of an action sequence, the sequence being derived
 * either directly or indirectly from the ID of another command.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class ExecuteSequenceAction implements Action {
  private UUID sequenceFromCommandId;
  private ActionSequence actionSequence;
}