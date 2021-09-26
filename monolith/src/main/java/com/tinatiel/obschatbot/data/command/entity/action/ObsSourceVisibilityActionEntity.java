/*
 * Copyright (c) 2020 TinaTiel. This file is part of the OBS Chatbot project which is released under
 * GNU General Public License v3.0. See LICENSE or go to https://fsf.org/ for more details.
 */

package com.tinatiel.obschatbot.data.command.entity.action;

import com.tinatiel.obschatbot.data.common.ActionType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An Action that either hides or shows a specified Source in OBS (an image, audio source, etc.).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue(ActionType.OBS_SOURCE_VIZ)
public class ObsSourceVisibilityActionEntity extends ActionEntity {

  private String sceneName;
  private String sourceName;
  private boolean visible;

}