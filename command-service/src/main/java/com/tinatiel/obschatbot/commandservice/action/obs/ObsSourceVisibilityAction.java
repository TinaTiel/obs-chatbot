/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.commandservice.action.obs;

import com.tinatiel.obschatbot.commandservice.action.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * An Action that either hides or shows a specified Source in OBS (an image, audio source, etc.).
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
public class ObsSourceVisibilityAction implements Action {
  private String sceneName;
  private String sourceName;
  private boolean visible;
}